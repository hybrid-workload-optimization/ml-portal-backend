package kr.co.strato.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static LocalDateTime strToLocalDateTime(String text){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return LocalDateTime.parse(text, formatter);
    }

    public static LocalDateTime strToLocalDateTime(String text, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.from(formatter.parse(text));
    }

    public static String localDateTimeToStr(LocalDateTime localDateTime, String format){
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }
}
