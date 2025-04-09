package edu.northeastern.finalproject_group_1;

public class SettingOption {
    private int iconResId;
    private String title;

    public SettingOption(int iconResId, String title) {
        this.iconResId = iconResId;
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }
}