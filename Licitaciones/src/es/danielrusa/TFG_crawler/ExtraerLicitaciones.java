package es.danielrusa.TFG_crawler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtraerLicitaciones {

	public static int PaginaActual, PaginaFinal;
	public static String post = null;

	public ExtraerLicitaciones() {
		DefaultTrustManager dtm = new DefaultTrustManager();
		dtm.CrearConexionHTTPS();
	}

	public String accederPrimeraPaginaLicitaciones(BaseDatos bd, String bas,
			String url, Map<String, String> cookies, Estadisticas estadisticas) {

		String base = bas;
		String location = url;
		Document doc;
		Response response = null;
		String respuesta = null;
		String id;
		String[] l;
		try {

			/*
			 * Primer get sobre pagina principal
			 */
			// System.out.println("\n\n====================================================");
			// System.out.println("Fase de acceso a la pagina principal de licitaciones");
			// System.out.println("====================================================\n\n");

			// System.out.println("Realizo get sobre:    "+location);
			response = Jsoup
					.connect(location)
					.ignoreContentType(true)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.referrer("http://www.google.com").timeout(12000)
					.followRedirects(true).method(Method.GET).execute();
			cookies = response.cookies(); // Capturo ID_SESSION
			id = cookies.get("JSESSIONID");
			// System.out.println("ID de sesion ="+id);

			doc = response.parse();
			respuesta = buscarPatronHref(doc,
					"Búsqueda avanzada de licitaciones");
			// System.out.println("Realizo get sobre:    "+respuesta);

			// /// Segundo Get sobre enlace de Busqueda avanzada

			response = Jsoup
					.connect(respuesta)
					.ignoreContentType(true)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.referrer(url).timeout(12000).followRedirects(true)
					.cookie("JSESSIONID", id).method(Method.GET).execute();

			cookies = response.cookies(); // Capturo ID_SESSION

			String resAux = respuesta;

			doc = response.parse();
			// System.out.println("\n\n\n+++++++++++++++++++++++++Aqui empieza+++++++++++++++++++++++++++++++++++\n\n\n");
			// System.out.println(doc.html());
			// System.out.println("\n\n\n++++++++++++++++++++++++++++++Aqui termina++++++++++++++++++++++++++++++\n\n\n");
			Elements links = doc.select("form[id]");

			for (Element link : links) {
				if (link.attr("abs:id").contains(
						"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
					respuesta = new String(link.attr("abs:action")); // Capturo
																		// el
																		// Link
					// System.out.println("IIIIIIIIIIIDDDDDDDDDDDDDDD    "+
					// link.id());
					String descripcion = new String(trim(link.text(), 35)); // Capturo
																			// la
																			// de
					// System.out.println(respuesta+"   ,   "+descripcion);
					break;
				}
			}

			// Peticion post a la primera pagina

			String peticion = respuesta.trim()
					+ "?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id1%3Aj_id2&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Abutton1=Buscar&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
			System.out.println("PETICION: " + peticion);
			post = peticion;
			// String
			// location1="https://contrataciondelestado.es/wps/portal/!ut/p/b1/jY_LDoIwEEW_hQ8wM7ZQYElKXwaVhIDQjWFhDIbHxvj9VhIXLqze3U3OyZ0BC92GkTRGFwot2Ll_DNf-PixzP766ZedQHDmXmmBS0RxJkdc1066qyAGdA_BLMvz0UewpEh2rRFCOWL79iPKw2TUlq4xCNFrmRb2NUBH2375n4Id_ArsivgtWwPeif4TAQS_TBSY7SpmaW5gFwRMr6o0s/dl4/d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_AVEQAI930OBRD02JPMTPG21004/act/id=0/p=javax.servlet.include.path_info=QCPjspQCPbusquedaQCPMainBusqueda.jsp/254706312144/-/?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id1%3Aj_id2&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Abutton1=Buscar&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
			// System.out.println(location1);

			// System.exit(0);

			// / Primer post a pagina 1

			response = Jsoup
					.connect(peticion)
					.ignoreContentType(true)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.timeout(12000).followRedirects(true)
					.cookie("JSESSIONID", id).method(Method.POST).execute();

			cookies = response.cookies(); // Capturo ID_SESSION
			doc = response.parse();
			// System.out.println(doc.html());

			l = idLicitacion(doc);
			// System.exit(0);
			buscarLicitaciones(l, id, estadisticas, bd);
			estadisticas.incrementarPaginasVisitadas();

			// mostrarEnlaces(doc);
			// System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			int ind = 0;
			while (!fin(doc, estadisticas)) {
				doc = iterarPaginas(doc, id, cookies, estadisticas, bd);
				// System.exit(0);
			}

			// /////////////////////////////////

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public boolean fin(Document doc, Estadisticas estadisticas) {
		boolean fin = false;
		Patrones p = new Patrones();
		String pActual = null, pFinal = null;
		String[] aux;
		/*
		 * aux=p.search(
		 * "span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoNumPagMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>"
		 * , doc.html()); for (int i=0;aux.length>i;i++){
		 * pActual=aux[i].replaceAll(
		 * "span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoNumPagMAQ\" class=\"outputText marginLeft0punto5\">"
		 * , ""); pActual=pActual.replaceAll("</span>", "");
		 * 
		 * }
		 * 
		 * aux=p.search(
		 * "span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>"
		 * , doc.html()); for (int i=0;aux.length>i;i++){
		 * pFinal=aux[i].replaceAll(
		 * "span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">"
		 * , ""); pFinal=pFinal.replaceAll("</span>", "");
		 * 
		 * }
		 */

		PaginaActual = estadisticas.getPaginasVisitadas();

		if (PaginaFinal == 0) {
			aux = p.search(
					"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">[\\d]*</span>",
					doc.html());
			for (int i = 0; aux.length > i; i++) {
				pFinal = aux[i]
						.replaceAll(
								"span id=\"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1:textfooterInfoTotalPaginaMAQ\" class=\"outputText marginLeft0punto5\">",
								"");
				pFinal = pFinal.replaceAll("</span>", "");

			}
			PaginaFinal = Integer.parseInt(pFinal);
		}

		// si llege al final
		if (PaginaFinal == PaginaActual) {
			fin = true;
			System.out
					.println("\n\n\n**********************************************************************************************************");
			System.out
					.println("**********************************************************************************************************");
			System.out
					.println("**********************************************************************************************************\n");
			System.out.println("Recorridas todas las paginas");
			double por = ((PaginaActual * (1.0)) / (PaginaFinal * (1.0))) * 100.0;
			System.out
					.printf("\nPagina actual  [%d],Paginas Totales  [%d],Porcentaje conpletado [%.4f%%]",
							Double.parseDouble(pActual),
							Double.parseDouble(pFinal), por);
			System.out
					.println("\n**********************************************************************************************************");
			System.out
					.println("**********************************************************************************************************");
			System.out
					.println("**********************************************************************************************************\n\n");
		} else {
			System.out
					.println("\n\n----------------------------------------------------------------------------------------------------------\n");
			double por = ((PaginaActual * (1.0)) / (PaginaFinal * (1.0))) * 100.0;
			System.out
					.printf("\nPagina actual  [%d],Paginas Totales  [%d],Porcentaje conpletado [%.4f%%],Enalces capturados [%d]",
							PaginaActual, PaginaFinal, por,
							estadisticas.getEnlacesCapturados());
			// System.out.println("Pagina actual: ["+pActual+"],		Pagina final "+pFinal+"]");
			System.out
					.println("\n----------------------------------------------------------------------------------------------------------\n");
		}

		return fin;
	}

	public Document iterarPaginas(Document doc, String id,
			Map<String, String> cookies, Estadisticas estadisticas, BaseDatos bd) {
		Elements links = doc.select("form[id]");
		Patrones p = new Patrones();
		String formulario = null;
		Document d = null;

		for (Element link : links) {
			if (link.attr("abs:id").contains(
					"viewns_Z7_AVEQAI930OBRD02JPMTPG21004_:form1")) {
				formulario = new String(link.attr("abs:action")); // Capturo el
																	// Link
				// System.out.println("IIIIIIIIIIIDDDDDDDDDDDDDDD    "+
				// link.id());
				// String descripcion=new String(trim(link.text(), 35)); //
				// Capturo la de
				System.out.println("formulario:      " + formulario);
				break;
			}
		}

		String[] pag = p.search("j_id[\\d]*", doc.html());

		for (int i = 0; pag.length > i; i++) {
			pag[i] = pag[i].replaceAll("j_id", "");
		}
		String peticion = formulario
				+ "?CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&javax.faces.ViewState=j_id"
				+ pag[0]
				+ "%3Aj_id"
				+ pag[1]
				+ "&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfooterSiguiente=Siguiente&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";

		try {
			System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxPeticion:   "
					+ peticion);
			Response response = Jsoup
					.connect(peticion)
					.ignoreContentType(true)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
					.timeout(12000).followRedirects(true)
					.cookie("JSESSIONID", id).method(Method.POST).execute();
			d = response.parse();
			estadisticas.incrementarPaginasVisitadas();
			cookies = response.cookies();
			String[] l = idLicitacion(d);
			buscarLicitaciones(l, id, estadisticas, bd);
			// System.out.println(d);
			// mostrarEnlaces(d);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return d;
	}

	public int buscarLicitaciones(String[] licitaciones, String id,
			Estadisticas estadisticas, BaseDatos bd) {
		int num;
		String url = "https://contrataciondelestado.es/wps/portal/!ut/p/b1/jY_LDoIwFES_xQ8wHfoSlqTQFoNKQkDpxrAwBsNjY_x-kcSFC6t3N8k5mbnEkWYdSREiADg5ETe2j-7a3rtpbPtXdvLM04NS2lKEJUtA86SqpJ2jETPQzAC-XIxPH-mOgdqNCVOmgOLtC6Z4va0LWWYGyKxO8ioQMFT-1-8p-OEfiVsQ34IF8L3oL6Fkb6fhQgbXax1lNx6vVk9ghp30/dl4/d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_AVEQAI930OBRD02JPMTPG21004/act/id=0/p=javax.servlet.include.path_info=QCPjspQCPbusquedaQCPMainBusqueda.jsp/254843306643/-/?";
		String parametros = null;
		for (int i = 0; licitaciones.length > i; i++) {

			parametros = "ACTION_NAME_PARAM=SourceAction&CpvorigenmultiplecpvMultiple=BusquedaVIS_UOE&TIPO_LICITACION=0&cpvPrincipalmultiplecpvMultiple=&cpvViewmultiplecpvMultiple=%23%7BbeanCpvPpt.cpv%7D&idLicitacion="
					+ licitaciones[i].trim()
					+ "&javax.faces.ViewState=j_id11%3Aj_id12&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcomboTipoAdminMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Acomboadmins=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AcpvMultiple%3AcodigoCpv=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0=viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AenlaceExpediente_0&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfAdjuOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfBOEPubMinMAQvis2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMaxMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfDOUEPubMinMAQvis=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos1MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AfPresOtrosDatos2MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AhiddenBusquedaOtrosDatos=true&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu111MAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Amenu1MAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuCompraPublicaInnovadoraMAQ1=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuSubtipoMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AmenuTipoContMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionAnulMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3As_detLicitacionMAQ=00&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtexoorganoMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atext71ExpMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado18MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextEstimado19MAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMaxFecAnuncioMAQ=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtextMinFecAnuncioMAQ2=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3Atextoministerio=&viewns_Z7_AVEQAI930OBRD02JPMTPG21004_%3Aform1%3AtipoSistemaContratacion=0";
			try {
				Response respuesta = Jsoup
						.connect(url + parametros)
						.ignoreContentType(true)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
						.timeout(12000).followRedirects(true)
						.cookie("JSESSIONID", id).method(Method.POST).execute();
				Document doc = respuesta.parse();
				// mostrarEnlaces(doc);

				ArrayList<String> coincidencias = buscarPatronHrefXml(doc,
						estadisticas, bd);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return 0;
	}

	public String[] idLicitacion(Document doc) {

		// System.out.println("\n\n======================================================================================");
		// System.out.println("===============================    =Toy aqui     ========================================");
		// System.out.println("======================================================================================\n\n");
		Patrones p = new Patrones();
		String[] coincidencias = p.search(
				"idLicitacion':'\\d\\d\\d\\d\\d\\d\\d\\d", doc.html());

		for (int i = 0; i < coincidencias.length; i++) {
			coincidencias[i] = coincidencias[i].replaceAll("idLicitacion':'",
					"");
			// System.out.println(coincidencias[i]);
		}

		return coincidencias;
	}

	public String buscarPatronHref(Document d, String p) {
		Elements links = d.select("a[href]");
		String referencia = null;
		for (Element link : links) {
			// Capturo el Link
			String enlace = new String(link.attr("abs:href"));
			// Capturo la de
			String descripcion = new String(trim(link.text(), 35));

			if (descripcion.contains("Búsqueda avanzada de licitaciones"))
				referencia = enlace;
		}
		return referencia;
	}

	public ArrayList<String> buscarPatronHrefXml(Document d,
			Estadisticas estadisticas, BaseDatos bd) {

		String[] expedientes = null;

		Patrones p = new Patrones();

		// //System.out.println(d);System.exit(0);

		expedientes = p
				.search(
				// "\\d\\d\\d\\d\\d\\d\\d\\d",
				"inputtext31\" value=\".*\" class=\"inputTextMedio\"", d.html());
		String exp = null;
		for (int i = 0; expedientes.length > i; i++) {
			exp = expedientes[i].replaceAll("inputtext31\" value=\"", "");
			exp = exp.replaceAll("\" class=\"inputTextMedio\"", "");
		}

		Elements links = d.select("a[href]");
		ArrayList<String> encontrados = new ArrayList<>();
		int ind = 0;
		for (Element link : links) {
			// Capturo el Link
			String enlace = new String(link.attr("abs:href"));
			// Capturo la de
			String descripcion = new String(trim(link.text(), 35));
			link.attr("");

			if (descripcion.trim().toLowerCase().contains("xml")) {
				encontrados.add(enlace);
				if (!estadisticas.getXml().containsKey(enlace)) {
					estadisticas.añadirEnlace(enlace, exp);
					estadisticas.incrementarEnlacesCapturados();
					// System.out.println("Encontrado XML en enlace:   "+enlace+" de expediente "+exp);
					// System.out.println(enlace);
					String xml = this.getXML(enlace);
					String xml2 = xml.replaceAll("'", "''");
					// System.out.println(xml);System.exit(0);
					bd.insertarLinkLicitacion(enlace, exp, xml2, post);
				} else {
					System.out
							.println("----------------------------->>>>>>>   Enlace XML duplicado:   "
									+ enlace);
				}
			}
		}
		return encontrados;
	}

	public static void mostrarEnlaces(Document d) {

		Elements links = d.select("a[href]");

		for (Element link : links) {
			// Capturo el Link
			String enlace = new String(link.attr("abs:href"));
			// Capturo la de
			String descripcion = new String(trim(link.text(), 35));
			System.out.println(enlace + "   ,   " + descripcion);
		}

		System.out
				.println("\n\n======================================================================================");
		System.out
				.println("======================================================================================");
		System.out
				.println("======================================================================================\n\n");

	}

	public String getXML(String urlToRead) {

		DefaultTrustManager dtm = new DefaultTrustManager();
		dtm.CrearConexionHTTPS();

		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "", result2 = "";
		try {
			Patrones p = new Patrones();
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			String[] url2 = p.search("url='.*'", result);
			String newURL = null;
			for (int i = 0; url2.length > i; i++) {
				newURL = url2[i].replaceAll("url='",
						"https://contrataciondelestado.es");
				break;
			}
			newURL = newURL.replaceAll("'", "");
			// System.out.println(newURL);
			url = new URL(newURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result2 += line;
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		result2.replaceAll("'", "''");
		return result2;
	}

	private static String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}
}
