package es.danielrusa.crawler;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import es.unizar.contsem.Log;

/**
 * This class main method check all retrieved CODICE XML from the database to assure they are correct, if some CODICE
 * XML is not correct, it's retrieved again using the petition URL stored in the database.
 * 
 * Is assumed that a CODICE XML of 4724 bytes of length is incorrect.
 * 
 * @author gesteban
 * @date 2014-12-14
 */
public class Main_UpdateXml {

	/**
	 * @param args
	 *            args[0] = database/table URL (e.g. jdbc:mysql://localhost:4406/licitaciones) args[1] = username
	 *            args[2] = password
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			Log.error(Main_UpdateXml.class,
					"bad arguments, usage: Main_UpdateXml <database/table URL> <username> <password>");
			return;
		}

		int startId = 1;
		int sizeOfQuery = 3000;
		int endId = Integer.MAX_VALUE;
		Database database = new Database(args[0], args[1], args[2]);
		database.connect();
		Set<Row> rowSet;
		int errorCount = 0;

		for (;;) {
			rowSet = database.getRows(startId, (startId + sizeOfQuery > endId ? endId : startId + sizeOfQuery));
			for (Row row : rowSet)
				if (row.xml.getBytes(StandardCharsets.UTF_8).length == 4724) {
					errorCount++;
					String xml = Crawler.getXML(row.link);
					if (xml.getBytes(StandardCharsets.UTF_8).length != 4724) {
						row.xml = xml;
						database.updateRow(row);
					} else
						Log.error(Main_UpdateXml.class, "de nuevo 4724 en %d" + row.id);
				}
			startId = startId + sizeOfQuery;
			if (rowSet.size() < sizeOfQuery || startId >= endId)
				break;
		}

		database.disconnect();
		Log.info(Main_UpdateXml.class, "errorCount = " + errorCount);

	}
}
