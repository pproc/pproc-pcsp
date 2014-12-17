package es.unizar.contsem.codice.parser;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.danielrusa.crawler.Database;
import es.unizar.contsem.Log;

/**
 * This class main parse the CODICE XML documents from a database and transform
 * them into RDF following the PPROC ontology (http://contsem.unizar.es/) model.
 * 
 * @author gesteban
 * @date 2014-12-14
 */
public class Main_Parser {

	static final int start = 168001;
	static final int end = 200000;
	static final int length = 2000;

	/**
	 * @param args
	 *            args[0] = database/table URL (e.g.
	 *            jdbc:mysql://localhost:4406/licitaciones) args[1] = username
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
		Model model;
		int xmlErrorCount = 0;
		String xml = "error";

		int from = start;
		int to = from + length - 1;

		while (from < end) {

			model = ModelFactory.createDefaultModel();

			for (int i = from; i < to; i++) {
				try {
					xml = database.getXML(i);
					SAXReader reader = new SAXReader();
					Document document = reader.read(new ByteArrayInputStream(
							xml.getBytes(StandardCharsets.UTF_8)));
					try {
						CodiceToPprocParser.parseCodiceXML(model, document);
					} catch (Exception ex) {
						Log.error(Main_Parser.class,
								"error parsing XML, see codice_doc.xml");
						Log.writeInfile("codice_doc.xml", document.asXML());
						ex.printStackTrace();
						return;
					}
					Log.info(Main_Parser.class,
							"XML %d parseado e introducido en el modelo", i);
				} catch (Exception exc) {
					Log.error(Main_Parser.class,
							"error reading XML with id = %d [%d bytes] : %s",
							i, xml.getBytes(StandardCharsets.UTF_8).length, exc
									.getClass().getSimpleName());
					xmlErrorCount++;
				}
			}

			model.write(
					new PrintWriter(String.format("pcsp-output-%d-%d.ttl",
							from, to), "UTF-8"), "Turtle");
			Log.info(Main_Parser.class, "escritura finalizada");
			Log.info(Main_Parser.class, "%d XMLs han fallado", xmlErrorCount);

			from += length;
			to = (from + length - 1 > end ? end : from + length - 1);
		}

		Log.info(Main_Parser.class, "proceso finalizado");
	}

}
