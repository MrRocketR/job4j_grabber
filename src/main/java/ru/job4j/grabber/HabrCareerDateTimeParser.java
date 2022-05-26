package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        ZonedDateTime zd = ZonedDateTime.parse(parse);
        return zd.toLocalDateTime();
    }
}
