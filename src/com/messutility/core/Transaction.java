package com.messutility.core;

import com.messutility.models.users.Resident;

public class Transaction {
    private Resident from;
    private Resident to;
    private double amount;

    public Transaction(Resident from, Resident to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Resident getFrom() { return from; }
    public Resident getTo() { return to; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return String.format("%s owes %s $%.2f", from.getName(), to.getName(), amount);
    }
}
