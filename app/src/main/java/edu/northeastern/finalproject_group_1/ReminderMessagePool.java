package edu.northeastern.finalproject_group_1;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReminderMessagePool {

    private static final List<String> MESSAGES = Arrays.asList(
            "Time to work on \"%s\"!",
            "Don't forget: \"%s\" is waiting for you.",
            "Let's do it: \"%s\" 💪",
            "You're doing great! Keep it up with \"%s\"!",
            "A small step today for \"%s\" is a big win tomorrow.",
            "Remember your goal: \"%s\"!",
            "Keep going with \"%s\" 🌱",
            "You're on fire! Get \"%s\" done!",
            "It's \"%s\" time ☕",
            "Consistency is key. Work on \"%s\" now!"
    );

    private static final Random random = new Random();

    public static String getRandomMessage(String habitTitle) {
        String template = MESSAGES.get(random.nextInt(MESSAGES.size()));
        return String.format(template, habitTitle);
    }
}