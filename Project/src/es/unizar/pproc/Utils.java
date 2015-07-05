package es.unizar.pproc;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;

public class Utils {

	public static final String TAG = Utils.class.getSimpleName();

	public static String[] search(String regex, String onText) {
		Pattern regPatt = Pattern.compile(regex);
		Matcher regMatch = regPatt.matcher(onText);
		ArrayList<String> matches = new ArrayList<>();
		String[] matchesArray;
		while (regMatch.find()) {
			String match = regMatch.group();
			matches.add(match);
		}
		matchesArray = new String[matches.size()];
		matches.toArray(matchesArray);
		return matchesArray;
	}

	public static void writeModel(Model model, String fileName) {
		try {
			model.write(new PrintWriter(fileName, "UTF-8"), "Turtle");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "[writeModel] model written in %s", fileName);
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
