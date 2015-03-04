package es.unizar.contsem.parser;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.unizar.contsem.vocabulary.CPV;
import es.unizar.contsem.vocabulary.GR;
import es.unizar.contsem.vocabulary.ORG;
import es.unizar.contsem.vocabulary.PC;
import es.unizar.contsem.vocabulary.PPROC;
import es.unizar.contsem.vocabulary.S;

/**
 * Transforms an XML/CODICE document into RDF following PPROC ontology.
 * 
 * @author gesteban
 */
public class Codice2Pproc {

    static {
        org.apache.log4j.BasicConfigurator.configure();
    }

    private static String BASE_URI_CONTRACT = "http://pproc.unizar.es/recurso/sector-publico/contrato/";
    private static String BASE_URI_ORGANIZATION = "http://pproc.unizar.es/recurso/sector-publico/organization/";

    public static void parseCodiceXML(Model model, Document document) throws Exception {

        String altString, altString2, organizationResourceURI;

        Element root = document.getRootElement();
        Element altElement, altElement2;

        Resource contractResource;

        // UUID
        // pproc:Contract rdf:type
        contractResource = model.createResource(BASE_URI_CONTRACT + root.elementText("UUID"));
        contractResource.addProperty(RDF.type, PPROC.Contract);

        // ContractFolderID
        // pproc:Contract dcterms:identifier
        if ((altString = root.elementText("ContractFolderID")) != null)
            contractResource.addProperty(DCTerms.identifier, altString, XSDDatatype.XSDstring);

        // TODO IssueDate -> en anuncios (fecha de envio)
        // TODO IssueTime -> en anuncios (fecha de envio)
        // TODO Note -> en anuncios (descripción textual)

        // ProcurementProject/
        if ((altElement = root.element("ProcurementProject")) != null) {

            // ProcurementProject/Name
            // pproc:Contract dcterms:title rdfs:label
            if ((altString = altElement.elementText("Name")) != null) {
                contractResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
                contractResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
            }

            // ProcurementProject/Description
            // pproc:Contract dcterms:description rdfs:comment
            if ((altString = altElement.elementText("Description")) != null) {
                contractResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                contractResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
            }

            // ProcurementProject/TypeCode
            // pproc:Contract rdf:type
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = altElement.elementText("TypeCode")) != null) {
                switch (altString) {
                case "1":
                    contractResource.addProperty(RDF.type, PPROC.SuppliesContract);
                    // ProcurementProject/SubTypeCode
                    // rdf:type
                    // TODO parseo independiente de versión (parseo del .gc)
                    if ((altString = altElement.elementText("SubTypeCode")) != null)
                        switch (altString) {
                        case "1":
                            contractResource.addProperty(RDF.type, PPROC.RentContract);
                            break;
                        case "2":
                            contractResource.addProperty(RDF.type, PPROC.BuyContract);
                        }

                    break;
                case "2":
                    contractResource.addProperty(RDF.type, PPROC.ServicesContract);
                    break;
                case "3":
                    contractResource.addProperty(RDF.type, PPROC.WorksContract);
                    break;
                case "21":
                    contractResource.addProperty(RDF.type, PPROC.PublicServicesManagementContract);
                    break;
                case "31":
                    contractResource.addProperty(RDF.type, PPROC.PublicWorksConcessionContract);
                    break;
                case "40":
                    contractResource.addProperty(RDF.type, PPROC.PublicPrivatePartnershipContract);
                    break;
                case "7":
                    contractResource.addProperty(RDF.type, PPROC.SpecialAdministrativeContract);
                    break;
                case "8":
                    contractResource.addProperty(RDF.type, PPROC.PrivateContract);
                    break;
                case "50":
                    // Contrato patrimonial, no hay equivalencia en PPROC
                }
            } // ProcurementProject/TypeCode

