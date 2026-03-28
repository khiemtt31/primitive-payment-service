package repository;

import model.Bill;

import java.util.List;

public interface BillRepositoryInterface {
    Bill findById(Long id);
    List<Bill> findAll();
    void save(Bill bill);
}