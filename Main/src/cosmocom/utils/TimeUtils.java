package src.cosmocom.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private static LocalDateTime currentTime;
    private static final ZoneId MOSCOW_ZONE = ZoneId.of("Europe/Moscow");

    public static void updateTime() {
        currentTime = LocalDateTime.now(MOSCOW_ZONE);
    }

    public static double getCurrentJD() {
        updateTime();
        return toJulianDate(
                currentTime.getYear(),
                currentTime.getMonthValue(),
                currentTime.getDayOfMonth(),
                currentTime.getHour(),
                currentTime.getMinute(),
                currentTime.getSecond()
        );
    }

    public static String getCurrentDateString() {
        updateTime();
        return currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getCurrentTimeString() {
        updateTime();
        return currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String getCurrentDateTimeString() {
        updateTime();
        return currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getCurrentJDString() {
        return String.format("%.5f", getCurrentJD());
    }

    private static double toJulianDate(int year, int month, int day, int hour, int minute, int second) {
        if (month <= 2) {
            year -= 1;
            month += 12;
        }
        int A = year / 100;
        int B = 2 - A + (A / 4);
        double JD = (int) (365.25 * (year + 4716)) + (int) (30.6001 * (month + 1)) + day + B - 1524.5;
        JD += (hour + minute / 60.0 + second / 3600.0) / 24.0;
        return JD;
    }
}