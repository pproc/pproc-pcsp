package es.unizar.contsem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;

import es.unizar.contsem.crawler.DefaultTrustManager;

public class Utils {

    public static final int MAX_TRY_COUNT = 6;

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

    public static String getXML(String urlToRead) {
        DefaultTrustManager.CrearConexionHTTPS();
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line, result = "", result2 = "";
        boolean success = false;
        int tryCount = 0;
        while (!success && tryCount < MAX_TRY_COUNT) {
            tryCount++;
            try {
                url = new URL(urlToRead);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = rd.readLine()) != null)
                    result += line;
                String[] url2 = Utils.search("url='.*'", result);
                String newURL = null;
                if (url2.length > 0) {
                    newURL = url2[0].replaceAll("url='", "https://contrataciondelestado.es");
                }
                newURL = newURL.replaceAll("'", "");
                url = new URL(newURL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
                while ((line = rd.readLine()) != null)
                    result2 += line;
                rd.close();
                result2.replaceAll("'", "''");
                success = true;
                conn.disconnect();
            } catch (Exception e) {
                Log.warning(Utils.class, "try number %d at getXML failed", tryCount);
            }
        }
        if (!success) {
            Log.error(Utils.class, "getXML could not retrieve the XML");
            return "<error/>";
        }
        return result2;
    }

    public static void writeModel(Model model, String fileName) {
        try {
            model.write(new PrintWriter(fileName, "UTF-8"), "Turtle");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.info(Utils.class, "escritura finalizada");
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

}
