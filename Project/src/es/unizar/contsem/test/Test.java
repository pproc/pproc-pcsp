package es.unizar.contsem.test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import es.unizar.contsem.Database;
import es.unizar.contsem.Log;
import es.unizar.contsem.Utils;
import es.unizar.contsem.XmlLink;

public class Test {

    public static String printThis = "";
    public static Map<String, Integer> propertyCount = new HashMap<String, Integer>();
    public static Map<String, String> propertyExample = new HashMap<String, String>();

    public static void start(Database database) {

        // Parse
        SAXReader reader = new SAXReader();
        for (int i = 1000; i < 10000; i = i + 1000) {
            Set<XmlLink> xmlLinks = database.getLinksByFlag(1, i);
            for (XmlLink xmlLink : xmlLinks)
                try {
                    Document document = reader.read(new ByteArrayInputStream(xmlLink.xml
                            .getBytes(StandardCharsets.UTF_8)));
                    countProperties("", document.getRootElement());
                } catch (DocumentException e) {
                    Log.error(Test.class, "error parsing codice doc");
                }
        }

        // Print property count
        String output = "";
        List<String> keyList = new ArrayList<String>(propertyCount.keySet());
        Collections.sort(keyList);
        for (String key : keyList)
            output += key + " - " + propertyCount.get(key) + " - " + propertyExample.get(key) + "\n";
        Utils.writeInfile("debug/propertyCount.txt", output);
        database.disconnect();
    }

    public static void countProperties(String parentPath, Element rootElement) {
        if (rootElement == null)
            return;
        for (Iterator iter = rootElement.elementIterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            Integer previousCount = propertyCount.get(parentPath + "/" + element.getName());
            propertyCount.put(parentPath + "/" + element.getName(), previousCount == null ? 1 : previousCount + 1);
            if (propertyExample.get(parentPath + "/" + element.getName()) == null && !element.getTextTrim().isEmpty())
                propertyExample.put(parentPath + "/" + element.getName(), element.getTextTrim());
            if (!printThis.equals("") && printThis.equals(parentPath + "/" + element.getName())
                    && !element.getTextTrim().isEmpty()) {
                System.out.println(element.getText());
            }
            countProperties(parentPath + "/" + element.getName(), element);
        }
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
            Log.error(Test.class, "usage: Test database/table_URL username password");
            return;
        }

        Database database = new Database(args[0], args[1], args[2]);
        database.connect();
        Log.setLevel(Log.DEBUG);
        try {
            Test.start(database);
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.disconnect();
    }

}
