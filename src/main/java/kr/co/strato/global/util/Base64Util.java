package kr.co.strato.global.util;

import java.util.Base64;

public class Base64Util {
    public static String decode(String encodedText){
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        String decodedText = new String(decodedBytes);

        return decodedText;
    }

    public static String encode(String text){
        String encodedText = Base64.getEncoder().encodeToString(text.getBytes());

        return encodedText;
    }
}
