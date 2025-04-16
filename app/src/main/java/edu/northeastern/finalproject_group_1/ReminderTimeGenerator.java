package edu.northeastern.finalproject_group_1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderTimeGenerator {

    public enum RepeatUnit {
        NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    }

    public static List<Calendar> generateReminderTimes(
            Calendar startDate,
            Calendar endDate,
            RepeatUnit unit,
            int every,
            List<Integer> selectedWeekdays,
            List<Calendar> dailyTimes
    ) {
        List<Calendar> result = new ArrayList<>();

        if (unit == RepeatUnit.NONE) return result;

        Calendar current = (Calendar) startDate.clone();

        while (!current.after(endDate)) {
            boolean isValidDay = false;

            switch (unit) {
                case DAILY:
                    isValidDay = true;
                    break;

                case WEEKLY:
                    int dayOfWeek = current.get(Calendar.DAY_OF_WEEK);
                    isValidDay = selectedWeekdays.contains(dayOfWeek);
                    break;

                case MONTHLY:
                    isValidDay = true;
                    break;

                case YEARLY:
                    isValidDay = true;
                    break;
            }

            if (isValidDay) {
                for (Calendar time : dailyTimes) {
                    Calendar reminderTime = (Calendar) current.clone();
                    reminderTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
                    reminderTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                    reminderTime.set(Calendar.SECOND, 0);
                    reminderTime.set(Calendar.MILLISECOND, 0);

                    result.add(reminderTime);
                }
            }

            switch (unit) {
                case DAILY:
                    current.add(Calendar.DAY_OF_MONTH, every);
                    break;
                case WEEKLY:
                    current.add(Calendar.DAY_OF_MONTH, 1);
                    break;
                case MONTHLY:
                    current.add(Calendar.MONTH, every);
                    break;
                case YEARLY:
                    current.add(Calendar.YEAR, every);
                    break;
            }
        }

        return result;
    }
}
