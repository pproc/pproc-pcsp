package es.danielrusa.crawler;

import es.unizar.contsem.Log;

public class Main {

	public static void main(String[] args) {
		Database database = new Database();
		database.connect();
		database.exhaustiveSearch = false;
		Crawler el = new Crawler();
		try {
			Log.setLevel(Log.INFO);
			el.start(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
