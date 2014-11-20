package es.unizar.contsem.codice.parser;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.unizar.contsem.codice.parser.database.DatabaseManager;

public class Test {

	public static void main(String[] args) throws Exception {

		DatabaseManager db = new DatabaseManager();
		String firstXML = db.getFirstXML();

		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(firstXML
				.getBytes(StandardCharsets.UTF_8)));

		Model model = ModelFactory.createDefaultModel();
		CodiceToPprocParser.parseCodiceXML(model, document);

	}

}
