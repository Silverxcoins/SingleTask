package com.example.sasha.singletask.helpers;

import android.app.Activity;
import android.app.FragmentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    private static final long DIVIDE_MILLISECONDS_FOR_DAYS = 86400000L;

    public static final int DAYS_IN_WEEK = 7;

    private static long userId;

    public static void clearBackStack(Activity activity) {

        logger.debug("clearBackStack()");

        FragmentManager manager = activity.getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public static void setUserId(long userId) {
        Utils.userId = userId;
    }

    public static long getUserId() {
        return userId;
    }
    
    public static String getTimeAsString(int minutes) {

        logger.debug("getTimeAsString(int minutes)");

        final int hours = minutes / 60;
        minutes = minutes % 60;
        return getTimeAsString(hours, minutes);
    }
    
    public static String getTimeAsString(int hours, int minutes) {

        logger.debug("getTimeAsString(int hours, int minutes)");

        String timeString = "";

        if (hours != 0) {
            String hoursString;
            if (hours % 10 == 1 && (hours < 10 || hours > 20)) {
                hoursString = " час ";
            } else if ((hours % 10 == 2 || hours % 10 == 3 || hours % 10 == 4)
                    && (hours < 10 || hours > 20)) {
                hoursString = " часа ";
            } else {
                hoursString = " часов ";
            }
            timeString += hours + hoursString;
        }

        if (minutes != 0) {
            String minutesString;
            if (minutes % 10 == 1 && (minutes < 10 || minutes > 20)) {
                minutesString = " минута ";
            } else if ((minutes % 10 == 2 || minutes % 10 == 3 || minutes % 10 == 4)
                    && (minutes < 10 || minutes > 20)) {
                minutesString = " минуты ";
            } else {
                minutesString = " минут ";
            }
            timeString += minutes + minutesString;
        }
        return timeString;
    }

    public static int getTimeAsInt(String timeString) {

        logger.debug("getTimeAsInt()");

        if (timeString.equals("")) return 0;

        String[] timeParts = timeString.split(" ");
        if (timeParts.length == 4) {
            return new Integer(timeParts[0]) * 60 + new Integer(timeParts[2]);
        } else if (timeParts[1].charAt(0) == 'ч') {
            return new Integer(timeParts[0]) * 60;
        } else {
            return new Integer(timeParts[0]);
        }
    }

    public static boolean isDateEarlierThanNow(int year, int month, int day) {

        logger.debug("isDateEarlierThanNow()");

        Calendar c = Calendar.getInstance();
        return year < c.get(Calendar.YEAR)
                || (year == c.get(Calendar.YEAR) && month < c.get(Calendar.MONTH) + 1)
                || (year == c.get(Calendar.YEAR) && month == c.get(Calendar.MONTH) + 1
                && day < c.get(Calendar.DAY_OF_MONTH));
    }

    public static String getCurrentTimeAsString() {

        logger.debug("getCurrentTimeAsString()");

        Calendar c = Calendar.getInstance();
        StringBuilder timeBuilder = new StringBuilder();
        timeBuilder.append(c.get(Calendar.YEAR)).append("-")
                .append(String.valueOf(c.get(Calendar.MONTH) + 1)).append("-")
                .append(c.get(Calendar.DAY_OF_MONTH)).append(" ")
                .append(c.get(Calendar.HOUR)).append(":")
                .append(c.get(Calendar.MINUTE)).append(":")
                .append(c.get(Calendar.SECOND));
        return timeBuilder.toString();
    }

    public static Date parseDateString(String dateString) {

        logger.debug("parseDateString()");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    public static Integer getDaysBetweenDateAndCurrentDate(String dateString) {

        logger.debug("getDaysBetweenDateAndCurrentDate()");

        Date date = parseDateString(dateString);
        Date currentDate = Calendar.getInstance().getTime();

        long difference = date.getTime() - currentDate.getTime();
        return (int) (difference/DIVIDE_MILLISECONDS_FOR_DAYS);
    }
}