            // ProcurementProject/PlannedPeriod
            // pproc:ContractObject pproc:contractTemporalConditions
            if (altElement.element("PlannedPeriod") != null) {
                Resource objectResource = model.createResource(contractResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                contractResource.addProperty(PPROC.contractObject, objectResource);
                Resource ctcResource = model.createResource(contractResource + "/ContractTemporalConditions");
                ctcResource.addProperty(RDF.type, PPROC.ContractTemporalConditions);
                objectResource.addProperty(PPROC.contractTemporalConditions, ctcResource);

                // ProcurementProject/PlannedPeriod/Description
                // pproc:ContractTemporalConditions dcterms:description rdfs:comment
                if ((altString = altElement.element("PlannedPeriod").elementText("Note")) != null) {
                    ctcResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                    ctcResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/PlannedPeriod/DurationMeasure
                // pproc:ContractTemporalConditions pproc:estimatedDuration
                if ((altString = altElement.element("PlannedPeriod").elementText("DurationMeasure")) != null
                        && (altString2 = altElement.element("PlannedPeriod").element("DurationMeasure")
                                .attributeValue("unitCode")) != null)
                    switch (altString2) {
                    case "DAY":
                        ctcResource
                                .addProperty(PPROC.estimatedDuration, "P" + altString + "D", XSDDatatype.XSDduration);
                        break;
                    case "MON":
                        ctcResource
                                .addProperty(PPROC.estimatedDuration, "P" + altString + "M", XSDDatatype.XSDduration);
                        break;
                    case "ANN":
                        ctcResource
                                .addProperty(PPROC.estimatedDuration, "P" + altString + "Y", XSDDatatype.XSDduration);
                        break;
                    }

                // ProcurementProject/PlannedPeriod/EndDate
                // pproc:ContractTemporalConditions pproc:estimatedEndDate
                if ((altString = altElement.element("PlannedPeriod").elementText("EndDate")) != null)
                    ctcResource.addProperty(PPROC.estimatedEndDate, altString, XSDDatatype.XSDdate);
            } // ProcurementProject/PlannedPeriod

            // ProcurementProject/BudgetAmount
            // pproc:Contract pproc:contractEconomicConditions
            if (altElement.element("BudgetAmount") != null) {
                Resource objectResource = model.createResource(contractResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                contractResource.addProperty(PPROC.contractObject, objectResource);
                Resource cecResource = model.createResource(contractResource + "/ContractEconomicConditions");
                cecResource.addProperty(RDF.type, PPROC.ContractEconomicConditions);
                objectResource.addProperty(PPROC.contractEconomicConditions, cecResource);

                // ProcurementProject/BudgetAmount/EstimatedOverallContractAmount
                // pproc:ContractEconomicConditions pproc:estimatedValue
                if ((altString = altElement.element("BudgetAmount").elementText("EstimatedOverallContractAmount")) != null) {
                    Resource priceResource = model.createResource(cecResource + "/EstimatedValue");
                    cecResource.addProperty(PPROC.estimatedValue, priceResource);
                    priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                    priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                    priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                    if ((altString = altElement.element("BudgetAmount").element("EstimatedOverallContractAmount")
                            .attributeValue("currencyID")) != null)
                        priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/BudgetAmount/TotalAmount
                // pproc:ContractEconomicConditions pproc:budgetPrice
                if ((altString = altElement.element("BudgetAmount").elementText("TotalAmount")) != null) {
                    Resource priceResource = model.createResource(cecResource + "/TotalAmount");
                    cecResource.addProperty(PPROC.budgetPrice, priceResource);
                    priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                    priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                    priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                    if ((altString = altElement.element("BudgetAmount").element("TotalAmount")
                            .attributeValue("currencyID")) != null)
                        priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
                    cecResource.addProperty(PPROC.budgetPrice, priceResource);
                }

                // ProcurementProject/BudgetAmount/TaxExclusiveAmount
                // pproc:ContractEconomicConditions pproc:budgetPrice
                if ((altString = altElement.element("BudgetAmount").elementText("TaxExclusiveAmount")) != null) {
                    Resource priceResource = model.createResource(cecResource + "/TaxExclusiveAmount");
                    cecResource.addProperty(PPROC.budgetPrice, priceResource);
                    priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                    priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                    priceResource.addProperty(GR.valueAddedTaxIncluded, "false", XSDDatatype.XSDboolean);
                    if ((altString = altElement.element("BudgetAmount").element("TaxExclusiveAmount")
                            .attributeValue("currencyID")) != null)
                        priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
                }
            } // ProcurementProject/BudgetAmount

            // ProcurementProject/RequiredFeeAmount
            // pproc:ContractEconomicConditions pproc:feePrice
            if ((altString = altElement.elementText("RequiredFeeAmount")) != null) {
                Resource objectResource = model.createResource(contractResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                contractResource.addProperty(PPROC.contractObject, objectResource);
                Resource cecResource = model.createResource(contractResource + "/ContractEconomicConditions");
                cecResource.addProperty(RDF.type, PPROC.ContractEconomicConditions);
                objectResource.addProperty(PPROC.contractEconomicConditions, cecResource);
                Resource priceResource = model.createResource(cecResource + "/FeePrice");
                cecResource.addProperty(PPROC.feePrice, priceResource);
                priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                if ((altString = altElement.element("RequiredFeeAmount").attributeValue("currencyID")) != null)
                    priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
            }

            // ProcurementProject/RequiredCommodityClassification
            // pproc:ContractObject pc:mainObject
            if (altElement.element("RequiredCommodityClassification") != null
                    && (altString = altElement.element("RequiredCommodityClassification").elementText(
                            "ItemClassificationCode")) != null) {
                Resource objectResource = model.createResource(contractResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                contractResource.addProperty(PPROC.contractObject, objectResource);
                objectResource.addProperty(PPROC.mainObject, CPV.code(altString));
            }

            // ProcurementProject/RequestForTenderLine
            // pproc:ContractObject pproc:provision
            if (altElement.element("RequestForTenderLine") != null) {
                for (Iterator<?> iter = altElement.elementIterator("RequestForTenderLine"); iter.hasNext();) {
                    altElement2 = (Element) iter.next();
                    Resource objectResource = model.createResource(contractResource + "/ContractObject");
                    objectResource.addProperty(RDF.type, PPROC.ContractObject);
                    contractResource.addProperty(PPROC.contractObject, objectResource);
                    Resource offeringResource = model.createResource(objectResource + "Offering_"
                            + altElement2.elementText("ID"));
                    offeringResource.addProperty(RDF.type, GR.Offering);
                    objectResource.addProperty(PPROC.provision, offeringResource);

                    // ProcurementProject/RequestForTenderLine/Item/Name
                    // gr:Offering dcterms:title rdfs:label
                    if ((altString = altElement2.element("Item").elementText("Name")) != null) {
                        offeringResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
                        offeringResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/Item/Description
                    // gr:Offering dcterms:description rdfs:comment
                    if ((altString = altElement2.element("Item").elementText("Description")) != null) {
                        offeringResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                        offeringResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/Item/AdditionalInformation
                    // gr:Offering dcterms:description rdfs:comment
                    if ((altString = altElement2.element("Item").elementText("AdditionalInformation")) != null) {
                        offeringResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                        offeringResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/Quantity
                    // gr:Offering gr:hasEligibleQuantity
                    if ((altString = altElement2.elementText("Quantity")) != null) {
                        Resource quantityResource = model.createResource(offeringResource + "/QuantitativeValue");
                        quantityResource.addProperty(RDF.type, GR.QuantitativeValue);
                        offeringResource.addProperty(GR.hasEligibleQuantity, quantityResource);
                        quantityResource.addProperty(GR.hasValue, altString);
                        if ((altString = altElement2.element("Quantity").attributeValue("unitCode")) != null)
                            quantityResource.addProperty(GR.hasUnitOfMeasurement, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/MaximumTaxExclusiveAmount
                    // gr:Offering gr:hasPriceSpecification
                    if ((altString = altElement2.elementText("MaximumTaxExclusiveAmount")) != null) {
                        Resource priceResource = model.createResource(offeringResource + "/MaximumTaxExclusiveAmount");
                        offeringResource.addProperty(GR.hasPriceSpecification, priceResource);
                        priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                        priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                        priceResource.addProperty(GR.valueAddedTaxIncluded, "false", XSDDatatype.XSDboolean);
                        if ((altString = altElement2.element("MaximumTaxExclusiveAmount").attributeValue("currencyID")) != null)
                            priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/MaximumTaxInclusiveAmount
                    // gr:Offering gr:hasPriceSpecification
                    if ((altString = altElement2.elementText("MaximumTaxInclusiveAmount")) != null) {
                        Resource priceResource = model.createResource(offeringResource + "/MaximumTaxInclusiveAmount");
                        offeringResource.addProperty(GR.hasPriceSpecification, priceResource);
                        priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                        priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                        priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                        if ((altString = altElement2.element("MaximumTaxInclusiveAmount").attributeValue("currencyID")) != null)
                            priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/RequiredItemLocationQuantity
                    // gr:Offering gr:hasPriceSpecification
                    if (altElement2.element("RequiredItemLocationQuantity") != null
                            && (altString = altElement2.element("RequiredItemLocationQuantity").element("Price")
                                    .elementText("PriceAmount")) != null) {
                        Resource priceResource = model.createResource(offeringResource + "/UnitPrice");
                        offeringResource.addProperty(GR.hasPriceSpecification, priceResource);
                        priceResource.addProperty(RDF.type, GR.UnitPriceSpecification);
                        priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                        priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                        if ((altString = altElement2.element("RequiredItemLocationQuantity").element("Price")
                                .element("PriceAmount").attributeValue("currencyID")) != null)
                            priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);

                        // ProcurementProject/RequestForTenderLine/RequiredItemLocationQuantity/MinimumQuantity
                        // gr:UnitPriceSpecification gr:hasEligibleQuantity
                        if ((altString = altElement2.element("RequiredItemLocationQuantity").elementText(
                                "MinimumQuantity")) != null) {
                            Resource quantityResource = model.createResource(priceResource + "/QuantitativeValue");
                            quantityResource.addProperty(RDF.type, GR.QuantitativeValue);
                            priceResource.addProperty(GR.hasEligibleQuantity, quantityResource);
                            quantityResource.addProperty(GR.hasMinValue, altString);
                            if ((altString = altElement2.element("RequiredItemLocationQuantity")
                                    .element("MinimumQuantity").attributeValue("unitCode")) != null)
                                quantityResource.addProperty(GR.hasUnitOfMeasurement, altString, XSDDatatype.XSDstring);
                        }

                        // ProcurementProject/RequestForTenderLine/RequiredItemLocationQuantity/MaximumQuantity
                        // gr:UnitPriceSpecification gr:hasEligibleQuantity
                        if ((altString = altElement2.element("RequiredItemLocationQuantity").elementText(
                                "MaximumQuantity")) != null) {
                            Resource quantityResource = model.createResource(priceResource + "/QuantitativeValue");
                            quantityResource.addProperty(RDF.type, GR.QuantitativeValue);
                            priceResource.addProperty(GR.hasEligibleQuantity, quantityResource);
                            quantityResource.addProperty(GR.hasMaxValue, altString);
                            if ((altString = altElement2.element("RequiredItemLocationQuantity")
                                    .element("MaximumQuantity").attributeValue("unitCode")) != null)
                                quantityResource.addProperty(GR.hasUnitOfMeasurement, altString, XSDDatatype.XSDstring);
                        } // ProcurementProject/RequestForTenderLine/RequiredItemLocationQuantity/MaximumQuantity
                    } // ProcurementProject/RequestForTenderLine/RequiredItemLocationQuantity
                }
            } // ProcurementProject/RequestForTenderLine

            // ProcurementProject/RealizedLocation
            // pproc:ContractObject pproc:location
            if (altElement.element("RealizedLocation") != null) {
                Resource objectResource = model.createResource(contractResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                contractResource.addProperty(PPROC.contractObject, objectResource);
                Resource placeResource = model.createResource(contractResource + "/Place");
                placeResource.addProperty(RDF.type, S.Place);
                objectResource.addProperty(ORG.hasSite, placeResource);

                // ProcurementProject/RealizedLocation/Description
                // s:Place s:name dcterms:title rdfs:label
                if ((altString = altElement.element("RealizedLocation").elementText("Description")) != null) {
                    placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);
                    placeResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
                    placeResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/RealizedLocation/Address
                // s:Place s:address
                if ((altElement2 = altElement.element("RealizedLocation").element("Address")) != null) {
                    Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                    postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                    placeResource.addProperty(S.address, postalAddressResource);
                    if ((altString = altElement2.elementText("CityName")) != null)
                        postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                    if ((altString = altElement2.elementText("PostalZone")) != null)
                        postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                    if (altElement2.element("AddressLine") != null
                            && (altString = altElement2.element("AddressLine").elementText("Line")) != null)
                        postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                    if ((altString = altElement2.element("Country").elementText("IdentificationCode")) != null)
                        postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/RealizedLocation/CountrySubentityCode
                // s:Place s:address
                if ((altString = altElement.element("RealizedLocation").elementText("CountrySubentityCode")) != null) {
                    Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                    postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                    placeResource.addProperty(S.address, postalAddressResource);
                    postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
                }
            } // ProcurementProject/RealizedLocation

            // ProcurementProject/ContractExtension
            // pproc:ContractObject pproc:contractTemporalConditions
            if (altElement.element("ContractExtension") != null) {
                Resource objectResource = model.createResource(contractResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                contractResource.addProperty(PPROC.contractObject, objectResource);
                Resource ctcResource = model.createResource(contractResource + "/ContractTemporalConditions");
                ctcResource.addProperty(RDF.type, PPROC.ContractTemporalConditions);
                objectResource.addProperty(PPROC.contractTemporalConditions, ctcResource);

                // ProcurementProject/ContractExtension/OptionValidityPeriod/Description
                // pproc:ContractTemporalConditions dcterms:description rdfs:comment
                if ((altString = altElement.element("ContractExtension").element("OptionValidityPeriod")
                        .elementText("Description")) != null) {
                    ctcResource.addProperty(DCTerms.description, "Período de validez de opciones de extensión: "
                            + altString, XSDDatatype.XSDstring);
                    ctcResource.addProperty(RDFS.comment, "Período de validez de opciones de extensión: " + altString,
                            XSDDatatype.XSDstring);
                }

                // ProcurementProject/ContractExtension/OptionsDescription
                // pproc:ContractTemporalConditions dcterms:description rdfs:comment
                if ((altString = altElement.element("ContractExtension").elementText("OptionsDescription")) != null) {
                    ctcResource.addProperty(DCTerms.description, "Opciones de extensión: " + altString,
                            XSDDatatype.XSDstring);
                    ctcResource.addProperty(RDFS.comment, "Opciones de extensión: " + altString, XSDDatatype.XSDstring);
                }
            } // ProcurementProject/ContractExtension

        } // ProcurementProject/

        // ////////////////
        // ////////////////
        // ////////////////
        // ////////////////
        // ////////////////

        altString2 = null;
        // pproc:Contract pc:contractingAuthority
        if (document.getRootElement().element("ContractingParty") != null) {
            for (Iterator iter = document.getRootElement().element("ContractingParty").element("Party")
                    .elementIterator("PartyIdentification"); iter.hasNext();) {
                altElement = (Element) iter.next();
                if (altElement.element("ID").attributeValue("schemeName").equals("ID_PLATAFORMA"))
                    altString = altElement.elementText("ID");
                else if (altElement.element("ID").attributeValue("schemeName").equals("NIF"))
                    altString2 = altElement.elementText("ID");
            }
            organizationResourceURI = BASE_URI_ORGANIZATION
                    + altString.replace(" ", "").replace("\t", "").replace("	", "");
            Resource organizationResource = model.createResource(organizationResourceURI);
            if (!model.containsResource(ResourceFactory.createResource(organizationResourceURI))) {

                // org:Organization rdf:type
                organizationResource.addProperty(RDF.type, ORG.Organization);

                // org:Organization dcterms:title
                if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                        .element("PartyName").elementText("Name")) != null)
                    organizationResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);

                // org:Organization dcterms:identifier
                if (altString2 != null)
                    organizationResource.addProperty(DCTerms.identifier, altString2, XSDDatatype.XSDstring);

                // org:Organization org:hasSite
                if (document.getRootElement().element("ContractingParty").element("Party").element("PostalAddress") != null) {
                    Resource placeResource = model.createResource(organizationResource + "/Place");
                    placeResource.addProperty(RDF.type, S.Place);
                    organizationResource.addProperty(ORG.hasSite, placeResource);

                    // s:Place s:address
                    Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                    postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                    placeResource.addProperty(S.address, postalAddressResource);
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("PostalAddress").elementText("CityName")) != null)
                        postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("PostalAddress").elementText("PostalZone")) != null)
                        postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("PostalAddress").element("AddressLine").elementText("Line")) != null)
                        postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("PostalAddress").element("Country").elementText("IdentificationCode")) != null)
                        postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
                }

                if (document.getRootElement().element("ContractingParty").element("Party").element("Contact") != null) {
                    // org:Organization s:telephone
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("Contact").elementText("Telephone")) != null)
                        organizationResource.addProperty(S.telephone, altString, XSDDatatype.XSDstring);

                    // org:Organization s:faxNumber
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("Contact").elementText("Telefax")) != null)
                        organizationResource.addProperty(S.faxNumber, altString, XSDDatatype.XSDstring);

                    // org:Organization s:email
                    if ((altString = document.getRootElement().element("ContractingParty").element("Party")
                            .element("Contact").elementText("ElectronicMail")) != null)
                        organizationResource.addProperty(S.email, altString, XSDDatatype.XSDstring);
                }

            }
            contractResource.addProperty(PC.contractingAuthority, organizationResource);
        }

        // pproc:Contract pproc:delegatingAuthority
        altString2 = null;
        if (document.getRootElement().element("OriginatorCustomerParty") != null) {
            for (Iterator iter = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                    .elementIterator("PartyIdentification"); iter.hasNext();) {
                altElement = (Element) iter.next();
                if (altElement.element("ID").attributeValue("schemeName").equals("ID_PLATAFORMA"))
                    altString = altElement.elementText("ID");
                else if (altElement.element("ID").attributeValue("schemeName").equals("NIF"))
                    altString2 = altElement.elementText("ID");

            }
            organizationResourceURI = BASE_URI_ORGANIZATION + altString.replace(" ", "");
            Resource organizationResource = model.createResource(organizationResourceURI);
            if (!model.containsResource(ResourceFactory.createResource(organizationResourceURI))) {

                // org:Organization rdf:type
                organizationResource.addProperty(RDF.type, ORG.Organization);

                // org:Organization dcterms:title
                if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                        .element("PartyName").elementText("Name")) != null)
                    organizationResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);

                // org:Organization dcterms:identifier
                if (altString2 != null)
                    organizationResource.addProperty(DCTerms.identifier, altString2, XSDDatatype.XSDstring);

                // org:Organization org:hasSite
                if (document.getRootElement().element("OriginatorCustomerParty").element("Party")
                        .element("PostalAddress") != null) {
                    Resource placeResource = model.createResource(organizationResourceURI + "/Place");
                    placeResource.addProperty(RDF.type, S.Place);
                    organizationResource.addProperty(ORG.hasSite, placeResource);

                    // s:address s:PostalAddress
                    Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                    postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                    placeResource.addProperty(S.address, postalAddressResource);
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("PostalAddress").elementText("CityName")) != null)
                        postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("PostalAddress").elementText("PostalZone")) != null)
                        postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("PostalAddress").element("AddressLine").elementText("Line")) != null)
                        postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("PostalAddress").element("Country").elementText("IdentificationCode")) != null)
                        postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
                }

                if (document.getRootElement().element("OriginatorCustomerParty").element("Party").element("Contact") != null) {
                    // org:Organization s:telephone
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("Contact").elementText("Telephone")) != null)
                        organizationResource.addProperty(S.telephone, altString, XSDDatatype.XSDstring);

                    // org:Organization s:faxNumber
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("Contact").elementText("Telefax")) != null)
                        organizationResource.addProperty(S.faxNumber, altString, XSDDatatype.XSDstring);

                    // org:Organization s:email
                    if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
                            .element("Contact").elementText("ElectronicMail")) != null)
                        organizationResource.addProperty(S.email, altString, XSDDatatype.XSDstring);
                }
            }
            contractResource.addProperty(PC.contractingAuthority, organizationResource);
        }

        // pproc:Contract pproc:contractProcedureSpecifications
        if (document.getRootElement().element("TenderingProcess") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);

            // pproc:ContractProcedureSpecifications pproc:urgencyType
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = document.getRootElement().element("TenderingProcess").elementText("UrgencyCode")) != null)
                switch (altString) {
                case "1":
                    cpeResource.addProperty(PPROC.urgencyType, PPROC.Regular);
                    break;
                case "2":
                    cpeResource.addProperty(PPROC.urgencyType, PPROC.Express);
                    break;
                case "3":
                    cpeResource.addProperty(PPROC.urgencyType, PPROC.Emergency);
                }

            // pproc:ContractProcedureSpecifications pproc:procedureType
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = document.getRootElement().element("TenderingProcess").elementText("ProcedureCode")) != null)
                switch (altString) {
                case "1":
                    cpeResource.addProperty(PPROC.procedureType, PPROC.RegularOpen);
                    break;
                case "2":
                    cpeResource.addProperty(PPROC.procedureType, PPROC.Restricted);
                    break;
                case "3":
                    cpeResource.addProperty(PPROC.procedureType, PPROC.NegotiatedWithoutPublicity);
                    cpeResource.addProperty(PPROC.procedureType, PPROC.Negotiated);
                    break;
                case "4":
                    cpeResource.addProperty(PPROC.procedureType, PPROC.NegotiatedWithPublicity);
                    cpeResource.addProperty(PPROC.procedureType, PPROC.Negotiated);
                    break;
                case "5":
                    cpeResource.addProperty(PPROC.procedureType, PPROC.CompetitiveDialogue);
                    break;
                case "100":
                    // Normas internas, no definido en PPROC
                }

            // pproc:Contract rdf:type (4)
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = document.getRootElement().element("TenderingProcess").elementText("ContractingSystemCode")) != null)
                switch (altString) {
                case "1":
                    contractResource.addProperty(RDF.type, PPROC.FrameworkConclusionContract);
                    break;
                case "2":
                    contractResource.addProperty(RDF.type, PPROC.DynamicPurchasingSystemConclusionContract);
                    break;
                case "3":
                    contractResource.addProperty(RDF.type, PPROC.FrameworkDerivativeContract);
                    break;
                case "4":
                    contractResource.addProperty(RDF.type, PPROC.DynamicPurchasingSystemDerivativeContract);
                }

            // pproc:Contract pproc:frameworAgreement
            if (document.getRootElement().element("TenderingProcess").element("FrameworAgreement") != null
                    && (model.containsResource(PPROC.FrameworkConclusionContract) || model
                            .containsResource(PPROC.DynamicPurchasingSystemConclusionContract))) {
                Resource frameworkResource = model.createResource(contractResource + "/FrameworkAgreement");
                frameworkResource.addProperty(RDF.type, PPROC.FrameworkAgreement);

                // pproc:FrameworkResource pproc:maxNumberOfOperators
                if ((altString = document.getRootElement().element("TenderingProcess").element("FrameworAgreement")
                        .elementText("MaximumOperatorsQuantity")) != null)
                    frameworkResource.addProperty(PPROC.maxNumberOfOperators, altString, XSDDatatype.XSDstring);

                // pproc:FrameworkResource pproc:estimatedDuration
                if ((altString = document.getRootElement().element("TenderingProcess").element("FrameworAgreement")
                        .elementText("DurationMeasure")) != null
                        && (altString2 = document.getRootElement().element("TenderingProcess")
                                .element("FrameworAgreement").element("DurationMeasure").attributeValue("unitCode")) != null)
                    switch (altString2) {
                    case "DAY":
                        frameworkResource.addProperty(PPROC.estimatedDuration, "P" + altString + "D",
                                XSDDatatype.XSDduration);
                        break;
                    case "MON":
                        frameworkResource.addProperty(PPROC.estimatedDuration, "P" + altString + "M",
                                XSDDatatype.XSDduration);
                        break;
                    case "ANN":
                        frameworkResource.addProperty(PPROC.estimatedDuration, "P" + altString + "Y",
                                XSDDatatype.XSDduration);
                        break;
                    }

                // TODO pproc:FrameworkResource pproc:estimatedEndDate
                // (falta mirar como es udt:DateType)

                contractResource.addProperty(PPROC.frameworkAgreement, frameworkResource);
            }

            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
        }

        // pproc:Contract pproc:tenderersRequirements
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("TendererQualificationRequest") != null) {
            Resource tenderersRequirementsResource = model.createResource(contractResource + "/TenderersRequirements");
            tenderersRequirementsResource.addProperty(RDF.type, PPROC.TenderersRequirements);

            // pproc:TenderersRequirements pproc:requiredClassification
            if (document.getRootElement().element("TenderingTerms").element("TendererQualificationRequest")
                    .element("RequiredBusinessClassificationScheme") != null
                    && (altString = document.getRootElement().element("TenderingTerms")
                            .element("TendererQualificationRequest").element("RequiredBusinessClassificationScheme")
                            .elementText("CodeValue")) != null) {
                tenderersRequirementsResource.addProperty(PPROC.requiredClassification, altString,
                        XSDDatatype.XSDstring);
            }

            // pproc:TenderersRequirements proc:requiredEconomicAndFinancialStanding
            if (document.getRootElement().element("TenderingTerms").element("TendererQualificationRequest")
                    .element("FinancialEvaluationCriteria") != null
                    && (altString = document.getRootElement().element("TenderingTerms")
                            .element("TendererQualificationRequest").element("FinancialEvaluationCriteria")
                            .elementText("Description")) != null)
                tenderersRequirementsResource.addProperty(PPROC.requiredEconomicAndFinancialStanding, altString,
                        XSDDatatype.XSDstring);

            // pproc:TenderersRequirements pproc:requiredTechnicalAndProfessionalAbility
            if (document.getRootElement().element("TenderingTerms").element("TendererQualificationRequest")
                    .element("TechnicalEvaluationCriteria") != null
                    && (altString = document.getRootElement().element("TenderingTerms")
                            .element("TendererQualificationRequest").element("TechnicalEvaluationCriteria")
                            .elementText("Description")) != null)
                tenderersRequirementsResource.addProperty(PPROC.requiredTechnicalAndProfessionalAbility, altString,
                        XSDDatatype.XSDstring);

            if (document.getRootElement().element("TenderingTerms").element("TendererQualificationRequest")
                    .element("SpecificTendererRequirement") != null)
                for (Iterator iter = document.getRootElement().element("TenderingTerms")
                        .element("TendererQualificationRequest").elementIterator("SpecificTendererRequirement"); iter
                        .hasNext();) {
                    altElement = (Element) iter.next();
                    if (altElement.element("RequirementTypeCode") != null)
                        tenderersRequirementsResource.addProperty(PPROC.otherAbilityRequisites,
                                altElement.element("RequirementTypeCode").attributeValue("name"));
                    if ((altString = altElement.elementText("Description")) != null)
                        tenderersRequirementsResource.addProperty(PPROC.otherAbilityRequisites, altString,
                                XSDDatatype.XSDstring);
                }
            contractResource.addProperty(PPROC.tenderersRequirements, tenderersRequirementsResource);
        }

        // pproc:Contract pc:awardCriteriaCombination
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("AwardingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("AwardingTerms")
                        .element("AwardingCriteria") != null) {
            Resource criteriaCombinationResource = model.createResource(contractResource + "/AwardCriteriaCombination");
            criteriaCombinationResource.addProperty(RDF.type, PC.AwardCriteriaCombination);

            // pc:AwardCriteriaCombination dcterms:description
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = document.getRootElement().element("TenderingTerms").element("AwardingTerms")
                    .elementText("WeightingAlgorithmCode")) != null)
                switch (altString) {
                case "1":
                    criteriaCombinationResource.addProperty(DCTerms.description,
                            "Algoritmo de ponderación: Ponderación lineal", XSDDatatype.XSDstring);
                    break;
                case "2":
                    criteriaCombinationResource.addProperty(DCTerms.description, "Algoritmo de ponderación: Promedio",
                            XSDDatatype.XSDstring);
                    break;
                case "3":
                    criteriaCombinationResource.addProperty(DCTerms.description, "Algoritmo de ponderación: Topsis",
                            XSDDatatype.XSDstring);
                    break;
                case "4":
                    criteriaCombinationResource.addProperty(DCTerms.description,
                            "Algoritmo de ponderación: Lexicográfico", XSDDatatype.XSDstring);
                }

            // pc:AwardCriteriaCombination pc:awardCriterion
            for (Iterator iter = document.getRootElement().element("TenderingTerms").element("AwardingTerms")
                    .elementIterator("AwardingCriteria"); iter.hasNext();) {
                altElement = (Element) iter.next();
                Resource criterionResource = model.createResource(criteriaCombinationResource + "/Criterion"
                        + altElement.elementText("ID"));
                criterionResource.addProperty(RDF.type, PC.AwardCriterion);

                // pc:AwardCriterion rdf:type
                // TODO parseo independiente de versión (parseo del .gc)
                if ((altString = altElement.elementText("AwardingCriteriaTypeCode")) != null)
                    switch (altString) {
                    case "SUBJ":
                        criterionResource.addProperty(RDF.type, PPROC.SubjectiveAwardCriterion);
                        break;
                    case "OBJ":
                        criterionResource.addProperty(RDF.type, PPROC.ObjectiveAwardCriterion);
                    }

                // pc:AwardCriterion pc:criterionName
                if ((altString = altElement.elementText("Description")) != null)
                    criterionResource.addProperty(PC.criterionName, altString, XSDDatatype.XSDstring);

                // pc:AwardCriterion pc:criterionWeight
                if ((altString = altElement.elementText("WeightNumeric")) != null)
                    criterionResource.addProperty(PC.criterionWeight, altString, XSDDatatype.XSDfloat);

                // pc:AwardCriterion pproc:criterionEvaluationMode
                if ((altString = altElement.elementText("CalculationExpression")) != null)
                    criterionResource.addProperty(PPROC.criterionEvaluationMode, altString, XSDDatatype.XSDstring);

                // pc:AwardCriterion pproc:criterionMaxAndMinScores
                altString2 = "";
                if ((altString = altElement.elementText("MinimumQuantity")) != null)
                    altString2 += "Cantidad mínima: " + altString + ". ";
                if ((altString = altElement.elementText("MaximumQuantity")) != null)
                    altString2 += "Cantidad máxima: " + altString + ". ";
                if ((altString = altElement.elementText("MinimumAmount")) != null)
                    altString2 += "Importe mínimo: " + altString + ". ";
                if ((altString = altElement.elementText("MaximumAmount")) != null)
                    altString2 += "Importe máximo: " + altString + ". ";
                if (altString2 != "")
                    criterionResource.addProperty(PPROC.criterionMaxAndMinScores, altString2, XSDDatatype.XSDstring);

                criteriaCombinationResource.addProperty(PC.awardCriterion, criterionResource);
            }

            contractResource.addProperty(PC.awardCriteriaCombination, criteriaCombinationResource);
        }

        // pproc:ContractProcedureSpecifications pproc:contractAddionalObligations
        // pproc:ContractAdditionalObligations pproc:finalFinancialGuarantee
        // pproc:ContractAdditionalObligations pproc:finalFinancialGuaranteeDuration
        // pproc:ContractAdditionalObligations pproc:provisionalFinancialGuarantee
        // pproc:ContractAdditionalObligations pproc:otherGuarantee
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("RequiredFinancialGuarantee") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            Resource addionalObligationsResource = model.createResource(cpeResource + "/ContractAdditionalObligations");
            addionalObligationsResource.addProperty(RDF.type, PPROC.ContractAdditionalObligations);
            for (Iterator iter = document.getRootElement().element("TenderingTerms")
                    .elementIterator("RequiredFinancialGuarantee"); iter.hasNext();) {
                altElement = (Element) iter.next();
                if ((altString = altElement.elementText("GuaranteeTypeCode")) != null) {
                    switch (altString) {
                    case "1":
                        if (altElement.element("AmountRate") != null)
                            addionalObligationsResource.addProperty(PPROC.finalFinancialGuarantee,
                                    altElement.elementText("AmountRate"));
                        if (altElement.element("ConstitutionPeriod") != null
                                && altElement.element("ConstitutionPeriod").element("DurationMeasure") != null) {
                            altString = altElement.element("ConstitutionPeriod").elementText("DurationMeasure");
                            altString2 = altElement.element("ConstitutionPeriod").element("DurationMeasure")
                                    .attributeValue("unitCode");
                            switch (altString2) {
                            case "DAY":
                                addionalObligationsResource.addProperty(PPROC.finalFinancialGuaranteeDuration, "P"
                                        + altString + "D", XSDDatatype.XSDduration);
                                break;
                            case "MON":
                                addionalObligationsResource.addProperty(PPROC.finalFinancialGuaranteeDuration, "P"
                                        + altString + "M", XSDDatatype.XSDduration);
                                break;
                            case "ANN":
                                addionalObligationsResource.addProperty(PPROC.finalFinancialGuaranteeDuration, "P"
                                        + altString + "Y", XSDDatatype.XSDduration);
                                break;
                            }
                        }
                        break;
                    case "2":
                        if (altElement.element("AmountRate") != null)
                            addionalObligationsResource.addProperty(PPROC.provisionalFinancialGuarantee,
                                    altElement.elementText("AmountRate"));
                        break;
                    case "3":
                        altString = "Garantía especial";
                        if (altElement.element("Description") != null)
                            altString += " | " + altElement.elementText("Description");
                        if (altElement.element("AmountRate") != null)
                            altString += " | Porcentaje: " + altElement.elementText("AmountRate");
                        if (altElement.element("LiabilityAmount") != null)
                            altString += " | Importe: " + altElement.elementText("LiabilityAmount");
                        addionalObligationsResource.addProperty(PPROC.otherGuarantee, altString, XSDDatatype.XSDstring);
                    }
                }
            }
            cpeResource.addProperty(PPROC.contractAdditionalObligations, addionalObligationsResource);
            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
        }

        // pproc:ContractAdditionalObligations pproc:advertisementAmount
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("MaximumAdvertisementAmount") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            Resource addionalObligationsResource = model.createResource(cpeResource + "/ContractAdditionalObligations");
            addionalObligationsResource.addProperty(RDF.type, PPROC.ContractAdditionalObligations);

            altString2 = "Gastos máximos de publicidad: ";
            altString2 += document.getRootElement().element("TenderingTerms").elementText("MaximumAdvertisementAmount");
            altString2 += " "
                    + document.getRootElement().element("TenderingTerms").element("MaximumAdvertisementAmount")
                            .attributeValue("currencyID");
            addionalObligationsResource.addProperty(PPROC.advertisementAmount, altString2, XSDDatatype.XSDstring);
            cpeResource.addProperty(PPROC.contractAdditionalObligations, addionalObligationsResource);
            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
        }

        // pproc:ContractProcedureSpecifications pproc:tenderInformationProvider (1)
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("AdditionalInformationParty") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            Resource informationProviderResource = model.createResource(cpeResource + "/AdditionalInformationProvider");
            informationProviderResource.addProperty(RDF.type, PPROC.InformationProvider);

            // pproc:InformationProvider s:location
            Resource placeResource = model.createResource(informationProviderResource + "/Place");
            placeResource.addProperty(RDF.type, S.Place);
            informationProviderResource.addProperty(S.location, placeResource);

            // s:Place s:name
            if (document.getRootElement().element("TenderingTerms").element("AdditionalInformationParty")
                    .element("PartyName") != null
                    && (altString = document.getRootElement().element("TenderingTerms")
                            .element("AdditionalInformationParty").element("PartyName").elementText("Name")) != null)
                placeResource.addProperty(S.name, altString);

            // s:Place s:address
            if (document.getRootElement().element("TenderingTerms").element("AdditionalInformationParty")
                    .element("PostalAddress") != null) {
                Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                placeResource.addProperty(S.address, postalAddressResource);
                if ((altString = document.getRootElement().element("TenderingTerms")
                        .element("AdditionalInformationParty").element("PostalAddress").elementText("CityName")) != null)
                    postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                if ((altString = document.getRootElement().element("TenderingTerms")
                        .element("AdditionalInformationParty").element("PostalAddress").elementText("PostalZone")) != null)
                    postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                if ((altString = document.getRootElement().element("TenderingTerms")
                        .element("AdditionalInformationParty").element("PostalAddress").element("AddressLine")
                        .elementText("Line")) != null)
                    postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                if ((altString = document.getRootElement().element("TenderingTerms")
                        .element("AdditionalInformationParty").element("PostalAddress").element("Country")
                        .elementText("IdentificationCode")) != null)
                    postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
            }

            // pproc:InformationProvider pproc:estimatedEndDate
            if (document.getRootElement().element("TenderingProcess") != null
                    && document.getRootElement().element("TenderingProcess").element("DocumentAvailabilityPeriod") != null
                    && (altString = document.getRootElement().element("TenderingProcess")
                            .element("DocumentAvailabilityPeriod").elementText("EndDate")) != null)
                informationProviderResource.addProperty(PPROC.estimatedEndDate, altString, XSDDatatype.XSDdate);

            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            cpeResource.addProperty(PPROC.tenderInformationProvider, informationProviderResource);
        }

        // pproc:ContractProcedureSpecifications pproc:tenderInformationProvider (2)
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("DocumentProviderParty") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            Resource informationProviderResource = model.createResource(cpeResource + "/DocumentProviderParty");
            informationProviderResource.addProperty(RDF.type, PPROC.InformationProvider);

            // pproc:InformationProvider s:location
            Resource placeResource = model.createResource(informationProviderResource + "/Place");
            placeResource.addProperty(RDF.type, S.Place);
            informationProviderResource.addProperty(S.location, placeResource);

            // s:Place s:name
            if (document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                    .element("PartyName") != null
                    && (altString = document.getRootElement().element("TenderingTerms")
                            .element("DocumentProviderParty").element("PartyName").elementText("Name")) != null)
                placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);

            // s:Place s:address
            if (document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                    .element("PostalAddress") != null) {
                Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                placeResource.addProperty(S.address, postalAddressResource);
                if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                        .element("PostalAddress").elementText("CityName")) != null)
                    postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                        .element("PostalAddress").elementText("PostalZone")) != null)
                    postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                        .element("PostalAddress").element("AddressLine").elementText("Line")) != null)
                    postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                        .element("PostalAddress").element("Country").elementText("IdentificationCode")) != null)
                    postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
            }

            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            cpeResource.addProperty(PPROC.tenderInformationProvider, informationProviderResource);
        }

        // pproc:ContractProcedureSpecifications pproc:tenderDeadline
        // pproc:ContractProcedureSpecifications dcterms:description
        if (document.getRootElement().element("TenderingProcess") != null
                && document.getRootElement().element("TenderingProcess").element("TenderSubmissionDeadlinePeriod") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);

            // pproc:ContractProcedureSpecifications pproc:tenderDeadline
            if (document.getRootElement().element("TenderingProcess").element("TenderSubmissionDeadlinePeriod")
                    .element("EndDate") != null
                    && document.getRootElement().element("TenderingProcess").element("TenderSubmissionDeadlinePeriod")
                            .element("EndTime") != null) {
                altString = document.getRootElement().element("TenderingProcess")
                        .element("TenderSubmissionDeadlinePeriod").elementText("EndDate");
                altString2 = document.getRootElement().element("TenderingProcess")
                        .element("TenderSubmissionDeadlinePeriod").elementText("EndTime");
                if (altString.indexOf("+") != -1)
                    altString = altString.substring(0, altString.indexOf("+"));
                cpeResource.addProperty(PPROC.tenderDeadline, altString + "T" + altString2, XSDDatatype.XSDdateTime);
            }

            // pproc:ContractProcedureSpecifications dcterms:description
            if ((altString = document.getRootElement().element("TenderingProcess")
                    .element("TenderSubmissionDeadlinePeriod").elementText("Description")) != null)
                cpeResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);

            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
        }

        // pproc:Contract pproc:tenderRequirements
        if (document.getRootElement().element("TenderingTerms") != null
                && (document.getRootElement().element("TenderingTerms").element("TenderPreparation") != null || document
                        .getRootElement().element("TenderingTerms").element("TenderValidityPeriod") != null)) {
            Resource tenderRequirementsResource = model.createResource(contractResource + "/TenderRequirements");
            tenderRequirementsResource.addProperty(RDF.type, PPROC.TenderRequirements);

            // pproc:TenderRequirements pproc:tenderDocumentNeeds
            for (Iterator iter = document.getRootElement().element("TenderingTerms")
                    .elementIterator("TenderPreparation"); iter.hasNext();) {
                altElement = (Element) iter.next();
                altString2 = "Documentos sobre " + altElement.elementText("TenderEnvelopeID") + " : ";
                if ((altString = altElement.elementText("Description")) != null)
                    altString2 += " | " + altString;
                if (altElement.element("DocumentTenderRequirement") != null) {
                    for (Iterator iter2 = altElement.elementIterator("DocumentTenderRequirement"); iter2.hasNext();) {
                        altElement2 = (Element) iter2.next();
                        if ((altString = altElement2.elementText("Name")) != null)
                            altString2 += " | " + altString;
                        if ((altString = altElement2.elementText("Description")) != null)
                            altString2 += " | " + altString;
                    }
                }
                tenderRequirementsResource.addProperty(PPROC.tenderDocumentNeeds, altString2, XSDDatatype.XSDstring);
            }

            // pproc:TenderRequirements pproc:tenderManteinanceDuration
            if (document.getRootElement().element("TenderingTerms").element("TenderValidityPeriod") != null
                    && (altString = document.getRootElement().element("TenderingTerms").element("TenderValidityPeriod")
                            .elementText("DurationMeasure")) != null
                    && (altString2 = document.getRootElement().element("TenderingTerms")
                            .element("TenderValidityPeriod").element("DurationMeasure").attributeValue("unitCode")) != null)
                switch (altString2) {
                case "DAY":
                    tenderRequirementsResource.addProperty(PPROC.tenderManteinanceDuration, "P" + altString + "D",
                            XSDDatatype.XSDduration);
                    break;
                case "MON":
                    tenderRequirementsResource.addProperty(PPROC.tenderManteinanceDuration, "P" + altString + "M",
                            XSDDatatype.XSDduration);
                    break;
                case "ANN":
                    tenderRequirementsResource.addProperty(PPROC.tenderManteinanceDuration, "P" + altString + "Y",
                            XSDDatatype.XSDduration);
                    break;
                }

            contractResource.addProperty(PPROC.tenderRequirements, tenderRequirementsResource);
        }

        // pproc:ContractProcedureSpecifications pproc:tenderSubmissionLocation
        if (document.getRootElement().element("TenderingTerms") != null
                && document.getRootElement().element("TenderingTerms").element("TenderRecipentParty") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            Resource placeResource = model.createResource(cpeResource + "/TenderSubmissionLocation");
            placeResource.addProperty(RDF.type, S.Place);

            // s:Place s:name
            if ((altString = document.getRootElement().element("TenderingTerms").element("TenderRecipentParty")
                    .element("PartyName").elementText("Name")) != null)
                placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);

            // s:Place s:address
            Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
            postalAddressResource.addProperty(RDF.type, S.PostalAddress);
            placeResource.addProperty(S.address, postalAddressResource);
            if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                    .element("PostalAddress").elementText("CityName")) != null)
                postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
            if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                    .element("PostalAddress").elementText("PostalZone")) != null)
                postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
            if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                    .element("PostalAddress").element("AddressLine").elementText("Line")) != null)
                postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
            if ((altString = document.getRootElement().element("TenderingTerms").element("DocumentProviderParty")
                    .element("PostalAddress").element("Country").elementText("IdentificationCode")) != null)
                postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);

            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            cpeResource.addProperty(PPROC.tenderSubmissionLocation, placeResource);
        }

        // pproc:ContractProcedureSpecifications pproc:contractActivities
        if (document.getRootElement().element("TenderingProcess") != null
                && document.getRootElement().element("TenderingProcess").element("OpenTenderEvent") != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            Resource contractActivitiesResource = model.createResource(cpeResource + "/ContractActivities");
            contractActivitiesResource.addProperty(RDF.type, PPROC.ContractActivities);

            // pproc:ContractActivities pproc:tenderMeeting
            for (Iterator iter = document.getRootElement().element("TenderingProcess")
                    .elementIterator("OpenTenderEvent"); iter.hasNext();) {
                altElement = (Element) iter.next();
                Resource tenderMeetingResource = model.createResource(contractActivitiesResource + "/Event_"
                        + getEventID());
                tenderMeetingResource.addProperty(RDF.type, PPROC.TenderMeeting);
                tenderMeetingResource.addProperty(RDF.type, S.Event);

                // pproc:TenderMeeting pproc:tenderPurpose
                if ((altString = altElement.elementText("Description")) != null)
                    tenderMeetingResource.addProperty(PPROC.tenderPurpose, altString, XSDDatatype.XSDstring);
                else if ((altString = altElement.elementText("TypeCode")) != null)
                    // Si no hay descripción textual, usaremos la del código
                    // TODO parseo independiente de versión (parseo del .gc)
                    switch (altString) {
                    case "1":
                        tenderMeetingResource.addProperty(PPROC.tenderPurpose, "Apertura sobre administrativa",
                                XSDDatatype.XSDstring);
                        break;
                    case "2":
                        tenderMeetingResource.addProperty(PPROC.tenderPurpose, "Apertura sobre oferta técnica",
                                XSDDatatype.XSDstring);
                        break;
                    case "3":
                        tenderMeetingResource.addProperty(PPROC.tenderPurpose,
                                "Apertura sobre oferta técnica y económica", XSDDatatype.XSDstring);
                        break;
                    case "4":
                        tenderMeetingResource.addProperty(PPROC.tenderPurpose, "Apertura sobre oferta económica",
                                XSDDatatype.XSDstring);
                        break;
                    }

                // pproc:TenderMeeting s:location
                if (altElement.elementText("OcurrenceLocation") != null) {
                    Resource placeResource = model.createResource(tenderMeetingResource + "/Place");
                    placeResource.addProperty(RDF.type, S.Place);

                    // s:Place s:name
                    if ((altString = altElement.elementText("Description")) != null)
                        placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);

                    // s:Place s:address
                    Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                    postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                    placeResource.addProperty(S.address, postalAddressResource);
                    if ((altString = altElement.element("Address").elementText("CityName")) != null)
                        postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                    if ((altString = altElement.element("Address").elementText("PostalZone")) != null)
                        postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                    if ((altString = altElement.element("Address").element("AddressLine").elementText("Line")) != null)
                        postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                    if ((altString = altElement.element("Address").element("Country").elementText("IdentificationCode")) != null)
                        postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);

                    tenderMeetingResource.addProperty(S.location, placeResource);
                }

                // pproc:TenderMeeting s:startDate
                if ((altString = altElement.elementText("OcurrenceDate")) != null
                        && (altString2 = altElement.elementText("OcurrenceTime")) != null) {
                    if (altString.indexOf("+") != -1)
                        altString = altString.substring(0, altString.indexOf("+"));
                    tenderMeetingResource.addProperty(S.startDate, altString + "T" + altString2,
                            XSDDatatype.XSDdateTime);
                }

                contractActivitiesResource.addProperty(PPROC.tenderMeeting, tenderMeetingResource);
            }

            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            cpeResource.addProperty(PPROC.contractActivities, contractActivitiesResource);
        }

        // pproc:Contract pc:tender
        // pproc:Contract pproc:contractOrProcedureExtinction
        if (document.getRootElement().element("TenderResult") != null) {

            // pc:Tender rdf:type
            // pproc:ContractOrProcedureExtinction rdf:type
            // pproc:ContractOrProcedureExtinction extinctionCause
            if (document.getRootElement().element("TenderResult") != null) {

                for (Iterator iter = document.getRootElement().elementIterator("TenderResult"); iter.hasNext();) {
                    altElement = (Element) iter.next();
                    boolean isAwarded = false;
                    boolean isFormalized = false;

                    if ((altString = altElement.elementText("ResultCode")) != null) {
                        switch (altString) {
                        case "2":
                        case "8":
                            isAwarded = true;
                            break;
                        case "9":
                            isFormalized = true;
                            break;
                        case "3":
                            Resource procedureVoidResource = model.createResource(contractResource + "/ProcedureVoid");
                            procedureVoidResource.addProperty(RDF.type, PPROC.ContractOrProcedureExtinction);
                            procedureVoidResource.addProperty(RDF.type, PPROC.ProcedureVoid);
                            if ((altString = altElement.elementText("Description")) != null)
                                procedureVoidResource.addProperty(PPROC.extinctionCause, altString,
                                        XSDDatatype.XSDstring);
                            contractResource.addProperty(PPROC.contractOrProcedureExtinction, procedureVoidResource);
                            contractResource.addProperty(PPROC.procedureVoid, procedureVoidResource);
                            break;
                        case "4":
                            Resource procedureResignation = model.createResource(contractResource
                                    + "/ProcedureResignation");
                            procedureResignation.addProperty(RDF.type, PPROC.ContractOrProcedureExtinction);
                            procedureResignation.addProperty(RDF.type, PPROC.ProcedureResignation);
                            if ((altString = altElement.elementText("Description")) != null)
                                procedureResignation.addProperty(PPROC.extinctionCause, altString,
                                        XSDDatatype.XSDstring);
                            contractResource.addProperty(PPROC.contractOrProcedureExtinction, procedureResignation);
                            contractResource.addProperty(PPROC.procedureResignation, procedureResignation);
                            break;
                        case "5":
                            Resource procedureWaive = model.createResource(contractResource + "/ContractWaive");
                            procedureWaive.addProperty(RDF.type, PPROC.ContractOrProcedureExtinction);
                            procedureWaive.addProperty(RDF.type, PPROC.ProcedureWaive);
                            if ((altString = altElement.elementText("Description")) != null)
                                procedureWaive.addProperty(PPROC.extinctionCause, altString, XSDDatatype.XSDstring);
                            contractResource.addProperty(PPROC.contractOrProcedureExtinction, procedureWaive);
                            contractResource.addProperty(PPROC.procedureWaive, procedureWaive);
                            break;
                        }
                    }

                    // pc:Tender
                    if (isAwarded || isFormalized) {
                        Resource tenderResource = model.createResource(contractResource + "/Tender");
                        tenderResource.addProperty(RDF.type, PC.Tender);

                        if (isAwarded)
                            tenderResource.addProperty(RDF.type, PPROC.AwardedTender);
                        if (isFormalized)
                            tenderResource.addProperty(RDF.type, PPROC.FormalizedTender);

                        // pc:Tender pc:supplier
                        if (altElement.element("WinningParty") != null) {

                            if (altElement.element("WinningParty").element("PartyIdentification") != null) {
                                altString = BASE_URI_ORGANIZATION
                                        + altElement.element("WinningParty").element("PartyIdentification")
                                                .elementText("ID");
                                altString = altString.replace(" ", "");
                                // altSupplierResource = ResourceFactory.createResource(BASE_URI_ORGANIZATION
                                // + altElement.element("WinningParty").element("PartyIdentification")
                                // .elementText("ID"));
                            } else {
                                altString = tenderResource + "/Supplier";
                                // altSupplierResource = ResourceFactory.createResource(tenderResource + "/Supplier");
                            }

                            if (!model.containsResource(ResourceFactory.createResource(altString))) {

                                // org:Organization rdf:type
                                Resource supplierResource = model.createResource(altString);
                                supplierResource.addProperty(RDF.type, ORG.Organization);

                                // org:Organization dcterms:title
                                if ((altString = altElement.element("WinningParty").element("PartyName")
                                        .elementText("Name")) != null)
                                    supplierResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);

                                // org:Organization dcterms:identifier
                                if ((altString = altElement.element("WinningParty").elementText("PartyIdentification")) != null) {
                                    supplierResource.addProperty(DCTerms.identifier, altString, XSDDatatype.XSDstring);
                                }

                                // org:Organization org:hasSite
                                // TODO con esta y otras PartyType, coger PostalAddress y PhysicalLocation
                                if (altElement.element("WinningParty").element("PhysicalLocation") != null
                                        && altElement.element("WinningParty").element("PhysicalLocation")
                                                .element("Address") != null) {
                                    Resource placeResource = model.createResource(supplierResource + "/Place");
                                    placeResource.addProperty(RDF.type, S.Place);
                                    supplierResource.addProperty(ORG.hasSite, placeResource);

                                    // s:address s:PostalAddress
                                    Resource postalAddressResource = model.createResource(placeResource
                                            + "/PostalAddress");
                                    postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                                    placeResource.addProperty(S.address, postalAddressResource);
                                    if ((altString = altElement.element("WinningParty").element("PhysicalLocation")
                                            .element("Address").elementText("CityName")) != null)
                                        postalAddressResource.addProperty(S.addressLocality, altString,
                                                XSDDatatype.XSDstring);
                                    if ((altString = altElement.element("WinningParty").element("PhysicalLocation")
                                            .element("Address").elementText("PostalZone")) != null)
                                        postalAddressResource.addProperty(S.postalCode, altString,
                                                XSDDatatype.XSDstring);
                                    if (altElement.element("WinningParty").element("PhysicalLocation")
                                            .element("Address").element("AddressLine") != null
                                            && (altString = altElement.element("WinningParty")
                                                    .element("PhysicalLocation").element("Address")
                                                    .element("AddressLine").elementText("Line")) != null)
                                        postalAddressResource.addProperty(S.streetAddress, altString,
                                                XSDDatatype.XSDstring);
                                    if ((altString = altElement.element("WinningParty").element("PhysicalLocation")
                                            .element("Address").element("Country").elementText("IdentificationCode")) != null)
                                        postalAddressResource.addProperty(S.addressCountry, altString,
                                                XSDDatatype.XSDstring);
                                }

                                if (altElement.element("WinningParty").element("Contact") != null) {
                                    // org:Organization s:telephone
                                    if ((altString = altElement.element("WinningParty").element("Contact")
                                            .elementText("Telephone")) != null)
                                        supplierResource.addProperty(S.telephone, altString, XSDDatatype.XSDstring);

                                    // org:Organization s:faxNumber
                                    if ((altString = altElement.element("WinningParty").element("Contact")
                                            .elementText("Telefax")) != null)
                                        supplierResource.addProperty(S.faxNumber, altString, XSDDatatype.XSDstring);

                                    // org:Organization s:email
                                    if ((altString = altElement.element("WinningParty").element("Contact")
                                            .elementText("ElectronicMail")) != null)
                                        supplierResource.addProperty(S.email, altString, XSDDatatype.XSDstring);
                                }
                            }

                        }

                        // pc:Tender pc:offeredPrice
                        if (altElement.element("AwardedTenderedProject") != null) {

                            // gr:BundlePriceSpecification with taxes
                            if ((altString = altElement.element("AwardedTenderedProject").element("LegalMonetaryTotal")
                                    .elementText("PayableAmount")) != null) {
                                Resource priceResource = model.createResource(tenderResource + "/OfferedPrice");
                                priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                                priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                                priceResource.addProperty(GR.hasCurrency,
                                        altElement.element("AwardedTenderedProject").element("LegalMonetaryTotal")
                                                .element("PayableAmount").attributeValue("currencyID"),
                                        XSDDatatype.XSDstring);
                                priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                                tenderResource.addProperty(PC.offeredPrice, priceResource);
                            }

                            // gr:BundlePriceSpecification without taxes
                            if ((altString = altElement.element("AwardedTenderedProject").element("LegalMonetaryTotal")
                                    .elementText("TaxExclusiveAmount")) != null) {
                                Resource priceResource = model.createResource(tenderResource
                                        + "/OfferedPriceWithoutTaxes");
                                priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                                priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                                priceResource.addProperty(GR.hasCurrency,
                                        altElement.element("AwardedTenderedProject").element("LegalMonetaryTotal")
                                                .element("TaxExclusiveAmount").attributeValue("currencyID"),
                                        XSDDatatype.XSDstring);
                                priceResource.addProperty(GR.valueAddedTaxIncluded, "false", XSDDatatype.XSDboolean);
                                tenderResource.addProperty(PC.offeredPrice, priceResource);
                            }
                        }

                        contractResource.addProperty(PC.tender, tenderResource);
                    }
                }
            }
        }

        // pproc:Contract pproc:legalDocumentReference
        if ((altElement = document.getRootElement().element("LegalDocumentReference")) != null)
            if ((altString = altElement.element("Attachment").element("ExternalReference").elementText("URI")) != null) {
                Resource documentResource = model.createResource(altString);
                documentResource.addProperty(RDF.type, FOAF.Document);
                contractResource.addProperty(PPROC.legalDocumentReference, documentResource);
            }

        // pproc:Contract pproc:technicalDocumentReference
        if ((altElement = document.getRootElement().element("TechnicalDocumentReference")) != null)
            if ((altString = altElement.element("Attachment").element("ExternalReference").elementText("URI")) != null) {
                Resource documentResource = model.createResource(altString);
                documentResource.addProperty(RDF.type, FOAF.Document);
                contractResource.addProperty(PPROC.technicalDocumentReference, documentResource);
            }

        // pproc:Contract pproc:additionalDocumentReference (1)
        if ((altElement = document.getRootElement().element("AdditionalDocumentReference")) != null)
            if ((altString = altElement.element("Attachment").element("ExternalReference").elementText("URI")) != null) {
                Resource documentResource = model.createResource(altString);
                documentResource.addProperty(RDF.type, FOAF.Document);
                contractResource.addProperty(PPROC.additionalDocumentReference, documentResource);
            }

        // pproc:Contract pproc:additionalDocumentReference (2)
        if (document.getRootElement().element("ContractModification") != null
                && (altElement = document.getRootElement().element("ContractModification")
                        .element("ContractModificationDocumentReference")) != null)
            if ((altString = altElement.element("Attachment").element("ExternalReference").elementText("URI")) != null) {
                Resource documentResource = model.createResource(altString);
                documentResource.addProperty(RDF.type, FOAF.Document);
                contractResource.addProperty(PPROC.additionalDocumentReference, documentResource);
            }

        // TODO organizar conforme a la estructura de codice para no consultar varias veces los mismos elementos

        // TODO notices

        // TODO lots

    }

    // TODO CODICE % used? PPROC % used?

    private static int eventId = 1;

    private static int getEventID() {
        return eventId++;
    }

}
