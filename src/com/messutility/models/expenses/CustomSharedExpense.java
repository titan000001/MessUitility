package com.messutility.models.expenses;

import com.messutility.models.users.Resident;
import java.util.Date;
import java.util.List;

public class CustomSharedExpense extends Expense {
    private List<Resident> involvedResidents;

    public CustomSharedExpense(String id, Date date, double amount, String description, Resident paidBy, Date dueDate, List<Resident> involved) {
        super(id, date, amount, description, paidBy, dueDate);
        this.involvedResidents = involved;
    }

    @Override
    public void calculateAndAssignSplit(List<Resident> allResidents, double costPerMeal) {
        if (!isApproved() || involvedResidents == null || involvedResidents.isEmpty()) return;
        
        double splitAmount = this.amount / involvedResidents.size();
        for (Resident r : involvedResidents) {
            r.addOwed(splitAmount);
        }
        
        if (this.paidBy != null) {
            this.paidBy.addPaid(this.amount);
        }
    }
}
