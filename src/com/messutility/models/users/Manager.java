package com.messutility.models.users;

import com.messutility.core.MealTracker;
import com.messutility.models.expenses.Expense;
import java.util.Date;

public class Manager extends User {
    public Manager(String id, String name, String contact, String password) {
        super(id, name, contact, password);
    }

    // Only a Manager can approve an expense.
    public void approveExpense(Expense e) {
        e.approve();
        System.out.println("Manager " + this.name + " approved expense: " + e.getDescription());
    }

    public void deleteExpense(Expense e) {
        System.out.println("Manager " + this.name + " deleted expense: " + e.getDescription());
    }

    // Strict encapsulation: Only Manager can instruct MealTracker to log or edit meals
    // Because logOrUpdateMeal is protected/package-private in core, 
    // Manager might need to use reflection or MealTracker must expose a public method 
    // that validates the caller is a Manager. For simplicity in OOP, we can expose a 
    // public method in MealTracker that accepts the Manager object as an authorized token.
    public void editResidentMeal(MealTracker tracker, Resident r, Date date, int breakfast, int lunch, int dinner) {
        tracker.logMealAsManager(this, r, date, breakfast, lunch, dinner);
    }
}
