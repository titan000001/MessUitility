package com.messutility.core;

import com.messutility.models.users.Resident;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SettlementEngine {

    public static List<Transaction> calculateSettlement(List<Resident> residents) {
        List<Transaction> transactions = new ArrayList<>();

        // PriorityQueue to always get the largest debtor
        PriorityQueue<ResidentNode> debtors = new PriorityQueue<>(Comparator.comparingDouble(r -> -r.balance));
        // PriorityQueue to always get the largest creditor
        PriorityQueue<ResidentNode> creditors = new PriorityQueue<>(Comparator.comparingDouble(r -> -r.balance));

        for (Resident r : residents) {
            double netBalance = r.getNetBalance();
            if (netBalance < -0.01) {
                // Negative balance means they owe money (Debtor). Store as positive magnitude for queue.
                debtors.add(new ResidentNode(r, Math.abs(netBalance)));
            } else if (netBalance > 0.01) {
                // Positive balance means they are owed money (Creditor).
                creditors.add(new ResidentNode(r, netBalance));
            }
        }

        while (!debtors.isEmpty() && !creditors.isEmpty()) {
            ResidentNode debtor = debtors.poll();
            ResidentNode creditor = creditors.poll();

            double amountToSettle = Math.min(debtor.balance, creditor.balance);
            
            // Round to 2 decimal places to avoid floating point issues
            amountToSettle = Math.round(amountToSettle * 100.0) / 100.0;

            if (amountToSettle > 0) {
                transactions.add(new Transaction(debtor.resident, creditor.resident, amountToSettle));
            }

            debtor.balance -= amountToSettle;
            creditor.balance -= amountToSettle;

            // If they still have remaining balance, push them back to the queue
            if (debtor.balance > 0.01) {
                debtors.add(debtor);
            }
            if (creditor.balance > 0.01) {
                creditors.add(creditor);
            }
        }

        return transactions;
    }

    private static class ResidentNode {
        Resident resident;
        double balance;

        ResidentNode(Resident resident, double balance) {
            this.resident = resident;
            this.balance = balance;
        }
    }

    public static List<com.messutility.core.MonthlyBill> generateMonthlyBills(List<com.messutility.models.expenses.Expense> expenses, List<com.messutility.models.tracker.MealLog> mealLogs, List<Resident> residents) {
        // 1. Calculate Total Meals and aggregate by Resident
        int totalMeals = 0;
        java.util.Map<String, Integer> residentMealCount = new java.util.HashMap<>();
        
        for (com.messutility.models.tracker.MealLog log : mealLogs) {
            int dailyMeals = log.getTotalMealsForDay();
            totalMeals += dailyMeals;
            String resId = log.getResident().getId();
            residentMealCount.put(resId, residentMealCount.getOrDefault(resId, 0) + dailyMeals);
        }

        // 2. Calculate Total Grocery Cost
        double totalGroceryCost = 0.0;
        for (com.messutility.models.expenses.Expense exp : expenses) {
            if (exp instanceof com.messutility.models.expenses.MealExpense && exp.isApproved()) {
                totalGroceryCost += exp.getAmount();
            }
        }

        // 3. Determine Cost Per Meal
        double costPerMeal = totalMeals > 0 ? totalGroceryCost / totalMeals : 0.0;

        // 4. Reset Residents' balances before calculation to ensure idempotency
        for (Resident r : residents) {
            // We shouldn't strictly reset in a real system if past balances exist, 
            // but for this month-end calculation, we assume these are fresh numbers or we handle it in memory
            r.addOwed(-r.getTotalOwed()); // Reset to 0
            r.addPaid(-r.getTotalPaid()); // Reset to 0
            
            // Add meal costs
            int mealsEaten = residentMealCount.getOrDefault(r.getId(), 0);
            r.addOwed(mealsEaten * costPerMeal);
        }

        // 5. Apply All Expenses (This handles Utility Bill splitting and credits the payers)
        for (com.messutility.models.expenses.Expense exp : expenses) {
            exp.calculateAndAssignSplit(residents, costPerMeal);
        }

        // 6. Generate the Monthly Bills
        List<com.messutility.core.MonthlyBill> bills = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 7); // Due in 7 days
        java.util.Date dueDate = cal.getTime();

        for (Resident r : residents) {
            double netBalance = r.getNetBalance();
            // If netBalance < 0, they owe money. If > 0, they are owed money.
            // We represent amountDue as the positive amount they need to pay.
            double amountDue = netBalance < 0 ? Math.abs(netBalance) : 0.0;
            
            com.messutility.core.MonthlyBill bill = new com.messutility.core.MonthlyBill(r, amountDue, new java.util.Date(), dueDate);
            bills.add(bill);
        }

        return bills;
    }
}
