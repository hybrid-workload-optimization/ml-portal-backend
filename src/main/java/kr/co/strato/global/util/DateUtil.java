package kr.co.strato.global.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    
    public static String currentDateTime(String dateFormat) {
    	Timestamp timestamp  = new Timestamp(System.currentTimeMillis());
    	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    	return sdf.format(timestamp);
    }
}
