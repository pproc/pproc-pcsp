package es.unizar.contsem.parser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.unizar.contsem.Database;
import es.unizar.contsem.Log;
import es.unizar.contsem.Utils;
import es.unizar.contsem.crawler.XmlLink;

/**
 * Transforms the XML/CODICE documents stored in a database into RDF following PPROC ontology.
 * 
 * @author gesteban
 * @see es.unizar.contsem.Database
 * @see es.unizar.contsem.parser.CodiceToPprocParser
 *
 */
public class Main_CodiceParser {

    private static final int QUERY_LIMIT = 2000;
    private static int xmlErrorCount = 0;

    private static void start(Database database) throws FileNotFoundException, UnsupportedEncodingException,
            InterruptedException {

        Set<XmlLink> xmlLinks;
        int totalCount = 0;
        while (!(xmlLinks = database.selectByLimit(true, QUERY_LIMIT, Database.FLAG_CHECKED_VALID)).isEmpty()) {
            Set<XmlLink> tempXmlLinks = new HashSet<XmlLink>();
            Model model = ModelFactory.createDefaultModel();
            SAXReader reader = new SAXReader();
            int tempCount = 0;
            for (XmlLink xmlLink : xmlLinks) {
                tempCount++;
                totalCount++;
                try {
                    Document document = reader.read(new ByteArrayInputStream(xmlLink.xml
                            .getBytes(StandardCharsets.UTF_8)));
                    try {
                        CodiceToPprocParser.parseCodiceXML(model, document);
                    } catch (Exception ex) {
                        Log.error(Main_CodiceParser.class, "[start] error parsing xml %d, see debug/codice_doc.xml",
                                xmlLink.id);
                        Utils.writeInfile("debug/codice_doc.xml", document.asXML());
                        ex.printStackTrace();
                        return;
                    }
                    Log.info(Main_CodiceParser.class, "[start] xml %d parsed and inserted in model", xmlLink.id);
                    tempXmlLinks.add(xmlLink);
                } catch (DocumentException exc) {
                    Log.error(Main_CodiceParser.class, "[start] error reading xml %d [%d bytes]", xmlLink.id,
                            xmlLink.xml.getBytes(StandardCharsets.UTF_8).length);
                    xmlErrorCount++;
                }
            }
            Utils.writeModel(model,
                    String.format("pcsp-output/pcsp-output-%d-%d.ttl", totalCount - tempCount + 1, totalCount));
            model.removeAll();
            model.close();
            database.updateFlags(tempXmlLinks, Database.FLAG_CHECKED_PARSED);
            model = ModelFactory.createDefaultModel();
            tempCount = 0;
            tempXmlLinks.clear();
            Log.info(Main_CodiceParser.class, "%d XMLs han fallado", xmlErrorCount);
            reader = null;
        }
        Log.info(Main_CodiceParser.class, "proceso finalizado");
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
            Log.error(Main_CodiceParser.class, "usage: Main_CodiceParser database/table_URL username password");
            return;
        }

        Database database = new Database(args[0], args[1], args[2]);
        database.connect();
        Log.setLevel(Log.DEBUG);
        try {
            Main_CodiceParser.start(database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
