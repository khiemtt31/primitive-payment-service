package model;

import constants.BillStatus;
import java.time.LocalDate;

public class Bill {
    private final Long id;
    private final String type;
    private final Double amount;
    private final LocalDate dueDate;
    private BillStatus state; // This is the only mutable field
    private final String provider;

    public Bill(Long id, String type, Double amount, LocalDate dueDate, String provider) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.dueDate = dueDate;
        this.provider = provider;
        this.state = BillStatus.NOT_PAID;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public BillStatus getState() {
        return state;
    }

    public String getProvider() {
        return provider;
    }

    public void setState(BillStatus state) {
        this.state = state;
    }
}