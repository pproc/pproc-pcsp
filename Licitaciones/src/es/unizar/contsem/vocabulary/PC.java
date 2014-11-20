package es.unizar.contsem.vocabulary;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class PC {

	protected static final String uri = "http://purl.org/procurement/public-contracts#";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	protected static final Resource resource(String local) {
		return ResourceFactory.createResource(uri + local);
	}

	protected static final Property property(String local) {
		return ResourceFactory.createProperty(uri, local);
	}

	public static final Resource Alt = resource("Alt");

	public static final Property contractingAuthority = property("contractingAuthority");

	/**
	 * The same items of vocabulary, but at the Node level, parked inside a
	 * nested class so that there's a simple way to refer to them.
	 */
	@SuppressWarnings("hiding")
	public static final class Nodes {
		public static final Node Alt = PC.Alt.asNode();
		public static final Node contractingAuthority = PC.contractingAuthority.asNode();
	}

}
