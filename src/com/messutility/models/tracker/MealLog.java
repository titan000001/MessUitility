package com.messutility.models.tracker;

import com.messutility.models.users.Resident;
import java.util.Date;

public class MealLog {
    private Resident resident;
    private Date date;
    private int breakfastCount;
    private int lunchCount;
    private int dinnerCount;

    public MealLog(Resident resident, Date date, int breakfastCount, int lunchCount, int dinnerCount) {
        this.resident = resident;
        this.date = date;
        this.breakfastCount = breakfastCount;
        this.lunchCount = lunchCount;
        this.dinnerCount = dinnerCount;
    }

    public Resident getResident() { return resident; }
    public Date getDate() { return date; }
    
    public int getBreakfastCount() { return breakfastCount; }
    public void setBreakfastCount(int breakfastCount) { this.breakfastCount = breakfastCount; }
    
    public int getLunchCount() { return lunchCount; }
    public void setLunchCount(int lunchCount) { this.lunchCount = lunchCount; }
    
    public int getDinnerCount() { return dinnerCount; }
    public void setDinnerCount(int dinnerCount) { this.dinnerCount = dinnerCount; }

    public int getTotalMealsForDay() {
        return breakfastCount + lunchCount + dinnerCount;
    }
}
