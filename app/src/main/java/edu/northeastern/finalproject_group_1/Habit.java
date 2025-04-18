package edu.northeastern.finalproject_group_1;

import static java.util.Objects.isNull;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Habit {
    private String title;
    private String description;
    private boolean isCompleted;
    private int iconResId;
    private String schedule;
    private int reward = 50;
    private String customIconUri;
    private int customColor;
    private String repeatUnit;
    private int every;
    private List<Integer> weekdays;
    //private Calendar startDate;
    //private Calendar endDate;
    private long startDateMillis;
    private long endDateMillis;
    private List<String> reminderTimes;
    private String habitKey;
    private long lastCompletedMillis = -1;
    private int totalCompleted;


    public Habit() {}

    public Habit(String title, String description, boolean completed, int iconResId, String schedule, int reward, String customIconUri, int customColor, String repeatUnit, int every, List<Integer> weekdays,
                 long startDate, long endDate, List<String> reminderTimes, String habitKey, long lastCompletedDate, int totalCompleted) {
        this.title = title;
        this.description = description;
        this.isCompleted = completed;
        this.iconResId = iconResId;
        this.schedule = schedule;
        this.reward = reward;
        this.customIconUri = customIconUri;
        this.customColor = customColor;
        this.repeatUnit = repeatUnit;
        this.every = every;
        this.weekdays = weekdays;
        this.startDateMillis = startDate;
        this.endDateMillis = endDate;
        this.reminderTimes = reminderTimes;
        this.habitKey = habitKey;
        this.lastCompletedMillis = lastCompletedDate;
        this.totalCompleted = totalCompleted;
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak) {
        this(title, description, completed, iconResId, repeatOption, streak, null, 0, "Daily", 1, new ArrayList<>(), -1, -1, null, null, -1, 0);
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak, String customIconUri) {
        this(title, description, completed, iconResId, repeatOption, streak, customIconUri, 0, "Daily", 1, new ArrayList<>(), -1, -1, null, null, -1, 0);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return isCompleted; }
    public int getIconResId() {
        return iconResId != 0 ? iconResId : IconData.getDefaultIcon();
    }
    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
    public String getSchedule() { return schedule; }
    public int getReward() { return reward; }
    public String getCustomIconUri() {
        return (customIconUri != null && !customIconUri.isEmpty()) ? customIconUri : null;
    }
    public int getCustomColor() { return customColor; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setCustomColor(int customColor) { this.customColor = customColor; }

    public String getRepeatUnit() { return repeatUnit; }
    public void setRepeatUnit(String repeatUnit) { this.repeatUnit = repeatUnit; }

    public int getEvery() { return every; }
    public void setEvery(int every) { this.every = every; }

    public List<Integer> getWeekdays() {
        if (!isNull(weekdays)) {
            return weekdays;
        }
        weekdays = new ArrayList<>();
        return weekdays;
    }
    public void setWeekdays(List<Integer> weekdays) { this.weekdays = weekdays; }

    /*public Calendar getStartDate() { return startDate; }
    public void setStartDate(Calendar startDate) { this.startDate = startDate; }

    public Calendar getEndDate() { return endDate; }
    public void setEndDate(Calendar endDate) { this.endDate = endDate; }*/
    public long getStartDateMillis() {
        return startDateMillis;
    }
    public void setStartDateMillis(long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public List<String> getReminderTimes() {
        if(!isNull(reminderTimes)){
            return reminderTimes;
        } else {
            reminderTimes = new ArrayList<>();
            return reminderTimes;
        }
    }
    public void setReminderTimes(List<String> reminderTimes) { this.reminderTimes = reminderTimes; }
    public String getHabitKey(){
        return habitKey;
    }

    public void setHabitKey(String habitKey) {
        this.habitKey = habitKey;
    }

    public void setLastCompletedMillis(long lastCompletedMillis) {
        this.lastCompletedMillis = lastCompletedMillis;
    }

    public long getLastCompletedMillis() {
        return this.lastCompletedMillis;
    }

    public int getTotalCompleted() {
        return this.totalCompleted;
    }

    public void setTotalCompleted(int totalCompleted) {
        this.totalCompleted = totalCompleted;
    }

}