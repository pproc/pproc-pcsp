package es.unizar.pproc;

import es.unizar.pproc.codice.Database;
import es.unizar.pproc.codice.Methods;

public class Main {

	public static void main(String[] args) {
		Log.setLevel(Log.INFO);
		if (args.length < 2) {
			Log.e("pproc-pcsp",
					"need mysql database parameters: jdbc:mysql://(ip):(port)/(database) (username) [password]");
			return;
		}

		Database database = new Database(args[0], args[1], args.length > 2 ? args[2] : null);
		database.connect();
		try {
			// Methods.getXmlLinks(database);
			// Methods.downloadXmls(database);
			// Methods.updateCorruptedXmls(database);
			// Methods.checkDeprecatedXmls(database);
			Methods.parseXmls(database);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.disconnect();
		}
	}

}
