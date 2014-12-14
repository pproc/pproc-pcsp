package es.unizar.contsem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

public class Log {

	public static int ERROR = 4;
	public static int WARNING = 3;
	public static int INFO = 2;
	public static int DEBUG = 1;

	public static int message_priority = DEBUG;

	public static void setLevel(int priority) {
		message_priority = priority;
	}

	public static void error(Class aClass, String message, Object... args) {
		if (message_priority <= ERROR)
			System.err.printf(getNow() + " [ERROR] " + aClass.getSimpleName() + " : " + message + "\n", args);
	}

	public static void warning(Class aClass, String message, Object... args) {
		if (message_priority <= WARNING)
			System.out.printf(getNow() + " [WARNING] " + aClass.getSimpleName() + " : " + message + "\n", args);
	}

	public static void info(Class aClass, String message, Object... args) {
		if (message_priority <= INFO)
			System.out.printf(getNow() + " [INFO] " + aClass.getSimpleName() + " : " + message + "\n", args);
	}

	public static void debug(Class aClass, String message, Object... args) {
		if (message_priority <= DEBUG)
			System.out.printf(getNow() + " [DEBUG] " + aClass.getSimpleName() + " : " + message + "\n", args);
	}

	private static String getNow() {
		Calendar rightNow = Calendar.getInstance();
		return String.format("%02d:%02d:%02d", rightNow.get(Calendar.HOUR_OF_DAY), rightNow.get(Calendar.MINUTE),
				rightNow.get(Calendar.SECOND));
	}
	
	public static void writeInfile(String fileName, String writeThis) {
		try {
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			writer.print(writeThis);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	public static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
