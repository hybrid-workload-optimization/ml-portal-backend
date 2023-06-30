package kr.co.strato.test;

import java.util.Base64;

public class Main {

	public static void main(String[] args) {
		String s = "abc";
		
		byte[] encoded = Base64.getEncoder().encode(s.getBytes());
		
		System.out.println(new String(encoded));
	}
}
