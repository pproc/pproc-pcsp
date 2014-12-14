package es.danielrusa.crawler;

import es.unizar.contsem.Log;

/**
 * This class main method retrieves all CODICE XML documents related to contracts at http://contratacionesdelestado.es/
 * and store them in a database.
 * 
 * @author gesteban, danielrusa
 * @date 2014-12-14
 */
public class Main_Crawler {

	/**
	 * @param args
	 *            args[0] = database/table URL (e.g. jdbc:mysql://localhost:4406/licitaciones) args[1] = username
	 *            args[2] = password
	 */
	public static void main(String[] args) {

		if (args.length != 3) {
			Log.error(Main_Crawler.class,
					"bad arguments, usage: Main_Crawler <database/table URL> <username> <password>");
			return;
		}

		Database database = new Database(args[0], args[1], args[2]);
		database.connect();
		database.exhaustiveSearch = true;
		Log.setLevel(Log.INFO);
		try {
			Crawler.start(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
