package es.unizar.contsem.crawler;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import es.unizar.contsem.Database;
import es.unizar.contsem.Log;
import es.unizar.contsem.Utils;

public class CrawlerMethods {

	private static final int MAX_BUFFER_GET = 50;
	private static final int MAX_BUFFER_UPDATE = 500;
	private static final int MAX_BUFFER_CHECK = 100;
	private static final int DELAY_DOWNLOAD_XML = 1000;
	private static final String FORM_NEXTPAGE[] = {
			"?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id",
			"%3Aj_id",
			"&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfooterSiguiente=Siguiente&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0" };
	private static final String FIRST_PETITION = "https://contrataciondelestado.es/wps/portal/";

	private static int actualPage(Document doc) {
		String altString = null;
		String[] aux;
		aux = Utils
				.search("span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoNumPagMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>",
						doc.html());
		for (int i = 0; aux.length > i; i++) {
			altString = aux[i]
					.replaceAll(
							"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoNumPagMAQ\" class=\"outputText marginLeft0punto5\">",
							"");
			altString = altString.replaceAll("</span>", "");
		}
		try {
			return Integer.parseInt(altString);
		} catch (Exception ex) {
			return -1;
		}
	}

	private static int finalPage(Document doc) {
		String altString = null;
		String[] aux;
		aux = Utils
				.search("span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>",
						doc.html());
		for (int i = 0; aux.length > i; i++) {
			altString = aux[i]
					.replaceAll(
							"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">",
							"");
			altString = altString.replaceAll("</span>", "");
		}
		try {
			return Integer.parseInt(altString);
		} catch (Exception ex) {
			return -1;
		}
	}

	private static String getAdvSearchPetition(Document doc) {
		Elements links = doc.select("a[href]");
		String reference = null;
		for (Element link : links) {
			String strLink = link.text();
			if (strLink.length() > 35)
				strLink = strLink.substring(0, 34) + ".";
			if (strLink.contains("Búsqueda avanzada de licitaciones"))
				reference = link.attr("abs:href");
		}
		return reference;
	}

	private static String getPagedBasePetition(Document doc) {
		Elements elements = doc.select("form[id]");
		for (Element link : elements)
			if (link.attr("abs:id").contains(
					"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1"))
				return link.attr("abs:action");
		return null;
	}

	private static String getNextPagePetition(Document doc) {
		String form = null, petition;
		for (Element element : doc.select("form[id]"))
			if (element.attr("abs:id").contains(
					"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
				form = new String(element.attr("abs:action"));
				break;
			}
		// Page stuff
		String[] pag = { "", "" };
		pag = Utils.search("j_id[\\d]*", doc.html());
		for (int i = 0; pag.length > i; i++)
			pag[i] = pag[i].replaceAll("j_id", "");
		try {
			petition = form + FORM_NEXTPAGE[0] + pag[0] + FORM_NEXTPAGE[1]
					+ pag[1] + FORM_NEXTPAGE[2];
			Log.debug(CrawlerMethods.class,
					"[getNextPagePetition] nextPetition pages %s - %s", pag[0],
					pag[1]);
		} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
			Log.error(CrawlerMethods.class,
					"[getNextPagePetition] ArrayIndexOutOfBoundsException");
			return null;
		}
		return petition;
	}

	private static String getContractPetition(String id) {
		String url = "https://contrataciondelestado.es/wps/portal/!ut/p/b1/jY_LDoIwFES_xQ8wHfoSlqTQFoNKQkDpxrAwBsNjY_x-kcSFC6t3N8k5mbnEkWYdSREiADg5ETe2j-7a3rtpbPtXdvLM04NS2lKEJUtA86SqpJ2jETPQzAC-XIxPH-mOgdqNCVOmgOLtC6Z4va0LWWYGyKxO8ioQMFT-1-8p-OEfiVsQ34IF8L3oL6Fkb6fhQgbXax1lNx6vVk9ghp30/dl4/d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_AVEQAI930OBRD02JPMTPG21004/act/id=0/p=javax.servlet.include.path_info=QCPjspQCPbusquedaQCPMainBusqueda.jsp/254843306643/-/?";
		String parameters = null;
		parameters = "ACTION_NAME_PARAM=SourceAction&CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&TIPO_LICITACION=0&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&idLicitacion="
				+ id.trim()
				+ "&javax.faces.ViewState=j_id11%3Aj_id12&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
		return url + parameters;
	}

