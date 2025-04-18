package edu.northeastern.finalproject_group_1;

import java.util.HashMap;
import java.util.Map;
public class IconData {

    public static final String[] ICON_NAMES = {
            "ic_check",
            "ic_calendar",
            "ic_alarm",
            "ic_bedtime",
            "ic_restaurant",
            "ic_drink_water",
            "ic_running",
            "ic_book",
            "ic_edit",
            "ic_code",
            "ic_school",
            "ic_translate",
            "ic_psychology",
            "ic_fitness",
            "ic_spa",
            "ic_self_improvement",
            "ic_favorite",
            "ic_hospital",
            "ic_brush",
            "ic_music",
            "ic_mic",
            "ic_camera",
            "ic_palette",
            "ic_game",
            "ic_movie",
            "ic_social",
            "ic_chat",
            "ic_travel",
            "ic_park",
            "ic_eco"
    };

    private static final Map<String, Integer> ICON_NAME_TO_RES_ID = new HashMap<>();

    static {
        for (String name : ICON_NAMES) {
            int resId = 0;
            try {
                resId = R.drawable.class.getField(name).getInt(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
            ICON_NAME_TO_RES_ID.put(name, resId);
        }
    }

    public static int getResIdByName(String iconName) {
        return ICON_NAME_TO_RES_ID.getOrDefault(iconName, R.drawable.ic_favorite);
    }


    public static String getDefaultIconName() {
        return "ic_favorite";
    }
}