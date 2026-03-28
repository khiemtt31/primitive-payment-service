package service;

import model.Bill;
import model.Payment;

import java.util.List;

public interface PaymentServiceInterface {
    void addFunds(double amount);
    double getBalance();

    List<Bill> listBills();
    List<Bill> searchByProvider(String provider);
    List<Bill> getBillsDue();

    void payBills(List<Long> billIds);
    void schedulePayment(Long billId, String dateStr);
    List<Payment> listPayments();
}