	private static Set<String> getLinks(Document doc) {
		Set<String> stringSet = new HashSet<String>();
		String[] expedientes = Utils.search(
				"inputtext31\" value=\".*\" class=\"inputTextMedio\"",
				doc.html());
		String expediente = null;
		for (int i = 0; expedientes.length > i; i++) {
			expediente = expedientes[i]
					.replaceAll("inputtext31\" value=\"", "");
			expediente = expediente.replaceAll("\" class=\"inputTextMedio\"",
					"");
		}
		Elements elements = doc.select("a[href]");
		for (Element element : elements) {
			String description = element.text();
			if (description.length() > 35)
				description = description.substring(0, 34) + ".";
			if (description.trim().toLowerCase().contains("xml"))
				stringSet.add(element.attr("abs:href"));
		}
		return stringSet;
	}

	private static Set<String> getIds(Document doc) {
		Set<String> ids = new HashSet<String>();
		String[] matches = Utils.search("'idLicitacion':'[0-9]+'", doc.html());
		for (int i = 0; i < matches.length; i++)
			ids.add(matches[i].replaceAll("'idLicitacion':'", "").replaceAll(
					"'", ""));
		// caso ultima pagina o error de parseo
		if (matches.length < 20) {
			Utils.writeInfile("debug/doc.html", doc.html());
			Log.warning(CrawlerMethods.class,
					"MIRA DOC.HTML PARA CONFIRMAR IDS PLATAFORMA");
		}
		return ids;
	}

	private static Document retrieveDocument(String petition,
			String sessionIdCookie) {
		Document document = null;
		boolean success = false;
		int tryCount = 0;
		while (!success && tryCount < Utils.MAX_TRY_COUNT) {
			try {
				tryCount++;
				Response response = Jsoup
						.connect(petition)
						.ignoreContentType(true)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
						.timeout(12000).followRedirects(true)
						.cookie("JSESSIONID", sessionIdCookie)
						.method(Method.POST).execute();
				document = response.parse();
				success = true;
			} catch (IOException e) {
				Log.warning(
						CrawlerMethods.class,
						"[retrieveDocument] getNextDocument try number %d failed",
						tryCount);
			} catch (Exception e) {
				Log.error(CrawlerMethods.class,
						"[retrieveDocument] getNextDocument unexpected error");
				e.printStackTrace();
			}
		}
		if (!success)
			Log.error(CrawlerMethods.class,
					"getNextDocument could not retrieve data");
		return document;
	}

	private static String getUUID(org.dom4j.Document doc) {
		return doc.getRootElement().elementText("UUID").replace("-", "").trim();
	}

