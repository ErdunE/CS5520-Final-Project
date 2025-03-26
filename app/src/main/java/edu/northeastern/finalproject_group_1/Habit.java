package edu.northeastern.finalproject_group_1;

import java.util.Calendar;
import java.util.List;

public class Habit {
    private String title;
    private String description;
    private boolean isCompleted;
    private int icon;
    private String schedule;
    private int reward;
    private String customIconUri;
    private int customColor;
    private String repeatUnit;
    private int every;
    private List<Integer> weekdays;
    private Calendar startDate;
    private Calendar endDate;
    private List<String> reminderTimes;


    public Habit() {}

    public Habit(String title, String description, boolean isCompleted, int icon, String schedule, int reward, String customIconUri, int customColor, String repeatUnit, int every, List<Integer> weekdays,
                 Calendar startDate, Calendar endDate, List<String> reminderTimes) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.icon = icon;
        this.schedule = schedule;
        this.reward = reward;
        this.customIconUri = customIconUri;
        this.customColor = customColor;
        this.repeatUnit = repeatUnit;
        this.every = every;
        this.weekdays = weekdays;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reminderTimes = reminderTimes;
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak) {
        this(title, description, completed, iconResId, repeatOption, streak, null, 0, "Daily", 1, null, null, null, null);
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak, String customIconUri) {
        this(title, description, completed, iconResId, repeatOption, streak, customIconUri, 0, "Daily", 1, null, null, null, null);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return isCompleted; }
    public int getIcon() { return icon; }
    public String getSchedule() { return schedule; }
    public int getReward() { return reward; }
    public String getCustomIconUri() { return customIconUri; }
    public int getCustomColor() { return customColor; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setCustomColor(int customColor) { this.customColor = customColor; }

    public String getRepeatUnit() { return repeatUnit; }
    public void setRepeatUnit(String repeatUnit) { this.repeatUnit = repeatUnit; }

    public int getEvery() { return every; }
    public void setEvery(int every) { this.every = every; }

    public List<Integer> getWeekdays() { return weekdays; }
    public void setWeekdays(List<Integer> weekdays) { this.weekdays = weekdays; }

    public Calendar getStartDate() { return startDate; }
    public void setStartDate(Calendar startDate) { this.startDate = startDate; }

    public Calendar getEndDate() { return endDate; }
    public void setEndDate(Calendar endDate) { this.endDate = endDate; }

    public List<String> getReminderTimes() { return reminderTimes; }
    public void setReminderTimes(List<String> reminderTimes) { this.reminderTimes = reminderTimes; }
}