package es.unizar.contsem.codice.parser;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.danielrusa.TFG_crawler.Database;

public class Test {

	public static void main(String[] args) throws Exception {

		Database database = new Database();
		database.connect();
		Model model = ModelFactory.createDefaultModel();
		int xmlErrorCount = 0;

		for (int i = 1; i <= 30000; i++) {
			try {
				String xml = database.getXML(i);
				SAXReader reader = new SAXReader();
				Document document = reader.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
				try {
					CodiceToPprocParser.parseCodiceXML(model, document);
				} catch (Exception ex) {
					PrintWriter writer = new PrintWriter("codice_doc.xml", "UTF-8");
					writer.print(document.asXML());
					writer.close();
					ex.printStackTrace();
					return;
				}
				Log.info(Test.class, "XML %d parseado e introducido en el modelo", i);
			} catch (Exception exc) {
				Log.error(Test.class, "error al introducir el XML %d", i);
				xmlErrorCount++;
			}
		}

		// FileOutputStream out = new FileOutputStream("output2.n3");
		// model.write(out, "N3");
		// out.close();

		model.write(new PrintWriter("pcsp-output.ttl", "UTF-8"), "Turtle");
		Log.info(Test.class, "escritura finalizada");
		Log.info(Test.class, "%d XMLs han fallado", xmlErrorCount);

	}
}
