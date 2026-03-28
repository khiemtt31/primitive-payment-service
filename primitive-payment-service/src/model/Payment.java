package model;

import constants.PaymentStatus;
import java.time.LocalDate;

public class Payment {
    private final Long id;
    private final Double amount;
    private final LocalDate paymentDate;
    private PaymentStatus state;
    private final Long billId;

    public Payment(Long id, Double amount, LocalDate paymentDate, PaymentStatus state, Long billId) {
        this.id = id;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.state = state;
        this.billId = billId;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public PaymentStatus getState() {
        return state;
    }

    public Long getBillId() {
        return billId;
    }

    public void setState(PaymentStatus state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("%d. %.0f %s %-10s %d",
                id, amount, paymentDate, state, billId);
    }
}