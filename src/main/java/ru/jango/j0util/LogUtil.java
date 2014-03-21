package ru.jango.j0util;

import android.util.Log;

/**
 * Utility class for better log usage: log tag consists of {@link #LOG_TAG} and class name,
 * so you could filter messages from a certain class.
 * <br /><br />
 * {@link #LOG_TAG} in not final, so u can change it, simply assign: <br />
 * LogUtil.LOG_TAG = "some_new_log_tag"
 */
public class LogUtil {

	public static String LOG_TAG = "j0";

	public static <T> String getLogTag(Class<T> c) {
		return (LOG_TAG + ":" + c.getName());
	}

	public static void i(Class<?> c, String msg) {
		Log.i(getLogTag(c), msg);
	}
	
	public static void e(Class<?> c, String msg) {
		Log.e(getLogTag(c), msg);
	}
	
	public static void d(Class<?> c, String msg) {
		Log.d(getLogTag(c), msg);
	}
	
	public static void w(Class<?> c, String msg) {
		Log.w(getLogTag(c), msg);
	}
	
	public static void logMemoryUsage() {
		final Runtime r = Runtime.getRuntime();
		final String memUse = "using " + formatNumber(r.totalMemory() - r.freeMemory())
				+ " memory of " + formatNumber(r.totalMemory()) + " total";
		Log.i(LOG_TAG, memUse);
	}
	
	protected static String formatNumber(long n) {
		final String DELIMITER = ".";
		final StringBuilder num = new StringBuilder(String.valueOf(n));
		final StringBuilder fNum = new StringBuilder();
		
		int i = 0;
		num.reverse();
		while (i < num.length()) {
			fNum.insert(0,num.charAt(i));
			
			i++;
			if (i%3 == 0 && i != num.length()) fNum.insert(0, DELIMITER);
		}
		
		return fNum.toString();
	}
}
