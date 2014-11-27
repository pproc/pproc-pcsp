package es.danielrusa.TFG_crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.unizar.contsem.codice.parser.Log;

public class ExtraerLicitaciones {

	private static final String FORM[] = {
			"?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id",
			"%3Aj_id",
			"&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfooterSiguiente=Siguiente&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0" };

	public static Integer actualPage, finalPage = null;
	public static String post = null;

	public ExtraerLicitaciones() {
		DefaultTrustManager dtm = new DefaultTrustManager();
		DefaultTrustManager.CrearConexionHTTPS();
	}

	public void start(Database database, String urlPortal, Estadisticas statistics) {

		Document document;
		Response response = null;
		String altString, sessionIdCookie, petition;
		String[] ids;
		Elements elements;

		try {

			// Get to main web to get sessionIdCookie
			response = Jsoup.connect(urlPortal).ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.referrer("http://www.google.com").timeout(12000).followRedirects(true).method(Method.GET)
					.execute();
			sessionIdCookie = response.cookies().get("JSESSIONID");
			document = response.parse();
			Log.info("get to main web to get sessionIdCookie successful");
			altString = buscarPatronHref(document, "Búsqueda avanzada de licitaciones");

			// Get to advanced search to get paged petition base url

			response = Jsoup.connect(altString).ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.referrer(urlPortal).timeout(12000).followRedirects(true).cookie("JSESSIONID", sessionIdCookie)
					.method(Method.GET).execute();
			document = response.parse();
			Log.info("get to advanced search to get paged petition base url successful");
			elements = document.select("form[id]");
			for (Element link : elements) {
				if (link.attr("abs:id").contains("viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
					// Get link
					altString = new String(link.attr("abs:action"));
					break;
				}
			}

			// Post to first page
			petition = altString.trim()
					+ "?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id1%3Aj_id2&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Abutton1=Buscar&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
			post = petition;
			response = Jsoup.connect(petition).ignoreContentType(true)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.timeout(12000).followRedirects(true).cookie("JSESSIONID", sessionIdCookie).method(Method.POST)
					.execute();
			document = response.parse();
			Log.info("post to first page successful");
			ids = getIds(document);
			buscarLicitaciones(ids, sessionIdCookie, statistics, database);
			statistics.incrementarPaginasVisitadas();
			while (!fin(document, statistics)) {
				document = iterarPaginas(document, sessionIdCookie, statistics, database);
			}
		} catch (Exception e) {
			Log.error("error en start");
			e.printStackTrace();
		}
	}

	public boolean fin(Document document, Estadisticas statistics) {
		boolean fin = false;
		String pFinal = null;
		String[] aux;
		actualPage = statistics.getPaginasVisitadas();

		// Get final page
		if (finalPage == null) {
			aux = search(
					"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>",
					document.html());
			for (int i = 0; aux.length > i; i++) {
				pFinal = aux[i]
						.replaceAll(
								"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">",
								"");
				pFinal = pFinal.replaceAll("</span>", "");
			}
			finalPage = Integer.parseInt(pFinal);
		}

		// Printing actual state
		double percentage = ((actualPage * (1.0)) / (finalPage * (1.0))) * 100.0;
		Log.info("[page %04d/%04d] [completed %.4f%%]", actualPage, finalPage, percentage);
		return fin;
	}

	public Document iterarPaginas(Document doc, String sessionIdCookie, Estadisticas statistics, Database database) {
		Elements elements = doc.select("form[id]");
		// ////

		try {
			PrintWriter writer;
			writer = new PrintWriter("doc.html", "UTF-8");
			writer.print(doc.html());
			writer.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// ////

		String form = null, petition;
		Document document = null;
		for (Element element : elements) {
			if (element.attr("abs:id").contains("viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
				// Get link
				form = new String(element.attr("abs:action"));
				break;
			}
		}
		String[] pag = search("j_id[\\d]*", doc.html());
		for (int i = 0; pag.length > i; i++) {
			pag[i] = pag[i].replaceAll("j_id", "");
		}
		try {
			petition = form + FORM[0] + pag[0] + FORM[1] + pag[1] + FORM[2];
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			Log.error("ArrayIndexOutOfBoundsException en iterarPaginas");
			return doc;
		}
		post = petition;
		boolean success = false;
		int tryCount = 0;
		while (!success && tryCount < 3) {
			try {
				tryCount++;
				Response response = Jsoup.connect(petition).ignoreContentType(true)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
						.timeout(12000).followRedirects(true).cookie("JSESSIONID", sessionIdCookie).method(Method.POST)
						.execute();
				document = response.parse();
				statistics.incrementarPaginasVisitadas();
				success = true;
				String[] ids = getIds(document);
				buscarLicitaciones(ids, sessionIdCookie, statistics, database);
			} catch (IOException e) {
				Log.warning("intento número %d en iterarPaginas", tryCount);
			} catch (Exception e) {
				Log.error("ERROR INESPERADO en iterarPaginas");
			}
		}
		if (!success) {
			Log.error("tres errores consecutivos en iterarPaginas");
			return null;
		}
		return document;
	}

	public void buscarLicitaciones(String[] ids, String sessionIdCookie, Estadisticas statistics, Database database)
			throws Exception {
		String url = "https://contrataciondelestado.es/wps/portal/!ut/p/b1/jY_LDoIwFES_xQ8wHfoSlqTQFoNKQkDpxrAwBsNjY_x-kcSFC6t3N8k5mbnEkWYdSREiADg5ETe2j-7a3rtpbPtXdvLM04NS2lKEJUtA86SqpJ2jETPQzAC-XIxPH-mOgdqNCVOmgOLtC6Z4va0LWWYGyKxO8ioQMFT-1-8p-OEfiVsQ34IF8L3oL6Fkb6fhQgbXax1lNx6vVk9ghp30/dl4/d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_AVEQAI930OBRD02JPMTPG21004/act/id=0/p=javax.servlet.include.path_info=QCPjspQCPbusquedaQCPMainBusqueda.jsp/254843306643/-/?";
		String parametros = null;
		for (int i = 0; i < ids.length; i++) {
			if (!database.idplataformExists(ids[i])) {
				parametros = "ACTION_NAME_PARAM=SourceAction&CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&TIPO_LICITACION=0&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&idLicitacion="
						+ ids[i].trim()
						+ "&javax.faces.ViewState=j_id11%3Aj_id12&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
				try {
					// Web de la licitación, única por licitación, puede tener
					// varios XMLs
					Response respuesta = Jsoup.connect(url + parametros).ignoreContentType(true)
							.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
							.timeout(12000).followRedirects(true).cookie("JSESSIONID", sessionIdCookie)
							.method(Method.POST).execute();
					Log.debug("post to get contract web %s successful ", ids[i]);
					buscarPatronHrefXml(respuesta.parse(), statistics, database, ids[i]);
				} catch (IOException e) {
					Log.error("error en buscarLicitaciones");
					e.printStackTrace();
				}
			} else {
				Log.debug("contract web %s already exist", ids[i]);
			}
		}
	}

	public static String buscarPatronHref(Document d, String p) {
		Elements links = d.select("a[href]");
		String referencia = null;
		for (Element link : links) {
			String enlace = new String(link.attr("abs:href"));
			String descripcion = new String(trim(link.text(), 35));
			if (descripcion.contains(p))
				referencia = enlace;
		}
		return referencia;
	}

	public ArrayList<String> buscarPatronHrefXml(Document document, Estadisticas statistics, Database database,
			String id) {
		String[] expedientes = search("inputtext31\" value=\".*\" class=\"inputTextMedio\"", document.html());
		String expediente = null;
		for (int i = 0; expedientes.length > i; i++) {
			expediente = expedientes[i].replaceAll("inputtext31\" value=\"", "");
			expediente = expediente.replaceAll("\" class=\"inputTextMedio\"", "");
		}
		Elements elements = document.select("a[href]");
		ArrayList<String> encontrados = new ArrayList<>();
		for (Element element : elements) {
			String link = new String(element.attr("abs:href"));
			String descripcion = new String(trim(element.text(), 35));
			element.attr("");
			if (descripcion.trim().toLowerCase().contains("xml")) {
				encontrados.add(link);
				if (!statistics.getXml().containsKey(link)) {
					statistics.añadirEnlace(link, expediente);
					statistics.incrementarEnlacesCapturados();
					String xml = getXML(link).replaceAll("'", "''");
					database.insertXML(link, expediente, xml, post, id);
				} else {
					// Podría ser causado por que aparece un contrato nuevo
					// mientras se parsea
					Log.warning("link duplicado [%s]", link);
				}
			}
		}
		return encontrados;
	}

	public static String getXML(String urlToRead) {
		DefaultTrustManager dtm = new DefaultTrustManager();
		DefaultTrustManager.CrearConexionHTTPS();
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line, result = "", result2 = "";
		boolean success = false;
		int tryCount = 0;
		while (!success && tryCount < 3) {
			tryCount++;
			try {
				url = new URL(urlToRead);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null)
					result += line;
				String[] url2 = search("url='.*'", result);
				String newURL = null;
				if (url2.length > 0) {
					newURL = url2[0].replaceAll("url='", "https://contrataciondelestado.es");
				}
				newURL = newURL.replaceAll("'", "");
				url = new URL(newURL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null)
					result2 += line;
				rd.close();
				result2.replaceAll("'", "''");
				success = true;
			} catch (Exception e) {
				Log.warning("intento número %d en getXML", tryCount);
			}
		}
		if (!success) {
			Log.error("ERROR GRAVE tres errores consecutivos en getXML");
		}
		return result2;
	}

	public String[] getIds(Document doc) {
		String[] matches = search("idLicitacion':'\\d\\d\\d\\d\\d\\d\\d\\d", doc.html());
		for (int i = 0; i < matches.length; i++) {
			matches[i] = matches[i].replaceAll("idLicitacion':'", "");
		}
		return matches;
	}

	public static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

	public static String[] search(String regex, String onText) {
		Pattern regPatt = Pattern.compile(regex);
		Matcher regMatch = regPatt.matcher(onText);
		ArrayList<String> matches = new ArrayList<>();
		String[] matchesArray;
		while (regMatch.find()) {
			String match = regMatch.group();
			matches.add(match);
		}
		matchesArray = new String[matches.size()];
		matches.toArray(matchesArray);
		return matchesArray;
	}
}
