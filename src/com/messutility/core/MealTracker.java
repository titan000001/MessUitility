package com.messutility.core;

import com.messutility.models.tracker.MealLog;
import com.messutility.models.users.Resident;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MealTracker {
    private List<MealLog> logs;

    public MealTracker() {
        this.logs = new ArrayList<>();
    }

    // This method is now meant to be called ONLY by the Manager
    public void logMealAsManager(com.messutility.models.users.Manager manager, Resident resident, Date date, int breakfast, int lunch, int dinner) {
        if (manager == null) return; // Strict enforcement

        // Check if log already exists for this resident and date
        for (MealLog log : logs) {
            if (log.getResident().equals(resident) && isSameDay(log.getDate(), date)) {
                log.setBreakfastCount(breakfast);
                log.setLunchCount(lunch);
                log.setDinnerCount(dinner);
                return;
            }
        }
        // If not, add new
        logs.add(new MealLog(resident, date, breakfast, lunch, dinner));
    }

    public int getTotalMealsForResident(Resident resident) {
        int total = 0;
        for (MealLog log : logs) {
            if (log.getResident().equals(resident)) {
                total += log.getTotalMealsForDay();
            }
        }
        return total;
    }

    public int getTotalSystemMeals() {
        int total = 0;
        for (MealLog log : logs) {
            total += log.getTotalMealsForDay();
        }
        return total;
    }

    public List<MealLog> getLogsForDate(Date date) {
        List<MealLog> dailyLogs = new ArrayList<>();
        for (MealLog log : logs) {
            if (isSameDay(log.getDate(), date)) {
                dailyLogs.add(log);
            }
        }
        return dailyLogs;
    }

    private boolean isSameDay(Date d1, Date d2) {
        // Simplified same-day logic. In production, use LocalDate.
        return d1.toString().substring(0, 10).equals(d2.toString().substring(0, 10));
    }
}
