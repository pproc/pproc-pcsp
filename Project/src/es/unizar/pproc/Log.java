package es.unizar.pproc;

import java.util.Calendar;

/**
 * Simple logger.
 * 
 * @author gesteban
 *
 */
public class Log {

	public static int ERROR = 4;
	public static int WARNING = 3;
	public static int INFO = 2;
	public static int DEBUG = 1;

	public static int message_priority = DEBUG;

	public static void setLevel(int priority) {
		message_priority = priority;
	}

	public static void e(String tag, String message, Object... args) {
		if (message_priority <= ERROR)
			System.err.printf(getNow() + " [ERROR] " + tag + " : " + message + "\n", args);
	}

	public static void w(String tag, String message, Object... args) {
		if (message_priority <= WARNING)
			System.out.printf(getNow() + " [WARNING] " + tag + " : " + message + "\n", args);
	}

	public static void i(String tag, String message, Object... args) {
		if (message_priority <= INFO)
			System.out.printf(getNow() + " [INFO] " + tag + " : " + message + "\n", args);
	}

	public static void d(String tag, String message, Object... args) {
		if (message_priority <= DEBUG)
			System.out.printf(getNow() + " [DEBUG] " + tag + " : " + message + "\n", args);
	}

	private static String getNow() {
		Calendar rightNow = Calendar.getInstance();
		return String.format("%02d:%02d:%02d", rightNow.get(Calendar.HOUR_OF_DAY), rightNow.get(Calendar.MINUTE),
				rightNow.get(Calendar.SECOND));
	}

}
