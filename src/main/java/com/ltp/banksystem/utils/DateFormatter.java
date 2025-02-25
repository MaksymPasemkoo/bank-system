package com.ltp.banksystem.utils;

import com.ltp.banksystem.exception.TimeException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateFormatter {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static LocalDate format(final String date) {
        try {
            return LocalDate.parse(date, dateTimeFormatter);
        } catch (DateTimeParseException exception) {
            throw new TimeException("Format must be \"dd-MM-yyyy\"");
        }
    }
}
