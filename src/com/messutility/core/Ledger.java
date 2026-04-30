package com.messutility.core;

import com.messutility.models.expenses.Expense;
import com.messutility.models.expenses.MealExpense;
import com.messutility.models.users.Resident;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ledger {
    private List<Expense> expenses;
    private List<Resident> residents;
    private MealTracker mealTracker;
    private List<MonthlyBill> generatedBills;
    private Resident houseFund; // Dummy account to balance penalty fees

    public Ledger(MealTracker mealTracker) {
        this.expenses = new ArrayList<>();
        this.residents = new ArrayList<>();
        this.mealTracker = mealTracker;
        this.generatedBills = new ArrayList<>();
        this.houseFund = new Resident("SYS_FUND", "System House Fund", "N/A", "N/A");
    }

    public void addResident(Resident r) {
        this.residents.add(r);
    }

    public void addExpense(Expense e) {
        this.expenses.add(e);
    }

    public void closeMonthAndCalculate(Date cycleDueDate) {
        // 1. Calculate total meal cost and total meals consumed globally
        double totalMealCost = 0.0;
        int totalMealsConsumed = mealTracker.getTotalSystemMeals();

        for (Expense e : expenses) {
            if (e instanceof MealExpense && e.isApproved()) {
                totalMealCost += e.getAmount();
            }
        }

        double costPerMeal = totalMealsConsumed > 0 ? totalMealCost / totalMealsConsumed : 0.0;
        System.out.println("System calculated Cost Per Meal: $" + String.format("%.2f", costPerMeal));

        // 2. Distribute Meal Costs to each resident dynamically via MealTracker
        for (Resident r : residents) {
            int residentMeals = mealTracker.getTotalMealsForResident(r);
            double mealOwed = residentMeals * costPerMeal;
            r.addOwed(mealOwed);
        }

        // 3. Process all expenses (Utilities, Custom, and apply credits for Meals)
        for (Expense e : expenses) {
            if (e.isApproved()) {
                e.calculateAndAssignSplit(residents, costPerMeal);
            }
        }

        // 4. Generate Monthly Bills for Debtors
        for (Resident r : residents) {
            double netBalance = r.getNetBalance();
            if (netBalance < -0.01) {
                // If Net Balance is negative, they owe money to the house
                MonthlyBill bill = new MonthlyBill(r, Math.abs(netBalance), new Date(), cycleDueDate);
                generatedBills.add(bill);
            }
        }
    }

    public void applyLatePenalties(double penaltyAmount) {
        for (MonthlyBill bill : generatedBills) {
            // We use the bill's internal logic, but we must manually credit the House Fund 
            // so the Double-Entry Accounting remains balanced to exactly $0.00
            double amountBefore = bill.getAmountDue();
            bill.applyLateFee(penaltyAmount);
            double amountAfter = bill.getAmountDue();
            
            if (amountAfter > amountBefore) { // Penalty was successfully applied
                houseFund.addPaid(penaltyAmount); 
            }
        }
    }

    public List<Transaction> generateSettlement() {
        List<Resident> allEntities = new ArrayList<>(residents);
        if (houseFund.getNetBalance() > 0.01) {
            allEntities.add(houseFund);
        }
        return SettlementEngine.calculateSettlement(allEntities);
    }

    public List<Resident> getResidents() { return residents; }
    public List<MonthlyBill> getGeneratedBills() { return generatedBills; }
    public Resident getHouseFund() { return houseFund; }
}
