package es.unizar.pproc.vocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class CPV {

	protected static final String uri = "http://purl.org/cpv/2008/";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	public static final Resource code(String code) {
		return ResourceFactory.createResource(uri + "code-" + code);
	}

}
