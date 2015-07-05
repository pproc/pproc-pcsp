package es.unizar.pproc.codice.test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.unizar.pproc.Log;
import es.unizar.pproc.Utils;
import es.unizar.pproc.codice.Codice2Pproc;
import es.unizar.pproc.codice.Database;
import es.unizar.pproc.codice.Methods;
import es.unizar.pproc.codice.XmlLink;

public class Search {

	public static final String TAG = Search.class.getSimpleName();

	static int count = 0;

	public static void main(String[] args) throws Exception {
		Database database = new Database(args[0], args[1], args[2]);
		database.connect();
		parseXmlsIf(database);
	}

	public static int getCount() {
		return count++;
	}

	public static void parseXmlsIf(Database database) throws FileNotFoundException, UnsupportedEncodingException,
			InterruptedException {
		int xmlErrorCount = 0, noPlatformIDCount = 0, noNIF_CIFCount = 0;
		Set<XmlLink> xmlLinks;
		while (!(xmlLinks = database.selectByLimit(true, 2000, Database.FLAG_CHECKED_PARSED)).isEmpty()) {
			Set<XmlLink> tempXmlLinks = new HashSet<XmlLink>();
			Model model = ModelFactory.createDefaultModel();
			SAXReader reader = new SAXReader();
			for (XmlLink xmlLink : xmlLinks) {
				try {
					org.dom4j.Document document = reader.read(new ByteArrayInputStream(xmlLink.xml
							.getBytes(StandardCharsets.UTF_8)));

					// ////////////////////////////////
					// Aquí empiezan las condiciones
					// ////////////////////////////////

					Element altElement = null;
					String altString = null, altString2 = null;
					if ((altElement = document.getRootElement().element("ContractingParty")) != null
							|| (altElement = document.getRootElement().element("ContractingAuthorityParty")) != null) {
						for (Iterator<?> iter = altElement.element("Party").elementIterator("PartyIdentification"); iter
								.hasNext();) {
							Element altElement2 = (Element) iter.next();
							if (altElement2.element("ID").attributeValue("schemeName").equals("ID_PLATAFORMA")) {
								altString = altElement2.elementText("ID");
							}
							if (altElement2.element("ID").attributeValue("schemeName").equals("CIF")) {
								altString2 = altElement2.elementText("ID");
							} else if (altElement2.element("ID").attributeValue("schemeName").equals("NIF")) {
								altString2 = altElement2.elementText("ID");
							}
						}
					}
					if (altString == null)
						noPlatformIDCount++;
					if (altString2 == null)
						noNIF_CIFCount++;
					if (altString == null && (altString = altString2) == null) {
						Utils.writeInfile("debug/codice_doc_X.xml", document.asXML());
						System.err.println("WEEEEEEEEEEEERROR");
						System.exit(-1);
					}
					if (altString.length() < 4) {

						// ///////////////////////
						// Aquí empieza el parseo
						// ///////////////////////

						try {
							Codice2Pproc.parseCodiceXML(model, document);
							Utils.writeInfile("debug/codice_doc_" + getCount() + ".xml", document.asXML());
						} catch (Exception ex) {
							Log.e(TAG, "[parseXmls] error parsing xml %d, see debug/codice_doc.xml", xmlLink.id);
							Utils.writeInfile("debug/codice_doc.xml", document.asXML());
							ex.printStackTrace();
							return;
						}
						Log.d(TAG, "[parseXmls] xml %d parsed and inserted in model", xmlLink.id);
						tempXmlLinks.add(xmlLink);
					}
				} catch (DocumentException exc) {
					Log.e(TAG, "[parseXmls] error reading xml %d [%d bytes]", xmlLink.id,
							xmlLink.xml.getBytes(StandardCharsets.UTF_8).length);
					xmlErrorCount++;
				}
			}
			if (tempXmlLinks.size() > 0) {
				Utils.writeModel(
						model,
						String.format("pcsp-output/pcsp-output-%d-%d.ttl",
								tempXmlLinks.toArray(new XmlLink[tempXmlLinks.size()])[0].id,
								tempXmlLinks.toArray(new XmlLink[tempXmlLinks.size()])[tempXmlLinks.size() - 1].id));

				model.removeAll();
				model.close();
				model = ModelFactory.createDefaultModel();
			}
			database.updateFlags(xmlLinks, 6);
			Log.i(TAG, "[parseXmls] %d/%d parsed documents (%d errors so far) (noPlatformIDCount = "
					+ noPlatformIDCount + ") ( noNIF_CIFCount = " + noNIF_CIFCount + ")", tempXmlLinks.size(),
					xmlLinks.size(), xmlErrorCount);
			tempXmlLinks.clear();
			reader = null;
		}
	}

}
