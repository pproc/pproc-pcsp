package es.danielrusa.TFG_crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.unizar.contsem.codice.parser.Log;

public class Crawler {

	private static final String FORM[] = {
			"?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id",
			"%3Aj_id",
			"&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfooterSiguiente=Siguiente&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0" };

	public static String post = null;

	public Crawler() {
		DefaultTrustManager dtm = new DefaultTrustManager();
		DefaultTrustManager.CrearConexionHTTPS();
	}

	public void start(Database database) throws Exception {

		Document document;
		Response response = null;
		String altString, sessionIdCookie, petition;
		String[] ids;
		Elements elements;

		// Get to main web to get sessionIdCookie
		response = Jsoup.connect("https://contrataciondelestado.es/wps/portal/").ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
				.referrer("http://www.google.com").timeout(12000).followRedirects(true).method(Method.GET).execute();
		sessionIdCookie = response.cookies().get("JSESSIONID");
		document = response.parse();
		altString = searchHrefPattern(document, "Búsqueda avanzada de licitaciones");
		Log.info(this.getClass(), "get to main web to get sessionIdCookie successful");

		// Get to advanced search to get paged petition base url
		response = Jsoup.connect(altString).ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
				.referrer("https://contrataciondelestado.es/wps/portal/").timeout(12000).followRedirects(true)
				.cookie("JSESSIONID", sessionIdCookie).method(Method.GET).execute();
		document = response.parse();
		elements = document.select("form[id]");
		for (Element link : elements) {
			if (link.attr("abs:id").contains("viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
				altString = new String(link.attr("abs:action"));
				break;
			}
		}
		Log.info(this.getClass(), "get to advanced search to get paged petition base url successful");

		// Post to first page
		petition = altString.trim()
				+ "?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id1%3Aj_id2&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Abutton1=Buscar&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
		post = petition;
		response = Jsoup.connect(petition).ignoreContentType(true)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0").timeout(12000)
				.followRedirects(true).cookie("JSESSIONID", sessionIdCookie).method(Method.POST).execute();
		document = response.parse();
		ids = getIds(document);
		retrieveDataFromPlatformIds(ids, sessionIdCookie, database);
		petition = getNextPetition(document);
		Log.info(this.getClass(), "first page done");

		// Post to next pages
		while (actualPage(document) <= finalPage(document)) {
			document = retrieveDataFromPage(petition, sessionIdCookie, database);
			double por = ((actualPage(document) * (1.0)) / (finalPage(document) * (1.0))) * 100.0;
			Log.info(this.getClass(), "page %d/%d done - %.4f%%", actualPage(document), finalPage(document), por);
			petition = getNextPetition(document);

		}
	}

	public int actualPage(Document document) {
		String altString = null;
		String[] aux;
		aux = search(
				"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoNumPagMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>",
				document.html());
		for (int i = 0; aux.length > i; i++) {
			altString = aux[i]
					.replaceAll(
							"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoNumPagMAQ\" class=\"outputText marginLeft0punto5\">",
							"");
			altString = altString.replaceAll("</span>", "");
		}
		return Integer.parseInt(altString);
	}

	public int nextPage(Document document) {
		return actualPage(document) + 1;
	}

	public int finalPage(Document document) {
		String altString = null;
		String[] aux;
		aux = search(
				"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>",
				document.html());
		for (int i = 0; aux.length > i; i++) {
			altString = aux[i]
					.replaceAll(
							"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">",
							"");
			altString = altString.replaceAll("</span>", "");
		}
		return Integer.parseInt(altString);
	}

	public Document retrieveDataFromPage(String petition, String sessionIdCookie, Database database) {
		Document document = null;
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
				success = true;
				String[] platformIds = getIds(document);

				if (platformIds.length < 20) {
					PrintWriter writer = new PrintWriter("doc.html", "UTF-8");
					writer.print(document.html());
					writer.close();
					Log.warning(this.getClass(), "MIRA DOC.HTML PARA CONFIRMAR IDS PLATAFORMA");
				}

				retrieveDataFromPlatformIds(platformIds, sessionIdCookie, database);
			} catch (IOException e) {
				Log.warning(this.getClass(), "try number %d at retrieveDataFromPage failed", tryCount);
			} catch (Exception e) {
				Log.error(this.getClass(), "unexpected error at retrieveDataFromPage");
			}
		}
		if (!success)
			Log.error(this.getClass(), "retrieveDataFromPage could not retrieve data");
		return document;
	}

	public void retrieveDataFromPlatformIds(String[] platformIds, String sessionIdCookie, Database database)
			throws Exception {
		int numberOfRowInserted = 0;
		int numberOfPlatformidsAnalized = 0;
		String url = "https://contrataciondelestado.es/wps/portal/!ut/p/b1/jY_LDoIwFES_xQ8wHfoSlqTQFoNKQkDpxrAwBsNjY_x-kcSFC6t3N8k5mbnEkWYdSREiADg5ETe2j-7a3rtpbPtXdvLM04NS2lKEJUtA86SqpJ2jETPQzAC-XIxPH-mOgdqNCVOmgOLtC6Z4va0LWWYGyKxO8ioQMFT-1-8p-OEfiVsQ34IF8L3oL6Fkb6fhQgbXax1lNx6vVk9ghp30/dl4/d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_AVEQAI930OBRD02JPMTPG21004/act/id=0/p=javax.servlet.include.path_info=QCPjspQCPbusquedaQCPMainBusqueda.jsp/254843306643/-/?";
		String parameters = null;
		for (int i = 0; i < platformIds.length; i++) {
			if (!database.platformIdExists(platformIds[i])) {
				numberOfPlatformidsAnalized++;
				parameters = "ACTION_NAME_PARAM=SourceAction&CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&TIPO_LICITACION=0&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&idLicitacion="
						+ platformIds[i].trim()
						+ "&javax.faces.ViewState=j_id11%3Aj_id12&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
				try {
					// Contract web page (platformId), can contain multiple links to xmls
					Response response = Jsoup.connect(url + parameters).ignoreContentType(true)
							.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
							.timeout(12000).followRedirects(true).cookie("JSESSIONID", sessionIdCookie)
							.method(Method.POST).execute();
					Log.debug(this.getClass(), "post to get contract web %s successful ", platformIds[i]);
					Set<Row> rowSet = searchHrefXmlPattern(response.parse(), database, platformIds[i]);
					for (Row row : rowSet)
						database.insertRow(row);
					numberOfRowInserted += rowSet.size();
				} catch (IOException e) {
					Log.error(this.getClass(), "error at retrieveDataFromPlatformIds");
					e.printStackTrace();
				}
			} else
				Log.debug(this.getClass(), "contract web %s already exist", platformIds[i]);
		}
		Log.info(this.getClass(), "%d/%d platformIds analized and %d rows inserted", numberOfPlatformidsAnalized,
				platformIds.length, numberOfRowInserted);
	}

	private static String searchHrefPattern(Document d, String p) {
		Elements links = d.select("a[href]");
		String reference = null;
		for (Element link : links)
			if (trim(link.text(), 35).contains(p))
				reference = link.attr("abs:href");
		return reference;
	}

	private Set<Row> searchHrefXmlPattern(Document document, Database database, String platformId) {
		Set<Row> rowSet = new HashSet<Row>();
		String[] expedientes = search("inputtext31\" value=\".*\" class=\"inputTextMedio\"", document.html());
		String expediente = null;
		for (int i = 0; expedientes.length > i; i++) {
			expediente = expedientes[i].replaceAll("inputtext31\" value=\"", "");
			expediente = expediente.replaceAll("\" class=\"inputTextMedio\"", "");
		}
		Elements elements = document.select("a[href]");
		for (Element element : elements) {
			String link = new String(element.attr("abs:href"));
			String description = new String(trim(element.text(), 35));
			if (description.trim().toLowerCase().contains("xml"))
				if (!database.linkExists(link))
					rowSet.add(new Row(link, expediente, getXML(link).replaceAll("'", "''"), post, platformId));
				else
					Log.debug(this.getClass(), "link %d already exists", link.hashCode());
		}
		return rowSet;
	}

	private String getXML(String urlToRead) {
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
				Log.warning(this.getClass(), "try number %d at getXML failed", tryCount);
			}
		}
		if (!success)
			Log.error(this.getClass(), "getXML could not retrieve the XML");
		return result2;
	}

	private String[] getIds(Document doc) {
		String[] matches = search("'idLicitacion':'[0-9]+'", doc.html());
		for (int i = 0; i < matches.length; i++) {
			matches[i] = matches[i].replaceAll("'idLicitacion':'", "").replaceAll("'", "");
		}
		return matches;
	}

	private String getNextPetition(Document document) {
		String form = null, petition;
		for (Element element : document.select("form[id]")) {
			if (element.attr("abs:id").contains("viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
				form = new String(element.attr("abs:action"));
				break;
			}
		}
		// Page stuff
		String[] pag = { "", "" };
		pag = search("j_id[\\d]*", document.html());
		for (int i = 0; pag.length > i; i++)
			pag[i] = pag[i].replaceAll("j_id", "");
		try {
			petition = form + FORM[0] + pag[0] + FORM[1] + pag[1] + FORM[2];
			Log.debug(this.getClass(), "nextPetition pages %s - %s", pag[0], pag[1]);
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			Log.error(this.getClass(), "ArrayIndexOutOfBoundsException at getNextPetition");
			return null;
		}
		return petition;
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

	private static String[] search(String regex, String onText) {
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
