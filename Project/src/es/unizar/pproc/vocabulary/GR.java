package es.unizar.pproc.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class GR {

	protected static final String uri = "http://purl.org/goodrelations/v1#";

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

	public static final Resource UnitPriceSpecification = resource("UnitPriceSpecification");
	public static final Resource Offering = resource("Offering");
	public static final Resource QuantitativeValue = resource("QuantitativeValue");

	public static final Property hasCurrencyValue = property("hasCurrencyValue");
	public static final Property valueAddedTaxIncluded = property("valueAddedTaxIncluded");
	public static final Property hasCurrency = property("hasCurrency");
	public static final Property hasEligibleQuantity = property("hasEligibleQuantity");
	public static final Property hasValue = property("hasValue");
	public static final Property hasUnitOfMeasurement = property("hasUnitOfMeasurement");
	public static final Property hasPriceSpecification = property("hasPriceSpecification");
	public static final Property hasMinValue = property("hasMinValue");
	public static final Property hasMaxValue = property("hasMaxValue");

}
