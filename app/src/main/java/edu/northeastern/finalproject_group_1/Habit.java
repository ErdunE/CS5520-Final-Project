package edu.northeastern.finalproject_group_1;

public class Habit {
    private String title;
    private String description;
    private boolean isCompleted;
    private int icon;
    private String schedule;
    private int reward;
    private String customIconUri;

    public Habit() {}

    public Habit(String title, String description, boolean isCompleted, int icon, String schedule, int reward, String customIconUri) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.icon = icon;
        this.schedule = schedule;
        this.reward = reward;
        this.customIconUri = customIconUri;
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak) {
        this(title, description, completed, iconResId, repeatOption, streak, null);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return isCompleted; }
    public int getIcon() { return icon; }
    public String getSchedule() { return schedule; }
    public int getReward() { return reward; }
    public String getCustomIconUri() {
        return customIconUri;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }
}