	/**
	 * Gets XML/CODICE links from http://contrataciondelestado.es/ and store
	 * them in a database.
	 * 
	 * @author gesteban, danielrusa
	 * @see es.unizar.contsem.Database
	 */
	public static void getXmlLinks(Database database) throws IOException {

		DefaultTrustManager.CrearConexionHTTPS();
		Document document, altDocument;
		Response response;
		String sessionIdCookie, petition;

		// Get to main web to get sessionIdCookie
		response = Jsoup
				.connect(FIRST_PETITION)
				.ignoreContentType(true)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
				.referrer("http://www.google.com").timeout(12000)
				.followRedirects(true).method(Method.GET).execute();
		sessionIdCookie = response.cookies().get("JSESSIONID");
		document = response.parse();
		petition = getAdvSearchPetition(document);
		Log.info(CrawlerMethods.class,
				"[getXmlLinks] get to main web to get sessionIdCookie successful");

		// Get to advanced search to get paged petition base url
		response = Jsoup
				.connect(petition)
				.ignoreContentType(true)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
				.referrer("https://contrataciondelestado.es/wps/portal/")
				.timeout(12000).followRedirects(true)
				.cookie("JSESSIONID", sessionIdCookie).method(Method.GET)
				.execute();
		document = response.parse();
		petition = getPagedBasePetition(document);
		Log.info(
				CrawlerMethods.class,
				"[getXmlLinks] get to advanced search to get paged petition base url successful");

		// Post to first page
		petition = petition.trim()
				+ "?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id1%3Aj_id2&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Abutton1=Buscar&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
		response = Jsoup
				.connect(petition)
				.ignoreContentType(true)
				.userAgent(
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
				.timeout(12000).followRedirects(true)
				.cookie("JSESSIONID", sessionIdCookie).method(Method.POST)
				.execute();
		document = response.parse();
		for (String id : getIds(document)) {
			petition = getContractPetition(id);
			altDocument = retrieveDocument(petition, sessionIdCookie);
			database.insertLinks(getLinks(altDocument));
			// links.addAll(getLinks(document));
		}
		Log.info(CrawlerMethods.class, "[getXmlLinks] first page done");

		// Post to next pages
		int numberOfErrors = 0;
		while (actualPage(document) < finalPage(document) && numberOfErrors < 3) {
			if (actualPage(document) != -1 && finalPage(document) != -1)
				petition = getNextPagePetition(document);
			else
				Log.error(
						CrawlerMethods.class,
						"[getXmlLinks] web document incorrect, trying same last petition again (%d)",
						++numberOfErrors);
			document = retrieveDocument(petition, sessionIdCookie);
			for (String id : getIds(document)) {
				petition = getContractPetition(id);
				altDocument = retrieveDocument(petition, sessionIdCookie);
				database.insertLinks(getLinks(altDocument));
				// links.addAll(getLinks(document));
			}
			double por = ((actualPage(document) * (1.0)) / (finalPage(document) * (1.0))) * 100.0;
			Log.info(CrawlerMethods.class,
					"[getXmlLinks] page %d/%d done - %.4f%%",
					actualPage(document), finalPage(document), por);
		}
	}

	/**
	 * Download XML/CODICE files from http://contrataciondelestado.es/ and store
	 * them in a database.
	 * 
	 * @author gesteban
	 * @see es.unizar.contsem.Database
	 */
	public static void downloadXmls(Database database)
			throws FileNotFoundException, UnsupportedEncodingException,
			InterruptedException {
		Set<XmlLink> xmlLinks = database.selectByLimit(false, 0,
				Database.FLAG_ONLY_LINK);
		Set<XmlLink> tempXmlLinks = new HashSet<XmlLink>();
		int tempCount = 0;
		String xml;
		long startTime = System.currentTimeMillis();
		for (Iterator<XmlLink> iter = xmlLinks.iterator(); iter.hasNext();) {
			XmlLink xmlLink = iter.next();
			tempCount++;
			xml = Utils.getXML(xmlLink.link);
			xmlLink.xml = xml;
			tempXmlLinks.add(xmlLink);
			if (tempCount == MAX_BUFFER_GET) {
				Log.info(
						CrawlerMethods.class,
						"[downloadXmls] takes %f seconds to download %d xmls",
						(double) (System.currentTimeMillis() - startTime) / 1000,
						tempXmlLinks.size());
				database.updateXmls(tempXmlLinks);
				database.updateFlags(tempXmlLinks, Database.FLAG_XML_UNCHECKED);
				tempCount = 0;
				tempXmlLinks.clear();
				startTime = System.currentTimeMillis();
			}
			iter.remove();
			Thread.sleep(DELAY_DOWNLOAD_XML);
		}
	}

	/**
	 * Check the XML/CODICE files stored in a database to assure they are
	 * correct, if some XML/CODICE is not correct, it's retrieved again using
	 * the link stored in the database.
	 * 
	 * Is assumed that a CODICE XML is incorrect when SaxReader throws an
	 * Exception.
	 * 
	 * @author gesteban
	 * @see es.unizar.contsem.Database
	 */
	public static void updateCorruptedXmls(Database database)
			throws InterruptedException, DocumentException {
		int errorCount = 0;
		SAXReader reader = new SAXReader();
		org.dom4j.Document document;
		Set<XmlLink> xmlLinks = database.selectByLimit(true, MAX_BUFFER_UPDATE,
				Database.FLAG_XML_UNCHECKED);
		while (xmlLinks.size() > 0) {
			Set<XmlLink> xmlLinksToUpdate = new HashSet<XmlLink>();
			for (XmlLink xmlLink : xmlLinks)
				try {
					document = reader.read(new ByteArrayInputStream(xmlLink.xml
							.getBytes(StandardCharsets.UTF_8)));
					xmlLinksToUpdate.add(xmlLink);
				} catch (org.dom4j.DocumentException ex) {
					errorCount++;
					String xml = Utils.getXML(xmlLink.link);
					Thread.sleep(1000);
					try {
						document = reader.read(new ByteArrayInputStream(
								xmlLink.xml.getBytes(StandardCharsets.UTF_8)));
						xmlLink.xml = xml;
						xmlLinksToUpdate.add(xmlLink);
						database.updateXml(xmlLink);
					} catch (org.dom4j.DocumentException ex2) {
						Log.error(CrawlerMethods.class,
								"[updateCorruptedXmls] %d still corrupted",
								xmlLink.id);
						database.updateFlag(xmlLink,
								Database.FLAG_XML_INCORRECT);
					}
				}
			for (XmlLink xmlLink : xmlLinksToUpdate) {
				document = reader.read(new ByteArrayInputStream(xmlLink.xml
						.getBytes(StandardCharsets.UTF_8)));
				try {
					xmlLink.uuid = getUUID(document);
				} catch (Exception ex) {
					Utils.writeInfile("debug/codice.xml", xmlLink.xml);
					Log.error(CrawlerMethods.class,
							"[updateCorruptedXmls] error, exiting", xmlLink.id);
					System.exit(-1);
				}
				database.updateUUID(xmlLink);
			}
			database.updateFlags(xmlLinksToUpdate, Database.FLAG_UUID_UNCHECKED);
			xmlLinksToUpdate.clear();
			xmlLinks = database.selectByLimit(true, MAX_BUFFER_UPDATE,
					Database.FLAG_XML_UNCHECKED);
		}
		Log.info(CrawlerMethods.class, "[updateCorruptedXmls] errorCount = "
				+ errorCount);
	}

	/**
	 * Check the XML/CODICE files stored in a database and look for deprecated
	 * documents, these being the XML/CODICE documents that have been replaced
	 * for a new version with corrections.
	 * 
	 * @author gesteban
	 * @see es.unizar.contsem.Database
	 */
	public static void checkDeprecatedXmls(Database database)
			throws InterruptedException, DocumentException {
		Set<XmlLink> xmlLinks = database.selectByLimit(true, MAX_BUFFER_CHECK,
				Database.FLAG_UUID_UNCHECKED,
				Database.FLAG_UUID_UNCHECKED_DEPRECATED);
		org.dom4j.Document document = null;
		SAXReader reader = new SAXReader();
		try {
			while (xmlLinks.size() > 0) {
				// Set of XmlLinks to mark as deprecated
				Set<String> uuidsDeprecated = new HashSet<String>();
				// Subset of uuidsDeprecated containing the XmlLinks already
				// deprecated when this method started
				Set<XmlLink> altSetWithDeprecatedChecked = new HashSet<XmlLink>();
				for (Iterator<XmlLink> iter = xmlLinks.iterator(); iter
						.hasNext();) {
					XmlLink xmlLink = iter.next();
					document = reader.read(new ByteArrayInputStream(xmlLink.xml
							.getBytes(StandardCharsets.UTF_8)));
					if (document.getRootElement().element("UBLExtensions") != null) {
						org.dom4j.Element altElement = (org.dom4j.Element) document
								.getRootElement().element("UBLExtensions")
								.element("UBLExtension")
								.element("ExtensionContent").elements().get(0);
						switch (altElement.getName()) {
						case "difference":
							altElement = altElement.element("corrections");
							boolean success = false;
							for (Iterator<?> iter2 = altElement
									.elementIterator("correction"); iter2
									.hasNext();) {
								org.dom4j.Element altElement2 = (org.dom4j.Element) iter2
										.next();
								if (altElement2
										.attributeValue("location")
										.contains(
												document.getRootElement()
														.getName()
														+ "[1]/UUID[1]/text()[1]")) {
									uuidsDeprecated.add(altElement2.element(
											"old").elementText("UUID"));
									if (xmlLink.flag == Database.FLAG_UUID_UNCHECKED_DEPRECATED) {
										altSetWithDeprecatedChecked
												.add(xmlLink);
										iter.remove();
									}
									success = true;
								}
							}
							if (!success) {
								Log.error(CrawlerMethods.class,
										"[checkDeprecatedXmls] unexpected miss element");
								Utils.writeInfile("debug/codice.xml",
										document.asXML());
								System.exit(-1);
							}
							break;
						case "NoticeCancellation":
							uuidsDeprecated.add(altElement
									.elementText("ReferencedUUID"));
							if (xmlLink.flag == Database.FLAG_UUID_UNCHECKED_DEPRECATED) {
								altSetWithDeprecatedChecked.add(xmlLink);
								iter.remove();
							}
							break;
						default:
							Log.warning(
									CrawlerMethods.class,
									"[checkDeprecatedXmls] UBLExtension child %s not supported",
									altElement.getName());
						}
					}
				}
				// Selecting the XmlLinks that have to be marked as deprecated
				Set<XmlLink> newlyDeprecated = database.selectByUuids(false,
						uuidsDeprecated);
				// Setting flag as deprecated only if flag is 2 or 3
				for (XmlLink xmlLink : newlyDeprecated)
					if (xmlLink.flag > 1)
						database.updateFlag(xmlLink, -xmlLink.flag);
				// Marking all XmlLinks parsed as checked and valid (though is
				// not correct, fixed in next line)
				database.updateFlags(xmlLinks, Database.FLAG_CHECKED_VALID);
				// Marking all XmlLinks already deprecated when this method
				// started as checked and deprecated
				database.updateFlags(altSetWithDeprecatedChecked,
						Database.FLAG_CHECKED_DEPRECATED);
				Log.info(
						CrawlerMethods.class,
						"[checkDeprecatedXmls] checked %d xmls: %d checked/valid, %d checked/deprecated, %d new deprecated",
						xmlLinks.size() + altSetWithDeprecatedChecked.size(),
						xmlLinks.size(), altSetWithDeprecatedChecked.size(),
						newlyDeprecated.size());
				altSetWithDeprecatedChecked.clear();
				uuidsDeprecated.clear();
				xmlLinks = database.selectByLimit(true, MAX_BUFFER_CHECK,
						Database.FLAG_UUID_UNCHECKED,
						Database.FLAG_UUID_UNCHECKED_DEPRECATED);
			}
		} catch (Exception ex) {
			Utils.writeInfile("debug/codice.xml", document.asXML());
			Log.error(CrawlerMethods.class,
					"[checkDeprecatedXmls] codice xml saved");
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Log.setLevel(Log.INFO);
		if (args.length != 3) {
			Log.error(CrawlerMethods.class,
					"need database parameters: database/table_URL username password");
			return;
		}

		Database database = new Database(args[0], args[1], args[2]);
		database.connect();
		try {
			// CrawlerMethods.getXmlLinks(database);
			// CrawlerMethods.downloadXmls(database);
			// CrawlerMethods.updateCorruptedXmls(database);
			CrawlerMethods.checkDeprecatedXmls(database);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			database.disconnect();
		}
	}
}
