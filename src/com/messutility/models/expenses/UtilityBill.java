package com.messutility.models.expenses;

import com.messutility.models.users.Resident;
import java.util.Date;
import java.util.List;

public class UtilityBill extends Expense {
    public UtilityBill(String id, Date date, double amount, String description, Resident paidBy, Date dueDate) {
        super(id, date, amount, description, paidBy, dueDate);
    }

    @Override
    public void calculateAndAssignSplit(List<Resident> allResidents, double costPerMeal) {
        if (!isApproved() || allResidents.isEmpty()) return;
        
        double splitAmount = this.amount / allResidents.size();
        for (Resident r : allResidents) {
            r.addOwed(splitAmount);
        }
        
        if (this.paidBy != null) {
            for (Resident r : allResidents) {
                if (r.getId().equals(this.paidBy.getId())) {
                    r.addPaid(this.amount);
                    break;
                }
            }
        }
    }
}
