package es.unizar.contsem.codice.parser;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import es.unizar.contsem.codice.parser.database.DatabaseManager;

public class Test {

	public static void main(String[] args) throws Exception {
		DatabaseManager db = new DatabaseManager();
		String firstXML = db.getFirstXML();
		System.out.println(firstXML);

		SAXReader reader = new SAXReader();
		Document document = reader.read(new ByteArrayInputStream(firstXML
				.getBytes(StandardCharsets.UTF_8)));

		Element root = document.getRootElement();

		// iterate through child elements of root
		for (Iterator i = root.elementIterator(); i.hasNext();) {
			Element element = (Element) i.next();
			System.out.println(element.toString());
		}

		// // iterate through child elements of root with element name "foo"
		// for ( Iterator i = root.elementIterator( "foo" ); i.hasNext(); ) {
		// Element foo = (Element) i.next();
		// // do something
		// }
		//
		// // iterate through attributes of root
		// for ( Iterator i = root.attributeIterator(); i.hasNext(); ) {
		// Attribute attribute = (Attribute) i.next();
		// // do something
		// }

	}

}
