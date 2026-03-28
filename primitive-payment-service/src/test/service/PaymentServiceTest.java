package test.service;

import constants.BillStatus;
import constants.PaymentStatus;
import model.Account;
import model.Bill;
import model.Payment;
import repository.impl.BillRepository;
import repository.impl.PaymentRepository;
import service.impl.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private Account account;
    private BillRepository billRepo;
    private PaymentRepository paymentRepo;

    @BeforeEach
    void setUp() {
        account = new Account(1000000.0);
        billRepo = new BillRepository();
        paymentRepo = new PaymentRepository();
        paymentService = new PaymentService(account, billRepo, paymentRepo);
    }

    @Test
    void testPAY01_SuccessfulPayment() {
        Bill bill = new Bill(1L, "ELECTRIC", 200000.0, LocalDate.now(), "EVN");
        billRepo.save(bill);

        paymentService.payBills(List.of(1L));

        assertEquals(800000.0, account.getBalance());
        assertEquals(BillStatus.PAID, bill.getState());
        assertEquals(1, paymentRepo.findAll().size());
        assertEquals(PaymentStatus.PROCESSED, paymentRepo.findAll().get(0).getState());
    }

    @Test
    void testPAY02_PrioritySorting() {
        Bill laterBill = new Bill(1L, "WATER", 100000.0, LocalDate.of(2026, 12, 31), "SAVACO");
        Bill earlierBill = new Bill(2L, "ELECTRIC", 100000.0, LocalDate.of(2026, 1, 1), "EVN");

        billRepo.save(laterBill);
        billRepo.save(earlierBill);

        paymentService.payBills(Arrays.asList(1L, 2L));

        List<Payment> history = paymentRepo.findAll();

        assertEquals(2L, history.get(0).getBillId());
        assertEquals(1L, history.get(1).getBillId());
    }

    @Test
    void testPAY03_PartialSuccessAndInsufficientFunds() {
        account = new Account(300000.0);
        paymentService = new PaymentService(account, billRepo, paymentRepo);

        Bill bill1 = new Bill(1L, "FOOD", 200000.0, LocalDate.of(2026, 1, 1), "GRAB");
        Bill bill2 = new Bill(2L, "RENT", 800000.0, LocalDate.of(2026, 1, 2), "LANDLORD");

        billRepo.save(bill1);
        billRepo.save(bill2);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.payBills(Arrays.asList(1L, 2L));
        });

        assertTrue(exception.getMessage().contains("Not enough fund"));
        assertEquals(BillStatus.PAID, bill1.getState());
        assertEquals(BillStatus.NOT_PAID, bill2.getState());
        assertEquals(100000.0, account.getBalance());
    }

    @Test
    void testPAY04_InvalidBillId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentService.payBills(List.of(99L));
        });

        assertTrue(exception.getMessage().contains("Not found"));
    }

    @Test
    void testPAY05_And_PAY06_SearchProviderCaseInsensitive() {
        billRepo.save(new Bill(1L, "INTERNET", 800000.0, LocalDate.now(), "VNPT"));
        billRepo.save(new Bill(2L, "PHONE", 100000.0, LocalDate.now(), "Viettel"));

        List<Bill> result1 = paymentService.searchByProvider("VNPT");
        assertEquals(1, result1.size());

        List<Bill> result2 = paymentService.searchByProvider("vnpt");
        assertEquals(1, result2.size());
        assertEquals("VNPT", result2.getFirst().getProvider());
    }

    @Test
    void testPAY07_DueDateView() {
        Bill future = new Bill(1L, "E", 100.0, LocalDate.of(2026, 12, 1), "P1");
        Bill soon = new Bill(2L, "W", 100.0, LocalDate.of(2026, 1, 1), "P2");
        Bill paid = new Bill(3L, "I", 100.0, LocalDate.of(2025, 1, 1), "P3");
        paid.setState(BillStatus.PAID);

        billRepo.save(future);
        billRepo.save(soon);
        billRepo.save(paid);

        List<Bill> dueBills = paymentService.getBillsDue();

        assertEquals(2, dueBills.size());
        assertEquals(2L, dueBills.getFirst().getId());
        assertFalse(dueBills.contains(paid));
    }

    @Test
    void testPAY_Coverage_AlreadyPaidFilter() {
        Bill paidBill = new Bill(1L, "NET", 100.0, LocalDate.now(), "VNPT");
        paidBill.setState(BillStatus.PAID);
        billRepo.save(paidBill);

        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.payBills(List.of(1L));
        });
    }

    @Test
    void testPAY_Coverage_NullIdFilter() {
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.payBills(List.of(999L));
        });
    }

    @Test
    void testPAY_Coverage_PartialFundsBranch() {
        account = new Account(100.0);
        paymentService = new PaymentService(account, billRepo, paymentRepo);

        billRepo.save(new Bill(1L, "B1", 100.0, LocalDate.of(2026, 1, 1), "P1"));
        billRepo.save(new Bill(2L, "B2", 100.0, LocalDate.of(2026, 1, 2), "P2"));

        assertThrows(IllegalStateException.class, () -> {
            paymentService.payBills(List.of(1L, 2L));
        });

        assertEquals(BillStatus.PAID, billRepo.findById(1L).getState());
        assertEquals(BillStatus.NOT_PAID, billRepo.findById(2L).getState());
    }

    @Test
    void testSCH01_SchedulePaymentSuccess() {
        billRepo.save(new Bill(1L, "INTERNET", 800000.0, LocalDate.now(), "VNPT"));

        paymentService.schedulePayment(1L, "28/03/2026");

        List<Payment> payments = paymentRepo.findAll();
        assertEquals(1, payments.size());
        assertEquals(PaymentStatus.PENDING, payments.get(0).getState());
        assertEquals(LocalDate.of(2026, 3, 28), payments.get(0).getPaymentDate());
    }

    @Test
    void testSCH02_ScheduleNonExistentBill() {
        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.schedulePayment(999L, "28/03/2026");
        });
    }

    @Test
    void testSCH03_InvalidDateFormat() {
        billRepo.save(new Bill(1L, "INTERNET", 800000.0, LocalDate.now(), "VNPT"));

        assertThrows(DateTimeParseException.class, () -> {
            paymentService.schedulePayment(1L, "2026-03-28");
        });
    }

    @Test
    void testACC_ExactBalancePayment() {
        account = new Account(500.0);
        paymentService = new PaymentService(account, billRepo, paymentRepo);
        billRepo.save(new Bill(1L, "TEST", 500.0, LocalDate.now(), "PROVIDER"));

        assertDoesNotThrow(() -> paymentService.payBills(List.of(1L)));
        assertEquals(0.0, account.getBalance());
    }

    @Test
    void testACC_InvalidFundAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            account.addFunds(-1.0);
        });
    }
}