package repository;

import model.Payment;

import java.util.List;

public interface PaymentRepositoryInterface {
    void save(Payment payment);
    List<Payment> findAll();
    List<Payment> findByBillId(Long id);
}