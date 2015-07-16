package es.unizar.pproc.vocabulary;

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

	public static final Resource AwardCriteriaCombination = resource("AwardCriteriaCombination");
	public static final Resource AwardCriterion = resource("AwardCriterion");
	public static final Resource Tender = resource("Tender");

	public static final Property contractingAuthority = property("contractingAuthority");
	public static final Property awardCriteriaCombination = property("awardCriteriaCombination");
	public static final Property awardCriterion = property("awardCriterion");
	public static final Property criterionName = property("criterionName");
	public static final Property criterionWeight = property("criterionWeight");
	public static final Property tender = property("tender");
	public static final Property offeredPrice = property("offeredPrice");
	public static final Property supplier = property("supplier");

}
