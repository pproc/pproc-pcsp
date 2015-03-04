package es.unizar.contsem.crawler;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import es.unizar.contsem.Database;
import es.unizar.contsem.Log;
import es.unizar.contsem.Utils;
import es.unizar.contsem.XmlLink;

/**
 * Check the XML/CODICE files stored in a database to assure they are correct, if some XML/CODICE is not correct, it's
 * retrieved again using the link stored in the database.
 * 
 * Is assumed that a CODICE XML of 4724 bytes of length is incorrect (otherwise the SaxReader throws an Exception).
 * 
 * @author gesteban
 * @see es.unizar.contsem.Database
 * @see org.dom4j.io.SAXReader
 */
public class Main_XmlUpdater {

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
            Log.error(Main_XmlUpdater.class, "usage: Main_XmlUpdater database/table_URL username password");
            return;
        }

        Database database = new Database(args[0], args[1], args[2]);
        database.connect();
        Set<XmlLink> xmlLinks = new HashSet<XmlLink>();
        int errorCount = 0;
        xmlLinks = database.getLinksByFlag(1,0);
        for (XmlLink xmlLink : xmlLinks)
            if (xmlLink.xml.getBytes(StandardCharsets.UTF_8).length == 4724) {
                errorCount++;
                String xml = Utils.getXML(xmlLink.link);
                if (xml.getBytes(StandardCharsets.UTF_8).length != 4724) {
                    xmlLink.xml = xml;
                    database.updateXml(xmlLink);
                } else
                    Log.error(Main_XmlUpdater.class, "[main] de nuevo 4724 en %d" + xmlLink.id);
            }
        database.disconnect();
        Log.info(Main_XmlUpdater.class, "[main] errorCount = " + errorCount);

    }
}
