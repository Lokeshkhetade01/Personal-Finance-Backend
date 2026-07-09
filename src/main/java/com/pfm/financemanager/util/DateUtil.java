package com.pfm.financemanager.util;

import java.time.LocalDate;
import java.time.YearMonth;

public class DateUtil {

    private DateUtil() {
    }

    public static LocalDate firstDayOfMonth(int year, int month) {
        return YearMonth.of(year, month).atDay(1);
    }

    public static LocalDate lastDayOfMonth(int year, int month) {
        return YearMonth.of(year, month).atEndOfMonth();
    }

    public static LocalDate firstDayOfYear(int year) {
        return LocalDate.of(year, 1, 1);
    }

    public static LocalDate lastDayOfYear(int year) {
        return LocalDate.of(year, 12, 31);
    }
}
