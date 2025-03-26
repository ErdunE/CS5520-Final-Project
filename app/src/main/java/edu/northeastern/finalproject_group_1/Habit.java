package edu.northeastern.finalproject_group_1;
public class Habit {
    private String title;
    private String description;
    private boolean isCompleted;
    private int icon;
    private String schedule;
    private int reward;
    private String customIconUri;
    private int customColor;

    public Habit() {}

    public Habit(String title, String description, boolean isCompleted, int icon, String schedule, int reward, String customIconUri, int customColor) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.icon = icon;
        this.schedule = schedule;
        this.reward = reward;
        this.customIconUri = customIconUri;
        this.customColor = customColor;
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak) {
        this(title, description, completed, iconResId, repeatOption, streak, null, 0);
    }

    public Habit(String title, String description, boolean completed, int iconResId, String repeatOption, int streak, String customIconUri) {
        this(title, description, completed, iconResId, repeatOption, streak, customIconUri, 0);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return isCompleted; }
    public int getIcon() { return icon; }
    public String getSchedule() { return schedule; }
    public int getReward() { return reward; }
    public String getCustomIconUri() {return customIconUri;}
    public int getCustomColor() { return customColor; }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }
    public void setDescription(String newDescription) {
        this.description = newDescription;
    }
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }
    public void setCustomColor(int customColor) { this.customColor = customColor; }
}