/*
 * The MIT License Copyright (c) 2014 Krayushkin Konstantin (jangokvk@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
