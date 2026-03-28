package repository.impl;

import model.Payment;
import repository.PaymentRepositoryInterface;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PaymentRepository implements PaymentRepositoryInterface {
    
    private final Map<Long, Payment> database = new HashMap<>();

    @Override
    public void save(Payment payment) {
        database.put(payment.getId(), payment);
    }

    @Override
    public List<Payment> findAll() {
        return database.values().stream()
                .sorted(Comparator.comparing(Payment::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByBillId(Long id) {
        return database.values().stream()
                .filter(payment -> id.equals(payment.getBillId()))
                .collect(Collectors.toList());
    }
}