package kr.co.strato.global.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

public class DateUtil {
    public static LocalDateTime strToLocalDateTime(String text){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		LocalDateTime local = LocalDateTime.parse(text, formatter);
		local = local.plusHours(9L);

        return local;
    }
    
    public static String strToNewFormatter(String text){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		LocalDateTime local = LocalDateTime.parse(text, formatter);
		local = local.plusHours(9L);
		
		DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return local.format(newFormatter);
    }

    public static LocalDateTime strToLocalDateTime(String text, String pattern){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.from(formatter.parse(text));
    }

    public static String localDateTimeToStr(LocalDateTime localDateTime, String format) {
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }
    
    public static String currentDateTime(String dateFormat) {
    	Timestamp timestamp  = new Timestamp(System.currentTimeMillis());
    	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    	return sdf.format(timestamp);
    }
    
    public static String currentDateTime() {
    	Timestamp timestamp  = new Timestamp(System.currentTimeMillis());
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return sdf.format(timestamp);
    }
    
    /**
     * 날짜형 문자열 변환
     * 
     * reference : http://zeany.net/62
     * 
     * @param strDate
     * @return
     */
    public static Date parse(String strDate) {
		if (strDate == null || strDate.isEmpty()) {
			return null;
		}

		StringBuilder sdfSb = new StringBuilder("yyyy-MM-dd");

		if (strDate.length() < 19) { // "yyyy-MM-dd HH:mm:ss".length == 19
			return null;
		}

		if (strDate.charAt(10) == 'T') {
			sdfSb.append("'T'HH:mm:ss");
		} else if (strDate.charAt(10) == ' ') {
			sdfSb.append(" HH:mm:ss");
		} else {
			return null;
		}

		int timezoneIndex;
		// .SSS는 있을 수도 있고 없을 수도 있음, 없는 경우에는 19번째부터 timezone이고 있는 경우는 23번째부터 timezone

		if (strDate.substring(19).length() >= 4 && Pattern.matches("[.]\\d{3}", strDate.substring(19, 23))) {
			sdfSb.append(".SSS");
			timezoneIndex = 23;
		} else {
			timezoneIndex = 19;
		}

		// Timezone을 요약해보면 Z, +09, +0900은 X로, +09:00은 XXX로, KST는 Z로
		String timezone = strDate.substring(timezoneIndex);
		if (timezone.equals("")) {
			// skip
		} else if (timezone.equals("Z")) {
			sdfSb.append("X");
		} else if (Pattern.matches("[+|-]\\d{2}", timezone)) {
			sdfSb.append("X");
		} else if (Pattern.matches("[+|-]\\d{4}", timezone)) {
			sdfSb.append("X");
		} else if (Pattern.matches("[+|-]\\d{2}[:]\\d{2}", timezone)) {
			sdfSb.append("XXX");
		} else if (Pattern.matches("[A-Z]{3}", timezone)) {
			sdfSb.append("Z");
		} else {
			return null;
		}
		
		Date resultDate = null;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(sdfSb.toString());
			resultDate = sdf.parse(strDate);
		} catch (ParseException e) {
			// ignore
		}
		
		return resultDate;
	}
    
    public static String convertDateTime(String dateTime) {
    	Date date = parse(dateTime);
    	if (date == null) {
    		return null;
    	}
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	return sdf.format(date);
    }
    
    public static Date toDate(String date, String dateFormat) throws ParseException {
    	SimpleDateFormat format = new SimpleDateFormat(dateFormat);
    	return format.parse(date);
    }
    
    public static boolean isToday(String date) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    	LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    	dateTime = dateTime.plusHours(9L);
    	
    	LocalDateTime today = LocalDate.now().atStartOfDay();
    	return dateTime.isAfter(today);
    }
    
    public static boolean isWithin30minutes(String date) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    	LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
    	
    	LocalDateTime today = LocalDateTime.now();
    	LocalDateTime minus30minutes = today.minusMinutes(30);    	
    	return dateTime.isAfter(minus30minutes);
    }
    
    public static void main(String[] args) {
    	/*
    	String str = "2023-07-28 00:00:12";
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    	LocalDateTime today = LocalDate.now().atStartOfDay();
    	
    	System.out.println(dateTime + ", " + today);
    	boolean after = dateTime.isAfter(today);
    	System.out.println(after);
    	*/
    	
    	String str = "2023-07-31 10:23:12";
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    	
    	LocalDateTime today = LocalDateTime.now();
    	LocalDateTime minus30minutes = today.minusMinutes(30);
    	//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	System.out.println(minus30minutes.format(formatter));
    	
    	boolean after = dateTime.isAfter(minus30minutes);
    	System.out.println(after);
    }
}
