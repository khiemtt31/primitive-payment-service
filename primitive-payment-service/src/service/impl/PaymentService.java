package service.impl;

import constants.BillStatus;
import constants.PaymentStatus;
import model.Account;
import model.Bill;
import model.Payment;
import repository.impl.BillRepository;
import repository.impl.PaymentRepository;
import service.PaymentServiceInterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentService implements PaymentServiceInterface {
    private final Account account;
    private final BillRepository billRepo;
    private final PaymentRepository paymentRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PaymentService(Account account, BillRepository billRepo, PaymentRepository paymentRepo) {
        this.account = account;
        this.billRepo = billRepo;
        this.paymentRepo = paymentRepo;
    }

    @Override
    public void addFunds(double amount) {
        account.addFunds(amount);
    }

    @Override
    public double getBalance() {
        return account.getBalance();
    }

    @Override
    public void payBills(List<Long> billIds) {
        List<Bill> billsToPay = billIds.stream()
                .map(billRepo::findById)
                .filter(b -> b != null && b.getState() == BillStatus.NOT_PAID)
                .sorted(Comparator.comparing(Bill::getDueDate))
                .toList();

        if (billsToPay.isEmpty()) {
            throw new IllegalArgumentException("Sorry! Not found a bill with such id");
        }

        for (Bill bill : billsToPay) {
            if (account.getBalance() >= bill.getAmount()) {
                account.deductFunds(bill.getAmount());
                bill.setState(BillStatus.PAID);

                paymentRepo.save(new Payment(
                        System.nanoTime(),
                        bill.getAmount(),
                        LocalDate.now(),
                        PaymentStatus.PROCESSED,
                        bill.getId()
                ));
            } else {
                throw new IllegalStateException("Sorry! Not enough fund to proceed with payment.");
            }
        }
    }

    @Override
    public void schedulePayment(Long billId, String dateStr) {
        Bill bill = billRepo.findById(billId);
        if (bill == null) throw new IllegalArgumentException("Bill not found");

        LocalDate scheduledDate = LocalDate.parse(dateStr, formatter);

        paymentRepo.save(new Payment(
                System.currentTimeMillis(),
                bill.getAmount(),
                scheduledDate,
                PaymentStatus.PENDING,
                bill.getId()
        ));
    }

    @Override
    public List<Bill> listBills() {
        return billRepo.findAll();
    }

    @Override
    public List<Bill> searchByProvider(String provider) {
        return billRepo.findAll().stream()
                .filter(b -> b.getProvider().equalsIgnoreCase(provider))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> listPayments() {
        return paymentRepo.findAll();
    }

    @Override
    public List<Bill> getBillsDue() {
        return billRepo.findAll().stream()
                .filter(b -> b.getState() == BillStatus.NOT_PAID)
                .sorted(Comparator.comparing(Bill::getDueDate))
                .collect(Collectors.toList());
    }
}