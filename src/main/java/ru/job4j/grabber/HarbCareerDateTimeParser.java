package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.util.Map;

public class HarbCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        LocalDateTime dateTime = LocalDateTime.parse(parse);
        return dateTime;
    }
}
