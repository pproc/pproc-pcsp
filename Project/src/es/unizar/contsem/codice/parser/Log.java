package es.unizar.contsem.codice.parser;

import java.util.Calendar;

public class Log {

	public static int ERROR = 4;
	public static int WARNING = 3;
	public static int INFO = 2;
	public static int DEBUG = 1;

	public static int message_priority = 1;

	public static void setLevel(int priority) {
		message_priority = priority;
	}

	public static void error(String message, Object... args) {
		if (message_priority <= ERROR)
			System.out.printf(getNow() + "[ERROR]: " + message + "\n", args);
	}

	public static void warning(String message, Object... args) {
		if (message_priority <= WARNING)
			System.out.printf(getNow() + "[WARNING]: " + message + "\n", args);
	}

	public static void info(String message, Object... args) {
		if (message_priority <= INFO)
			System.out.printf(getNow() + "[INFO]: " + message + "\n", args);
	}

	public static void debug(String message, Object... args) {
		if (message_priority <= DEBUG)
			System.out.printf(getNow() + "[DEBUG]: " + message + "\n", args);
	}

	private static String getNow() {
		Calendar rightNow = Calendar.getInstance();
		return String.format("%02d:%02d:%02d", rightNow.get(Calendar.HOUR_OF_DAY), rightNow.get(Calendar.MINUTE),
				rightNow.get(Calendar.SECOND));
	}

}
