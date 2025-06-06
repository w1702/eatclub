package com.example.eatclub.util;

public class TimeUtils {
    // Calculate the number of minutes past midnight
    public static int convertToMinutes(String timeStr) {
        timeStr = timeStr.trim().toLowerCase();
        boolean isPM = timeStr.endsWith("pm");
        timeStr = timeStr.replaceAll("[ap]m", "").trim();
        String[] parts = timeStr.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        if (hour == 12){
            hour = 0;
        }
        if (isPM){
            hour += 12;
        }
        return hour * 60 + minute;
    }

    // Given time in minutes past midnight, see if the target time is in the range of start and end (exclusive)
    public static boolean isTimeInRange(int target, int start, int end) {
        return target >= start && target < end;
    }

    // Format minutes past midnight back to 12hr time
    public static String formatMinutesToTime(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        String period = hour >= 12 ? "pm" : "am";
        // convert 24hr back to 12hr format
        hour = hour % 12;
        // if midnight (0) convert it back to 12
        if (hour == 0){
            hour = 12;
        }
        return String.format("%d:%02d%s", hour, min, period);
    }
}
