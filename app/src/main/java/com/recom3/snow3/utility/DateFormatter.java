package com.recom3.snow3.utility;

/**
 * Created by Recom3 on 31/05/2023.
 */

public class DateFormatter {
    public static CharSequence getDayWithSuffix(int day) {
        if (day >= 1 && day <= 31) {
            if (day >= 11 && day <= 13) {
                return String.valueOf(day) + "th";
            }
            switch (day % 10) {
                case 1:
                    return String.valueOf(day) + "st";
                case 2:
                    return String.valueOf(day) + "nd";
                case 3:
                    return String.valueOf(day) + "rd";
                default:
                    return String.valueOf(day) + "th";
            }
        }
        throw new IllegalArgumentException("The day of the month should be between 1 and 31.");
    }
}