package repository.impl;

import model.Bill;
import repository.BillRepositoryInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillRepository implements BillRepositoryInterface {
    private final Map<Long, Bill> database = new HashMap<>();

    @Override
    public Bill findById(Long id) {
        return database.get(id);
    }

    @Override
    public List<Bill> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public void save(Bill bill) {
        database.put(bill.getId(), bill);
    }

    @Override
    public void delete(Long id) { database.remove(id); }
}