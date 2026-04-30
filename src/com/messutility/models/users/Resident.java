package com.messutility.models.users;

public class Resident extends User {
    private double totalPaid; // Money paid out of pocket for the hostel
    private double totalOwed; // Money owed based on expense calculations

    public Resident(String id, String name, String contact, String password) {
        super(id, name, contact, password);
        this.totalPaid = 0.0;
        this.totalOwed = 0.0;
    }

    public double getTotalPaid() { return totalPaid; }
    public void addPaid(double amount) { this.totalPaid += amount; }

    public double getTotalOwed() { return totalOwed; }
    public void addOwed(double amount) { this.totalOwed += amount; }

    public double getNetBalance() {
        return totalPaid - totalOwed;
    }
}
