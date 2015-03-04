package es.unizar.contsem.crawler;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import es.unizar.contsem.Database;
import es.unizar.contsem.Log;
import es.unizar.contsem.Utils;
import es.unizar.contsem.XmlLink;

/**
 * Download XML/CODICE files from http://contrataciondelestado.es/ and store them in a database.
 * 
 * @author gesteban
 * @see es.unizar.contsem.Database
 *
 */
public class Main_XmlGetter {

    private static final int MAX_BUFFER = 50;
    private static final int DELAY = 1000;

    private static void start(Database database) throws FileNotFoundException, UnsupportedEncodingException,
            InterruptedException {
        Set<XmlLink> xmlLinks = database.getLinksByFlag(0,0);
        Set<XmlLink> tempXmlLinks = new HashSet<XmlLink>();
        int tempCount = 0;
        String xml;
        long startTime = System.currentTimeMillis();
        for (XmlLink xmlLink : xmlLinks) {
            tempCount++;
            xml = Utils.getXML(xmlLink.link);
            xmlLink.xml = xml;
            tempXmlLinks.add(xmlLink);
            if (tempCount == MAX_BUFFER) {
                Log.info(Main_XmlGetter.class, "[start] takes %f seconds to download %d xmls",
                        (double) (System.currentTimeMillis() - startTime) / 1000, tempXmlLinks.size());
                database.updateXmls(tempXmlLinks);
                database.updateFlags(tempXmlLinks, 1);
                tempCount = 0;
                tempXmlLinks.clear();
                startTime = System.currentTimeMillis();
            }
            Thread.sleep(DELAY);
        }
        Log.info(Main_XmlGetter.class, "proceso finalizado");
    }

    /**
     * @param args
     *            <ul>
     *            <li>args[0] database/table URL</li>
     *            <li>args[1] username</li>
     *            <li>args[2] password</li>
     *            </ul>
     *            example <i>jdbc:mysql://localhost:4406/licitaciones peter Gr1Ff1N</i>
     */
    public static void main(String[] args) {

        if (args.length != 3) {
            Log.error(Main_XmlGetter.class, "usage: Main_XmlGetter database/table_URL username password");
            return;
        }

        Database database = new Database(args[0], args[1], args[2]);
        database.connect();
        Log.setLevel(Log.DEBUG);
        try {
            Main_XmlGetter.start(database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
