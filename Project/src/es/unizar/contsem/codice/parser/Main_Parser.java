package es.unizar.contsem.codice.parser;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.danielrusa.crawler.Database;
import es.danielrusa.crawler.Main_UpdateXml;
import es.unizar.contsem.Log;

/**
 * This class main parse the CODICE XML documents from a database and transform them into RDF following the PPROC
 * ontology (http://contsem.unizar.es/) model.
 * 
 * @author guillermo
 * @date 2014-12-14
 */
public class Main_Parser {

	/**
	 * @param args
	 *            args[0] = database/table URL (e.g. jdbc:mysql://localhost:4406/licitaciones) args[1] = username
	 *            args[2] = password
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			Log.error(Main_Parser.class,
					"bad arguments, usage: Main_Parser <database/table URL> <username> <password>");
			return;
		}

		Database database = new Database(args[0], args[1], args[2]);
		database.connect();
		Model model = ModelFactory.createDefaultModel();
		int xmlErrorCount = 0;
		String xml = "error";

		for (int i = 1; i < 30000; i++) {
			try {
				xml = database.getXML(i);
				SAXReader reader = new SAXReader();
				Document document = reader.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
				try {
					CodiceToPprocParser.parseCodiceXML(model, document);
				} catch (Exception ex) {
					Log.writeInfile("codice_doc.xml", document.asXML());
					ex.printStackTrace();
					return;
				}
				Log.info(Main_Parser.class, "XML %d parseado e introducido en el modelo", i);
			} catch (Exception exc) {
				Log.error(Main_Parser.class, "error al introducir el XML [%d bytes] : %s",
						xml.getBytes(StandardCharsets.UTF_8).length, exc.getClass().getSimpleName());
				xmlErrorCount++;
			}
		}

		model.write(new PrintWriter("pcsp-output.ttl", "UTF-8"), "Turtle");
		Log.info(Main_Parser.class, "escritura finalizada");
		Log.info(Main_Parser.class, "%d XMLs han fallado", xmlErrorCount);

	}

}
