package edu.northeastern.finalproject_group_1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderScheduler {

    public static void scheduleReminders(Context context, int habitId, String habitTitle, List<Calendar> reminderTimes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < reminderTimes.size(); i++) {
            Calendar reminderTime = reminderTimes.get(i);

            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra("title", habitTitle);
            intent.putExtra("message", ReminderMessagePool.getRandomMessage(habitTitle));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    habitId * 100 + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            try {
                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            reminderTime.getTimeInMillis(),
                            pendingIntent
                    );
                    Log.d("ReminderScheduler", "Scheduled reminder at " + reminderTime.getTime().toString());
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cancelReminders(Context context, int baseRequestCode, List<Calendar> reminderTimes) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < reminderTimes.size(); i++) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    baseRequestCode + i,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                Log.d("ReminderScheduler", "Cancelled reminder at index " + i + " (code=" + (baseRequestCode + i) + ")");
            }
        }
    }

    public static void updateReminders(Context context, Habit oldHabit, Habit newHabit) {
        List<Calendar> oldTimes = ReminderTimeGenerator.generateReminderTimes(
                oldHabit.getStartDate(),
                oldHabit.getEndDate(),
                ReminderTimeGenerator.RepeatUnit.valueOf(oldHabit.getRepeatUnit().toUpperCase()),
                oldHabit.getEvery(),
                oldHabit.getWeekdays(),
                parseTimes(oldHabit.getReminderTimes())
        );
        cancelReminders(context, oldHabit.hashCode(), oldTimes);

        List<Calendar> newTimes = ReminderTimeGenerator.generateReminderTimes(
                newHabit.getStartDate(),
                newHabit.getEndDate(),
                ReminderTimeGenerator.RepeatUnit.valueOf(newHabit.getRepeatUnit().toUpperCase()),
                newHabit.getEvery(),
                newHabit.getWeekdays(),
                parseTimes(newHabit.getReminderTimes())
        );
        scheduleReminders(context, newHabit.hashCode(), newHabit.getTitle(), newTimes);
    }

    private static List<Calendar> parseTimes(List<String> timeStrings) {
        List<Calendar> result = new ArrayList<>();
        for (String time : timeStrings) {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                result.add(cal);
            }
        }
        return result;
    }
}