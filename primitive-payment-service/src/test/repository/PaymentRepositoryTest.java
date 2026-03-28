package test.repository;

import constants.PaymentStatus;
import model.Payment;
import repository.impl.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {

    private PaymentRepository repository;

    @BeforeEach
    void setUp() {
        repository = new PaymentRepository();
    }

    @Test
    void testSaveAndFindAll_SortingById() {
        Payment p1 = new Payment(300L, 50.0, LocalDate.now(), PaymentStatus.PROCESSED, 1L);
        Payment p2 = new Payment(100L, 150.0, LocalDate.now(), PaymentStatus.PROCESSED, 2L);

        repository.save(p1);
        repository.save(p2);

        List<Payment> results = repository.findAll();

        assertEquals(2, results.size());
        assertEquals(100L, results.get(0).getId());
        assertEquals(300L, results.get(1).getId());
    }

    @Test
    void testFindByBillId_Filtering() {
        repository.save(new Payment(1L, 100.0, LocalDate.now(), PaymentStatus.PROCESSED, 10L));
        repository.save(new Payment(2L, 200.0, LocalDate.now(), PaymentStatus.PROCESSED, 10L));
        repository.save(new Payment(3L, 300.0, LocalDate.now(), PaymentStatus.PROCESSED, 20L));

        List<Payment> bill10Payments = repository.findByBillId(10L);

        assertEquals(2, bill10Payments.size());
        assertTrue(bill10Payments.stream().allMatch(p -> p.getBillId().equals(10L)));
    }

    @Test
    void testFindByBillId_NoMatch_ReturnsEmptyList() {
        repository.save(new Payment(1L, 100.0, LocalDate.now(), PaymentStatus.PROCESSED, 10L));

        List<Payment> results = repository.findByBillId(999L);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSave_OverwritesExistingPaymentId() {
        Payment p1 = new Payment(1L, 100.0, LocalDate.now(), PaymentStatus.PENDING, 10L);
        repository.save(p1);

        Payment p1Update = new Payment(1L, 100.0, LocalDate.now(), PaymentStatus.PROCESSED, 10L);
        repository.save(p1Update);

        List<Payment> results = repository.findAll();
        assertEquals(1, results.size());
        assertEquals(PaymentStatus.PROCESSED, results.get(0).getState());
    }
}