package org.jinzora.upnp;

public class Log {
	public static void d(String tag, String message) {
		System.out.println(tag + ": " + message);
	}
	
	public static void d(String tag, String message, Exception e) {
		System.out.println(tag + ": " + message);
		e.printStackTrace();
	}
	
	public static void w(String tag, String message) {
		System.out.println(tag + ": " + message);
	}
	
	public static void w(String tag, String message, Exception e) {
		System.out.println(tag + ": " + message);
		e.printStackTrace();
	}
	
	public static void e(String tag, String message) {
		System.out.println(tag + ": " + message);
	}
	
	public static void e(String tag, String message, Exception e) {
		System.out.println(tag + ": " + message);
		e.printStackTrace();
	}
}
