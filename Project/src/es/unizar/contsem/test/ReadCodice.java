package es.unizar.contsem.test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import es.unizar.contsem.Utils;
import es.unizar.contsem.parser.Codice2Pproc;

public class ReadCodice {

    public static String URL = "https://contrataciondelestado.es/wps/wcm/connect/PLACE_es/Site/area/docAccCmpnt?srv=cmpnt&cmpntname=GetDocumentsById&source=library&DocumentIdParam=ca62ca02-3f40-4c98-bcd5-a84206e20bdf";

    public static void main(String[] args) throws Exception {

        String xml = Utils.getXML(URL);
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        Model model = ModelFactory.createDefaultModel();
        Codice2Pproc.parseCodiceXML(model, document);
        Utils.writeModel(model, String.format("debug/readCodice.ttl"));

    }

}
