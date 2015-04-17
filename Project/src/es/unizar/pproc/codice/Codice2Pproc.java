package es.unizar.pproc.codice;

import java.util.Iterator;

import org.apache.jena.atlas.logging.Log;
import org.dom4j.Document;
import org.dom4j.Element;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import es.unizar.pproc.vocabulary.CPV;
import es.unizar.pproc.vocabulary.GR;
import es.unizar.pproc.vocabulary.ORG;
import es.unizar.pproc.vocabulary.PC;
import es.unizar.pproc.vocabulary.PPROC;
import es.unizar.pproc.vocabulary.S;
import es.unizar.pproc.vocabulary.SKOS;

public class Codice2Pproc {

    static {
        org.apache.log4j.BasicConfigurator.configure();
    }

    private static final String BASE_URI_CONTRACT = "http://pproc.unizar.es/recurso/sector-publico/contrato/";
    private static final String BASE_URI_ORGANIZATION = "http://pproc.unizar.es/recurso/sector-publico/organization/";

    private static String altString, altString2;
    private static Element altElement, altElement2, altElement3;

    private static void parseProcurementProjectElement(Model model, Element altElement, Resource rootResource) {

        // ProcurementProject/Name
        if ((altString = altElement.elementText("Name")) != null) {
            rootResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
            rootResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
        }

        // ProcurementProject/Description
        if ((altString = altElement.elementText("Description")) != null) {
            rootResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
            rootResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
        }

        // ProcurementProject/TypeCode
        // TODO parseo independiente de versión (parseo del .gc)
        if ((altString = altElement.elementText("TypeCode")) != null) {
            switch (altString) {
            case "1":
                rootResource.addProperty(RDF.type, PPROC.SuppliesContract);
                // ProcurementProject/SubTypeCode
                // TODO parseo independiente de versión (parseo del .gc)
                if ((altString = altElement.elementText("SubTypeCode")) != null)
                    switch (altString) {
                    case "1":
                        rootResource.addProperty(RDF.type, PPROC.RentContract);
                        break;
                    case "2":
                        rootResource.addProperty(RDF.type, PPROC.BuyContract);
                    }

                break;
            case "2":
                rootResource.addProperty(RDF.type, PPROC.ServicesContract);
                break;
            case "3":
                rootResource.addProperty(RDF.type, PPROC.WorksContract);
                break;
            case "21":
                rootResource.addProperty(RDF.type, PPROC.PublicServicesManagementContract);
                break;
            case "31":
                rootResource.addProperty(RDF.type, PPROC.PublicWorksConcessionContract);
                break;
            case "40":
                rootResource.addProperty(RDF.type, PPROC.PublicPrivatePartnershipContract);
                break;
            case "7":
                rootResource.addProperty(RDF.type, PPROC.SpecialAdministrativeContract);
                break;
            case "8":
                rootResource.addProperty(RDF.type, PPROC.PrivateContract);
                break;
            case "50":
                // Contrato patrimonial, no hay equivalencia en PPROC
            }
        }

        // ProcurementProject/PlannedPeriod
        if (altElement.element("PlannedPeriod") != null) {
            Resource objectResource = model.createResource(rootResource + "/ContractObject");
            objectResource.addProperty(RDF.type, PPROC.ContractObject);
            rootResource.addProperty(PPROC.contractObject, objectResource);
            Resource ctcResource = model.createResource(rootResource + "/ContractTemporalConditions");
            ctcResource.addProperty(RDF.type, PPROC.ContractTemporalConditions);
            objectResource.addProperty(PPROC.contractTemporalConditions, ctcResource);

            // ProcurementProject/PlannedPeriod/Description
            if ((altString = altElement.element("PlannedPeriod").elementText("Note")) != null) {
                ctcResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                ctcResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
            }

            // ProcurementProject/PlannedPeriod/DurationMeasure
            if ((altString = altElement.element("PlannedPeriod").elementText("DurationMeasure")) != null
                    && (altString2 = altElement.element("PlannedPeriod").element("DurationMeasure")
                            .attributeValue("unitCode")) != null)
                switch (altString2) {
                case "DAY":
                    ctcResource.addProperty(PPROC.estimatedDuration, "P" + altString + "D", XSDDatatype.XSDduration);
                    break;
                case "MON":
                    ctcResource.addProperty(PPROC.estimatedDuration, "P" + altString + "M", XSDDatatype.XSDduration);
                    break;
                case "ANN":
                    ctcResource.addProperty(PPROC.estimatedDuration, "P" + altString + "Y", XSDDatatype.XSDduration);
                    break;
                }

            // ProcurementProject/PlannedPeriod/EndDate
            if ((altString = altElement.element("PlannedPeriod").elementText("EndDate")) != null)
                ctcResource.addProperty(PPROC.estimatedEndDate, altString, XSDDatatype.XSDdate);
        }

        // ProcurementProject/BudgetAmount
        if (altElement.element("BudgetAmount") != null) {
            Resource objectResource = model.createResource(rootResource + "/ContractObject");
            objectResource.addProperty(RDF.type, PPROC.ContractObject);
            rootResource.addProperty(PPROC.contractObject, objectResource);
            Resource cecResource = model.createResource(rootResource + "/ContractEconomicConditions");
            cecResource.addProperty(RDF.type, PPROC.ContractEconomicConditions);
            objectResource.addProperty(PPROC.contractEconomicConditions, cecResource);

            // ProcurementProject/BudgetAmount/EstimatedOverallContractAmount
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
            if ((altString = altElement.element("BudgetAmount").elementText("TotalAmount")) != null) {
                Resource priceResource = model.createResource(cecResource + "/TotalAmount");
                cecResource.addProperty(PPROC.budgetPrice, priceResource);
                priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                if ((altString = altElement.element("BudgetAmount").element("TotalAmount").attributeValue("currencyID")) != null)
                    priceResource.addProperty(GR.hasCurrency, altString, XSDDatatype.XSDstring);
                cecResource.addProperty(PPROC.budgetPrice, priceResource);
            }

            // ProcurementProject/BudgetAmount/TaxExclusiveAmount
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
        }

        // ProcurementProject/RequiredFeeAmount
        if ((altString = altElement.elementText("RequiredFeeAmount")) != null) {
            Resource objectResource = model.createResource(rootResource + "/ContractObject");
            objectResource.addProperty(RDF.type, PPROC.ContractObject);
            rootResource.addProperty(PPROC.contractObject, objectResource);
            Resource cecResource = model.createResource(rootResource + "/ContractEconomicConditions");
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
        if (altElement.element("RequiredCommodityClassification") != null
                && (altString = altElement.element("RequiredCommodityClassification").elementText(
                        "ItemClassificationCode")) != null) {
            Resource objectResource = model.createResource(rootResource + "/ContractObject");
            objectResource.addProperty(RDF.type, PPROC.ContractObject);
            rootResource.addProperty(PPROC.contractObject, objectResource);
            objectResource.addProperty(PPROC.mainObject, CPV.code(altString));
        }

        // ProcurementProject/RequestForTenderLine
        if (altElement.element("RequestForTenderLine") != null) {
            for (Iterator<?> iter = altElement.elementIterator("RequestForTenderLine"); iter.hasNext();) {
                altElement2 = (Element) iter.next();
                Resource objectResource = model.createResource(rootResource + "/ContractObject");
                objectResource.addProperty(RDF.type, PPROC.ContractObject);
                rootResource.addProperty(PPROC.contractObject, objectResource);
                Resource offeringResource = model.createResource(objectResource + "Offering_"
                        + altElement2.elementText("ID"));
                offeringResource.addProperty(RDF.type, GR.Offering);
                objectResource.addProperty(PPROC.provision, offeringResource);

                // ProcurementProject/RequestForTenderLine/Item/Name
                if ((altString = altElement2.element("Item").elementText("Name")) != null) {
                    offeringResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
                    offeringResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/RequestForTenderLine/Item/Description
                if ((altString = altElement2.element("Item").elementText("Description")) != null) {
                    offeringResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                    offeringResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/RequestForTenderLine/Item/AdditionalInformation
                if ((altString = altElement2.element("Item").elementText("AdditionalInformation")) != null) {
                    offeringResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
                    offeringResource.addProperty(RDFS.comment, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/RequestForTenderLine/Quantity
                if ((altString = altElement2.elementText("Quantity")) != null) {
                    Resource quantityResource = model.createResource(offeringResource + "/QuantitativeValue");
                    quantityResource.addProperty(RDF.type, GR.QuantitativeValue);
                    offeringResource.addProperty(GR.hasEligibleQuantity, quantityResource);
                    quantityResource.addProperty(GR.hasValue, altString);
                    if ((altString = altElement2.element("Quantity").attributeValue("unitCode")) != null)
                        quantityResource.addProperty(GR.hasUnitOfMeasurement, altString, XSDDatatype.XSDstring);
                }

                // ProcurementProject/RequestForTenderLine/MaximumTaxExclusiveAmount
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
                    if ((altString = altElement2.element("RequiredItemLocationQuantity").elementText("MinimumQuantity")) != null) {
                        Resource quantityResource = model.createResource(priceResource + "/QuantitativeValue");
                        quantityResource.addProperty(RDF.type, GR.QuantitativeValue);
                        priceResource.addProperty(GR.hasEligibleQuantity, quantityResource);
                        quantityResource.addProperty(GR.hasMinValue, altString);
                        if ((altString = altElement2.element("RequiredItemLocationQuantity").element("MinimumQuantity")
                                .attributeValue("unitCode")) != null)
                            quantityResource.addProperty(GR.hasUnitOfMeasurement, altString, XSDDatatype.XSDstring);
                    }

                    // ProcurementProject/RequestForTenderLine/RequiredItemLocationQuantity/MaximumQuantity
                    if ((altString = altElement2.element("RequiredItemLocationQuantity").elementText("MaximumQuantity")) != null) {
                        Resource quantityResource = model.createResource(priceResource + "/QuantitativeValue");
                        quantityResource.addProperty(RDF.type, GR.QuantitativeValue);
                        priceResource.addProperty(GR.hasEligibleQuantity, quantityResource);
                        quantityResource.addProperty(GR.hasMaxValue, altString);
                        if ((altString = altElement2.element("RequiredItemLocationQuantity").element("MaximumQuantity")
                                .attributeValue("unitCode")) != null)
                            quantityResource.addProperty(GR.hasUnitOfMeasurement, altString, XSDDatatype.XSDstring);
                    }
                }
            }
        }

        // ProcurementProject/RealizedLocation
        if (altElement.element("RealizedLocation") != null) {
            Resource objectResource = model.createResource(rootResource + "/ContractObject");
            objectResource.addProperty(RDF.type, PPROC.ContractObject);
            rootResource.addProperty(PPROC.contractObject, objectResource);
            Resource placeResource = model.createResource(rootResource + "/Place");
            placeResource.addProperty(RDF.type, S.Place);
            objectResource.addProperty(ORG.hasSite, placeResource);

            // ProcurementProject/RealizedLocation/Description
            if ((altString = altElement.element("RealizedLocation").elementText("Description")) != null) {
                placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);
                placeResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
                placeResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
            }

            // ProcurementProject/RealizedLocation/Address
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
            if ((altString = altElement.element("RealizedLocation").elementText("CountrySubentityCode")) != null) {
                Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                placeResource.addProperty(S.address, postalAddressResource);
                postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
            }
        }

        // ProcurementProject/ContractExtension
        if (altElement.element("ContractExtension") != null) {
            Resource objectResource = model.createResource(rootResource + "/ContractObject");
            objectResource.addProperty(RDF.type, PPROC.ContractObject);
            rootResource.addProperty(PPROC.contractObject, objectResource);
            Resource ctcResource = model.createResource(rootResource + "/ContractTemporalConditions");
            ctcResource.addProperty(RDF.type, PPROC.ContractTemporalConditions);
            objectResource.addProperty(PPROC.contractTemporalConditions, ctcResource);

            // ProcurementProject/ContractExtension/OptionValidityPeriod/Description
            if ((altString = altElement.element("ContractExtension").element("OptionValidityPeriod")
                    .elementText("Description")) != null) {
                ctcResource.addProperty(DCTerms.description, "Período de validez de opciones de extensión: "
                        + altString, XSDDatatype.XSDstring);
                ctcResource.addProperty(RDFS.comment, "Período de validez de opciones de extensión: " + altString,
                        XSDDatatype.XSDstring);
            }

            // ProcurementProject/ContractExtension/OptionsDescription
            if ((altString = altElement.element("ContractExtension").elementText("OptionsDescription")) != null) {
                ctcResource.addProperty(DCTerms.description, "Opciones de extensión: " + altString,
                        XSDDatatype.XSDstring);
                ctcResource.addProperty(RDFS.comment, "Opciones de extensión: " + altString, XSDDatatype.XSDstring);
            }
        }
    }

    private static void parseTenderingTermsElement(Model model, Element altElement, Resource rootResource,
            String documentAvailabilityPeriod) {

        // TenderingTerms/TendererQualificationRequest
        if (altElement.element("TendererQualificationRequest") != null) {

            Log.info(Codice2Pproc.class, altElement.getUniquePath());
            Resource tenderersRequirementsResource = model.createResource(rootResource + "/TenderersRequirements");
            tenderersRequirementsResource.addProperty(RDF.type, PPROC.TenderersRequirements);
            rootResource.addProperty(PPROC.tenderersRequirements, tenderersRequirementsResource);

            // TenderingTerms/TendererQualificationRequest/RequiredBusinessClassificationScheme
            if (altElement.element("TendererQualificationRequest").element("RequiredBusinessClassificationScheme") != null
                    && (altString = altElement.element("TendererQualificationRequest")
                            .element("RequiredBusinessClassificationScheme").elementText("CodeValue")) != null)
                tenderersRequirementsResource.addProperty(PPROC.requiredClassification, altString,
                        XSDDatatype.XSDstring);

            // TenderingTerms/TendererQualificationRequest/FinancialEvaluationCriteria
            if (altElement.element("TendererQualificationRequest").element("FinancialEvaluationCriteria") != null
                    && (altString = altElement.element("TendererQualificationRequest")
                            .element("FinancialEvaluationCriteria").elementText("Description")) != null)
                tenderersRequirementsResource.addProperty(PPROC.requiredEconomicAndFinancialStanding, altString,
                        XSDDatatype.XSDstring);

            // TenderingTerms/TendererQualificationRequest/TechnicalEvaluationCriteria
            if (altElement.element("TendererQualificationRequest").element("TechnicalEvaluationCriteria") != null
                    && (altString = altElement.element("TendererQualificationRequest")
                            .element("TechnicalEvaluationCriteria").elementText("Description")) != null)
                tenderersRequirementsResource.addProperty(PPROC.requiredTechnicalAndProfessionalAbility, altString,
                        XSDDatatype.XSDstring);

            // TenderingTerms/TendererQualificationRequest/SpecificTendererRequirement
            if (altElement.element("TendererQualificationRequest").element("SpecificTendererRequirement") != null)
                for (Iterator<?> iter = altElement.element("TendererQualificationRequest").elementIterator(
                        "SpecificTendererRequirement"); iter.hasNext();) {
                    altElement = (Element) iter.next();
                    if (altElement.element("RequirementTypeCode") != null)
                        tenderersRequirementsResource.addProperty(PPROC.otherAbilityRequisites,
                                altElement.element("RequirementTypeCode").attributeValue("name"));
                    if ((altString = altElement.elementText("Description")) != null)
                        tenderersRequirementsResource.addProperty(PPROC.otherAbilityRequisites, altString,
                                XSDDatatype.XSDstring);
                }
        }

        // TenderingTerms/AwardingTerms
        if (altElement.element("AwardingTerms") != null
                && altElement.element("AwardingTerms").element("AwardingCriteria") != null) {
            Resource criteriaCombinationResource = model.createResource(rootResource + "/AwardCriteriaCombination");
            criteriaCombinationResource.addProperty(RDF.type, PC.AwardCriteriaCombination);
            rootResource.addProperty(PC.awardCriteriaCombination, criteriaCombinationResource);

            // TenderingTerms/AwardingTerms/WeightingAlgorithmCode
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = altElement.element("AwardingTerms").elementText("WeightingAlgorithmCode")) != null)
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

            // TenderingTerms/AwardingTerms/AwardingCriteria
            for (Iterator<?> iter = altElement.element("AwardingTerms").elementIterator("AwardingCriteria"); iter
                    .hasNext();) {
                altElement2 = (Element) iter.next();
                Resource criterionResource = model.createResource(criteriaCombinationResource + "/Criterion"
                        + altElement2.elementText("ID"));
                criterionResource.addProperty(RDF.type, PC.AwardCriterion);
                criteriaCombinationResource.addProperty(PC.awardCriterion, criterionResource);

                // TenderingTerms/AwardingTerms/AwardingCriteria/AwardingCriteriaTypeCode
                // TODO parseo independiente de versión (parseo del .gc)
                if ((altString = altElement2.elementText("AwardingCriteriaTypeCode")) != null)
                    switch (altString) {
                    case "SUBJ":
                        criterionResource.addProperty(RDF.type, PPROC.SubjectiveAwardCriterion);
                        break;
                    case "OBJ":
                        criterionResource.addProperty(RDF.type, PPROC.ObjectiveAwardCriterion);
                    }

                // TenderingTerms/AwardingTerms/AwardingCriteria/Description
                if ((altString = altElement2.elementText("Description")) != null)
                    criterionResource.addProperty(PC.criterionName, altString, XSDDatatype.XSDstring);

                // TenderingTerms/AwardingTerms/AwardingCriteria/WeightNumeric
                if ((altString = altElement2.elementText("WeightNumeric")) != null)
                    criterionResource.addProperty(PC.criterionWeight, altString, XSDDatatype.XSDfloat);

                // TenderingTerms/AwardingTerms/AwardingCriteria/CalculationExpression
                if ((altString = altElement2.elementText("CalculationExpression")) != null)
                    criterionResource.addProperty(PPROC.criterionEvaluationMode, altString, XSDDatatype.XSDstring);

                // TenderingTerms/AwardingTerms/AwardingCriteria
                altString2 = "";
                if ((altString = altElement2.elementText("MinimumQuantity")) != null)
                    altString2 += "Cantidad mínima: " + altString + ". ";
                if ((altString = altElement2.elementText("MaximumQuantity")) != null)
                    altString2 += "Cantidad máxima: " + altString + ". ";
                if ((altString = altElement2.elementText("MinimumAmount")) != null)
                    altString2 += "Importe mínimo: " + altString + ". ";
                if ((altString = altElement2.elementText("MaximumAmount")) != null)
                    altString2 += "Importe máximo: " + altString + ". ";
                if (altString2 != "")
                    criterionResource.addProperty(PPROC.criterionMaxAndMinScores, altString2, XSDDatatype.XSDstring);
            }
        }

        // TenderingTerms/RequiredFinancialGuarantee
        if (altElement.element("RequiredFinancialGuarantee") != null) {
            Resource cpeResource = model.createResource(rootResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            rootResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            Resource addionalObligationsResource = model.createResource(cpeResource + "/ContractAdditionalObligations");
            addionalObligationsResource.addProperty(RDF.type, PPROC.ContractAdditionalObligations);
            cpeResource.addProperty(PPROC.contractAdditionalObligations, addionalObligationsResource);
            for (Iterator<?> iter = altElement.elementIterator("RequiredFinancialGuarantee"); iter.hasNext();) {
                altElement2 = (Element) iter.next();

                // TenderingTerms/RequiredFinancialGuarantee/GuaranteeTypeCode
                if ((altString = altElement2.elementText("GuaranteeTypeCode")) != null) {
                    switch (altString) {
                    case "1":
                        if (altElement2.element("AmountRate") != null)
                            addionalObligationsResource.addProperty(PPROC.finalFinancialGuarantee,
                                    altElement2.elementText("AmountRate"));
                        if (altElement2.element("ConstitutionPeriod") != null
                                && altElement2.element("ConstitutionPeriod").element("DurationMeasure") != null) {
                            altString = altElement2.element("ConstitutionPeriod").elementText("DurationMeasure");
                            altString2 = altElement2.element("ConstitutionPeriod").element("DurationMeasure")
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
                                    altElement2.elementText("AmountRate"));
                        break;
                    case "3":
                        altString = "Garantía especial";
                        if (altElement2.element("Description") != null)
                            altString += " | " + altElement2.elementText("Description");
                        if (altElement2.element("AmountRate") != null)
                            altString += " | Porcentaje: " + altElement2.elementText("AmountRate");
                        if (altElement2.element("LiabilityAmount") != null)
                            altString += " | Importe: " + altElement2.elementText("LiabilityAmount");
                        addionalObligationsResource.addProperty(PPROC.otherGuarantee, altString, XSDDatatype.XSDstring);
                    }
                }
            }
        }

        // TenderingTerms/MaximumAdvertisementAmount
        if ((altString = altElement.elementText("MaximumAdvertisementAmount")) != null) {
            Resource cpeResource = model.createResource(rootResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            rootResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            Resource addionalObligationsResource = model.createResource(cpeResource + "/ContractAdditionalObligations");
            addionalObligationsResource.addProperty(RDF.type, PPROC.ContractAdditionalObligations);
            cpeResource.addProperty(PPROC.contractAdditionalObligations, addionalObligationsResource);
            altString2 = "Gastos máximos de publicidad: ";
            altString2 += altString;
            altString2 += " " + altElement.element("MaximumAdvertisementAmount").attributeValue("currencyID");
            addionalObligationsResource.addProperty(PPROC.advertisementAmount, altString2, XSDDatatype.XSDstring);
        }

        // TenderingTerms/AdditionalInformationParty
        if (altElement.element("AdditionalInformationParty") != null) {
            Resource cpeResource = model.createResource(rootResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            rootResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            Resource informationProviderResource = model.createResource(cpeResource + "/AdditionalInformationProvider");
            informationProviderResource.addProperty(RDF.type, PPROC.InformationProvider);
            cpeResource.addProperty(PPROC.tenderInformationProvider, informationProviderResource);
            Resource placeResource = model.createResource(informationProviderResource + "/Place");
            placeResource.addProperty(RDF.type, S.Place);
            informationProviderResource.addProperty(S.location, placeResource);

            // TenderingTerms/AdditionalInformationParty/PartyName
            if (altElement.element("AdditionalInformationParty").element("PartyName") != null
                    && (altString = altElement.element("AdditionalInformationParty").element("PartyName")
                            .elementText("Name")) != null)
                placeResource.addProperty(S.name, altString);

            // TenderingTerms/AdditionalInformationParty/PostalAddress
            if (altElement.element("AdditionalInformationParty").element("PostalAddress") != null) {
                Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                placeResource.addProperty(S.address, postalAddressResource);
                if ((altString = altElement.element("AdditionalInformationParty").element("PostalAddress")
                        .elementText("CityName")) != null)
                    postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("AdditionalInformationParty").element("PostalAddress")
                        .elementText("PostalZone")) != null)
                    postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("AdditionalInformationParty").element("PostalAddress")
                        .element("AddressLine").elementText("Line")) != null)
                    postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("AdditionalInformationParty").element("PostalAddress")
                        .element("Country").elementText("IdentificationCode")) != null)
                    postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
            }

            // TenderingProcess/DocumentAvailabilityPeriod/PostalAddress
            if (documentAvailabilityPeriod != null)
                informationProviderResource.addProperty(PPROC.estimatedEndDate, documentAvailabilityPeriod,
                        XSDDatatype.XSDdate);
        }

        // TenderingTerms/DocumentProviderParty
        if (altElement.element("DocumentProviderParty") != null) {
            Resource cpeResource = model.createResource(rootResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            rootResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            Resource informationProviderResource = model.createResource(cpeResource + "/DocumentProviderParty");
            informationProviderResource.addProperty(RDF.type, PPROC.InformationProvider);
            cpeResource.addProperty(PPROC.tenderInformationProvider, informationProviderResource);
            Resource placeResource = model.createResource(informationProviderResource + "/Place");
            placeResource.addProperty(RDF.type, S.Place);
            informationProviderResource.addProperty(S.location, placeResource);

            // TenderingTerms/DocumentProviderParty/PartyName
            if (altElement.element("DocumentProviderParty").element("PartyName") != null
                    && (altString = altElement.element("DocumentProviderParty").element("PartyName")
                            .elementText("Name")) != null)
                placeResource.addProperty(S.name, altString);

            // TenderingTerms/DocumentProviderParty/PostalAddress
            if (altElement.element("DocumentProviderParty").element("PostalAddress") != null) {
                Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                placeResource.addProperty(S.address, postalAddressResource);
                if ((altString = altElement.element("DocumentProviderParty").element("PostalAddress")
                        .elementText("CityName")) != null)
                    postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("DocumentProviderParty").element("PostalAddress")
                        .elementText("PostalZone")) != null)
                    postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("DocumentProviderParty").element("PostalAddress")
                        .element("AddressLine").elementText("Line")) != null)
                    postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("DocumentProviderParty").element("PostalAddress")
                        .element("Country").elementText("IdentificationCode")) != null)
                    postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
            }

            // TenderingProcess/DocumentAvailabilityPeriod/PostalAddress
            if (documentAvailabilityPeriod != null)
                informationProviderResource.addProperty(PPROC.estimatedEndDate, documentAvailabilityPeriod,
                        XSDDatatype.XSDdate);
        }

        if (altElement.element("TenderPreparation") != null || altElement.element("TenderValidityPeriod") != null) {
            Resource tenderRequirementsResource = model.createResource(rootResource + "/TenderRequirements");
            tenderRequirementsResource.addProperty(RDF.type, PPROC.TenderRequirements);
            rootResource.addProperty(PPROC.tenderRequirements, tenderRequirementsResource);

            // TenderingTerms/TenderPreparation
            for (Iterator<?> iter = altElement.elementIterator("TenderPreparation"); iter.hasNext();) {
                altElement2 = (Element) iter.next();
                altString2 = "Documentos sobre " + altElement2.elementText("TenderEnvelopeID") + " : ";
                if ((altString = altElement2.elementText("Description")) != null)
                    altString2 += " | " + altString;
                if (altElement2.element("DocumentTenderRequirement") != null)
                    for (Iterator<?> iter2 = altElement2.elementIterator("DocumentTenderRequirement"); iter2.hasNext();) {
                        altElement3 = (Element) iter2.next();
                        if ((altString = altElement3.elementText("Name")) != null)
                            altString2 += " | " + altString;
                        if ((altString = altElement3.elementText("Description")) != null)
                            altString2 += " | " + altString;
                    }
                tenderRequirementsResource.addProperty(PPROC.tenderDocumentNeeds, altString2, XSDDatatype.XSDstring);
            }

            // TenderingTerms/TenderValidityPeriod
            if (altElement.element("TenderValidityPeriod") != null
                    && (altString = altElement.element("TenderValidityPeriod").elementText("DurationMeasure")) != null
                    && (altString2 = altElement.element("TenderValidityPeriod").element("DurationMeasure")
                            .attributeValue("unitCode")) != null)
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
        }

        // TenderingTerms/TenderRecipientParty
        if (altElement.element("TenderRecipientParty") != null) {
            Resource cpeResource = model.createResource(rootResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            rootResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);
            Resource placeResource = model.createResource(cpeResource + "/TenderSubmissionLocation");
            placeResource.addProperty(RDF.type, S.Place);
            cpeResource.addProperty(PPROC.tenderSubmissionLocation, placeResource);

            // TenderingTerms/TenderRecipientParty/PartyName/Name
            if ((altString = altElement.element("TenderRecipientParty").element("PartyName").elementText("Name")) != null)
                placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);

            // TenderingTerms/TenderRecipientParty/PostalAddress
            Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
            postalAddressResource.addProperty(RDF.type, S.PostalAddress);
            placeResource.addProperty(S.address, postalAddressResource);
            if ((altString = altElement.element("TenderRecipientParty").element("PostalAddress")
                    .elementText("CityName")) != null)
                postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
            if ((altString = altElement.element("TenderRecipientParty").element("PostalAddress")
                    .elementText("PostalZone")) != null)
                postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
            if ((altString = altElement.element("TenderRecipientParty").element("PostalAddress").element("AddressLine")
                    .elementText("Line")) != null)
                postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
            if ((altString = altElement.element("TenderRecipientParty").element("PostalAddress").element("Country")
                    .elementText("IdentificationCode")) != null)
                postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
        }
    }

    /**
     * Transforms an XML/CODICE document into RDF following PPROC ontology.
     * 
     * @author gesteban
     */
    public static void parseCodiceXML(Model model, Document document) throws Exception {

        Element root = document.getRootElement();
        altString = root.elementText("UUID");
        Resource contractResource = model.createResource(BASE_URI_CONTRACT + altString);
        contractResource.addProperty(RDF.type, PPROC.Contract);

        // ContractFolderID
        altString = root.elementText("ContractFolderID");
        contractResource.addProperty(DCTerms.identifier, altString, XSDDatatype.XSDstring);

        // ProcurementProject
        if ((altElement = root.element("ProcurementProject")) != null)
            parseProcurementProjectElement(model, altElement, contractResource);

        // ContractingParty
        if ((altElement = root.element("ContractingParty")) != null) {
            altString = null;
            altString2 = null;
            for (Iterator<?> iter = altElement.element("Party").elementIterator("PartyIdentification"); iter.hasNext();) {
                altElement2 = (Element) iter.next();
                if (altElement2.element("ID").attributeValue("schemeName").equals("ID_PLATAFORMA"))
                    altString = altElement2.elementText("ID");
                else if (altElement2.element("ID").attributeValue("schemeName").equals("NIF"))
                    altString2 = altElement2.elementText("ID");
            }
            Resource organizationResource = model.createResource(BASE_URI_ORGANIZATION
                    + altString.replace(" ", "").replace("\t", ""));
            organizationResource.addProperty(RDF.type, ORG.Organization);
            contractResource.addProperty(PC.contractingAuthority, organizationResource);

            // ContractingParty/Party/PartyName/Name
            if ((altString = altElement.element("Party").element("PartyName").elementText("Name")) != null) {
                organizationResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);
                organizationResource.addProperty(RDFS.label, altString, XSDDatatype.XSDstring);
                organizationResource.addProperty(SKOS.prefLabel, altString, XSDDatatype.XSDstring);
            }

            // ContractingParty/Party/PartyIdentification/ID
            if (altString2 != null) {
                organizationResource.addProperty(DCTerms.identifier, altString2, XSDDatatype.XSDstring);
                organizationResource.addProperty(SKOS.notation, altString2, XSDDatatype.XSDstring);
            }

            // ContractingParty/Party/PostalAddress
            if (altElement.element("Party").element("PostalAddress") != null) {
                Resource placeResource = model.createResource(organizationResource + "/Place");
                placeResource.addProperty(RDF.type, S.Place);
                organizationResource.addProperty(ORG.hasSite, placeResource);
                Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                placeResource.addProperty(S.address, postalAddressResource);
                if ((altString = altElement.element("Party").element("PostalAddress").elementText("CityName")) != null)
                    postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("Party").element("PostalAddress").elementText("PostalZone")) != null)
                    postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("Party").element("PostalAddress").element("AddressLine")
                        .elementText("Line")) != null)
                    postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("Party").element("PostalAddress").element("Country")
                        .elementText("IdentificationCode")) != null)
                    postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
            }

            // ContractingParty/Party/Contact
            if (altElement.element("Party").element("Contact") != null) {
                if ((altString = altElement.element("Party").element("Contact").elementText("Telephone")) != null)
                    organizationResource.addProperty(S.telephone, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("Party").element("Contact").elementText("Telefax")) != null)
                    organizationResource.addProperty(S.faxNumber, altString, XSDDatatype.XSDstring);
                if ((altString = altElement.element("Party").element("Contact").elementText("ElectronicMail")) != null)
                    organizationResource.addProperty(S.email, altString, XSDDatatype.XSDstring);
            }
        }

        // TenderingProcess
        if ((altElement = root.element("TenderingProcess")) != null) {
            Resource cpeResource = model.createResource(contractResource + "/ContractProcedureSpecifications");
            cpeResource.addProperty(RDF.type, PPROC.ContractProcedureSpecifications);
            contractResource.addProperty(PPROC.contractProcedureSpecifications, cpeResource);

            // TenderingProcess/UrgencyCode
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = altElement.elementText("UrgencyCode")) != null)
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

            // TenderingProcess/ProcedureCode
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = altElement.elementText("ProcedureCode")) != null)
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

            // TenderingProcess/ContractingSystemCode
            // TODO parseo independiente de versión (parseo del .gc)
            if ((altString = altElement.elementText("ContractingSystemCode")) != null)
                switch (altString) {
                case "1":
                    contractResource.addProperty(RDF.type, PPROC.FrameworkConclusionContract);
                    contractResource.addProperty(RDF.type, PPROC.ConclusionContract);
                    break;
                case "2":
                    contractResource.addProperty(RDF.type, PPROC.DynamicPurchasingSystemConclusionContract);
                    contractResource.addProperty(RDF.type, PPROC.ConclusionContract);
                    break;
                case "3":
                    contractResource.addProperty(RDF.type, PPROC.FrameworkDerivativeContract);
                    contractResource.addProperty(RDF.type, PPROC.DerivativeContract);
                    break;
                case "4":
                    contractResource.addProperty(RDF.type, PPROC.DynamicPurchasingSystemDerivativeContract);
                    contractResource.addProperty(RDF.type, PPROC.DerivativeContract);
                }

            // TenderingProcess/FrameworkAgreement
            if (altElement.element("FrameworkAgreement") != null && model.containsResource(PPROC.ConclusionContract)) {
                Resource frameworkResource = model.createResource(contractResource + "/FrameworkAgreement");
                frameworkResource.addProperty(RDF.type, PPROC.FrameworkAgreement);
                contractResource.addProperty(PPROC.frameworkAgreement, frameworkResource);

                // TenderingProcess/FrameworkAgreement/MaximumOperatorsQuantity
                if ((altString = altElement.element("FrameworkAgreement").elementText("MaximumOperatorsQuantity")) != null)
                    frameworkResource.addProperty(PPROC.maxNumberOfOperators, altString, XSDDatatype.XSDstring);

                // TenderingProcess/FrameworkAgreement/DurationMeasure
                if ((altString = altElement.element("FrameworkAgreement").elementText("DurationMeasure")) != null
                        && (altString2 = altElement.element("FrameworkAgreement").element("DurationMeasure")
                                .attributeValue("unitCode")) != null)
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
            }

            // TenderingProcess/TenderSubmissionDeadlinePeriod
            if (altElement.element("TenderSubmissionDeadlinePeriod") != null) {

                // TenderingProcess/TenderSubmissionDeadlinePeriod
                if (altElement.element("TenderSubmissionDeadlinePeriod").element("EndDate") != null
                        && altElement.element("TenderSubmissionDeadlinePeriod").element("EndTime") != null) {
                    altString = altElement.element("TenderSubmissionDeadlinePeriod").elementText("EndDate");
                    altString2 = altElement.element("TenderSubmissionDeadlinePeriod").elementText("EndTime");
                    if (altString.indexOf("+") != -1)
                        altString = altString.substring(0, altString.indexOf("+"));
                    cpeResource
                            .addProperty(PPROC.tenderDeadline, altString + "T" + altString2, XSDDatatype.XSDdateTime);
                }

                // TenderingProcess/TenderSubmissionDeadlinePeriod/Description
                if ((altString = altElement.element("TenderSubmissionDeadlinePeriod").elementText("Description")) != null)
                    cpeResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);
            }

            // TenderingProcess/OpenTenderEvent
            if (altElement.element("OpenTenderEvent") != null) {
                Resource contractActivitiesResource = model.createResource(cpeResource + "/ContractActivities");
                contractActivitiesResource.addProperty(RDF.type, PPROC.ContractActivities);
                cpeResource.addProperty(PPROC.contractActivities, contractActivitiesResource);

                // TenderingProcess/OpenTenderEvent
                for (Iterator<?> iter = altElement.elementIterator("OpenTenderEvent"); iter.hasNext();) {
                    altElement2 = (Element) iter.next();
                    Resource tenderMeetingResource = model.createResource(contractActivitiesResource + "/Event_"
                            + getEventID());
                    tenderMeetingResource.addProperty(RDF.type, PPROC.TenderMeeting);
                    tenderMeetingResource.addProperty(RDF.type, S.Event);
                    contractActivitiesResource.addProperty(PPROC.tenderMeeting, tenderMeetingResource);

                    // TenderingProcess/OpenTenderEvent/Description
                    if ((altString = altElement2.elementText("Description")) != null)
                        tenderMeetingResource.addProperty(DCTerms.description, altString, XSDDatatype.XSDstring);

                    // TenderingProcess/OpenTenderEvent/TypeCode
                    if ((altString = altElement2.elementText("TypeCode")) != null)
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

                    // TenderingProcess/OpenTenderEvent/OcurrenceLocation
                    if (altElement2.elementText("OcurrenceLocation") != null) {
                        Resource placeResource = model.createResource(tenderMeetingResource + "/Place");
                        placeResource.addProperty(RDF.type, S.Place);
                        tenderMeetingResource.addProperty(S.location, placeResource);

                        // TenderingProcess/OpenTenderEvent/OcurrenceLocation/Description
                        if ((altString = altElement2.elementText("Description")) != null)
                            placeResource.addProperty(S.name, altString, XSDDatatype.XSDstring);

                        // TenderingProcess/OpenTenderEvent/OcurrenceLocation/Address
                        Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                        postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                        placeResource.addProperty(S.address, postalAddressResource);
                        if ((altString = altElement2.element("Address").elementText("CityName")) != null)
                            postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                        if ((altString = altElement2.element("Address").elementText("PostalZone")) != null)
                            postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                        if ((altString = altElement2.element("Address").element("AddressLine").elementText("Line")) != null)
                            postalAddressResource.addProperty(S.streetAddress, altString, XSDDatatype.XSDstring);
                        if ((altString = altElement2.element("Address").element("Country")
                                .elementText("IdentificationCode")) != null)
                            postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
                    }

                    // TenderingProcess/OpenTenderEvent/OcurrenceDate
                    if ((altString = altElement2.elementText("OcurrenceDate")) != null
                            && (altString2 = altElement2.elementText("OcurrenceTime")) != null) {
                        if (altString.indexOf("+") != -1)
                            altString = altString.substring(0, altString.indexOf("+"));
                        tenderMeetingResource.addProperty(S.startDate, altString + "T" + altString2,
                                XSDDatatype.XSDdateTime);
                    }
                }
            }
        }

        if ((altElement = root.element("TenderingTerms")) != null) {
            // TenderingProcess/DocumentAvailabilityPeriod/PostalAddress
            altString = null;
            if (document.getRootElement().element("TenderingProcess") != null
                    && document.getRootElement().element("TenderingProcess").element("DocumentAvailabilityPeriod") != null)
                altString = document.getRootElement().element("TenderingProcess").element("DocumentAvailabilityPeriod")
                        .elementText("EndDate");
            // TenderingTerms
            parseTenderingTermsElement(model, altElement, contractResource, altString);
        }

        // TenderResult
        if (root.element("TenderResult") != null) {

            for (Iterator<?> iter = root.elementIterator("TenderResult"); iter.hasNext();) {
                altElement = (Element) iter.next();
                Resource rootResource;

                // TenderResult/AwardedTenderedProject
                if (altElement.element("AwardedTenderedProject") != null
                        && (altString = altElement.element("AwardedTenderedProject").elementText(
                                "ProcurementProjectLotID")) != null)
                    rootResource = model.createResource(contractResource + "/Lot_" + altString);
                else
                    rootResource = contractResource;

                boolean isAwarded = false;
                boolean isFormalized = false;

                // TenderResult/ResultCode
                // TODO parseo independiente de versión (parseo del .gc)
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
                        Resource procedureVoidResource = model.createResource(rootResource + "/ProcedureVoid");
                        procedureVoidResource.addProperty(RDF.type, PPROC.ContractOrProcedureExtinction);
                        procedureVoidResource.addProperty(RDF.type, PPROC.ProcedureVoid);
                        rootResource.addProperty(PPROC.contractOrProcedureExtinction, procedureVoidResource);
                        rootResource.addProperty(PPROC.procedureVoid, procedureVoidResource);
                        if ((altString = altElement.elementText("Description")) != null)
                            procedureVoidResource.addProperty(PPROC.extinctionCause, altString, XSDDatatype.XSDstring);
                        break;
                    case "4":
                        Resource procedureResignation = model.createResource(rootResource + "/ProcedureResignation");
                        procedureResignation.addProperty(RDF.type, PPROC.ContractOrProcedureExtinction);
                        procedureResignation.addProperty(RDF.type, PPROC.ProcedureResignation);
                        rootResource.addProperty(PPROC.contractOrProcedureExtinction, procedureResignation);
                        rootResource.addProperty(PPROC.procedureResignation, procedureResignation);
                        if ((altString = altElement.elementText("Description")) != null)
                            procedureResignation.addProperty(PPROC.extinctionCause, altString, XSDDatatype.XSDstring);
                        break;
                    case "5":
                        Resource procedureWaive = model.createResource(rootResource + "/ContractWaive");
                        procedureWaive.addProperty(RDF.type, PPROC.ContractOrProcedureExtinction);
                        procedureWaive.addProperty(RDF.type, PPROC.ProcedureWaive);
                        rootResource.addProperty(PPROC.contractOrProcedureExtinction, procedureWaive);
                        rootResource.addProperty(PPROC.procedureWaive, procedureWaive);
                        if ((altString = altElement.elementText("Description")) != null)
                            procedureWaive.addProperty(PPROC.extinctionCause, altString, XSDDatatype.XSDstring);
                        break;
                    }
                }

                if (isAwarded || isFormalized) {
                    Resource tenderResource = model.createResource(rootResource + "/Tender");
                    tenderResource.addProperty(RDF.type, PC.Tender);
                    rootResource.addProperty(PC.tender, tenderResource);
                    if (isAwarded)
                        tenderResource.addProperty(RDF.type, PPROC.AwardedTender);
                    if (isFormalized)
                        tenderResource.addProperty(RDF.type, PPROC.FormalizedTender);

                    // TenderResult/WinningParty
                    if ((altElement2 = altElement.element("WinningParty")) != null) {

                        // TenderResult/WinningParty/PartyIdentification/ID
                        if (altElement2.element("PartyIdentification") != null) {
                            altString = BASE_URI_ORGANIZATION
                                    + altElement2.element("PartyIdentification").elementText("ID");
                            altString = altString.replace(" ", "");
                        } else
                            altString = tenderResource + "/Supplier";
                        Resource supplierResource = model.createResource(altString);
                        supplierResource.addProperty(RDF.type, ORG.Organization);
                        tenderResource.addProperty(PC.supplier, supplierResource);

                        // TenderResult/WinningParty/PartyName/Name
                        if ((altString = altElement2.element("PartyName").elementText("Name")) != null)
                            supplierResource.addProperty(DCTerms.title, altString, XSDDatatype.XSDstring);

                        // TenderResult/WinningParty/PartyIdentification/ID
                        if ((altString = altElement2.element("PartyIdentification").elementText("ID")) != null)
                            supplierResource.addProperty(DCTerms.identifier, altString, XSDDatatype.XSDstring);

                        // TenderResult/WinningParty/PhysicalLocation/Address
                        // TenderResult/WinningParty/PostalAddress
                        // TODO copiar esta manera de poner la direccion en otras apariciones de Location o Address
                        if ((altElement2.element("PhysicalLocation") != null && (altElement3 = altElement2.element(
                                "PhysicalLocation").element("Address")) != null)
                                || (altElement3 = altElement2.element("PostalAddress")) != null) {
                            Resource placeResource = model.createResource(supplierResource + "/Place");
                            placeResource.addProperty(RDF.type, S.Place);
                            supplierResource.addProperty(ORG.hasSite, placeResource);
                            Resource postalAddressResource = model.createResource(placeResource + "/PostalAddress");
                            postalAddressResource.addProperty(RDF.type, S.PostalAddress);
                            placeResource.addProperty(S.address, postalAddressResource);
                            if ((altString = altElement3.elementText("CityName")) != null)
                                postalAddressResource.addProperty(S.addressLocality, altString, XSDDatatype.XSDstring);
                            if ((altString = altElement3.elementText("PostalZone")) != null)
                                postalAddressResource.addProperty(S.postalCode, altString, XSDDatatype.XSDstring);
                            if (altElement3.element("AddressLine") != null) {
                                altString2 = "";
                                for (Iterator<?> iter2 = altElement3.elementIterator("AddressLine"); iter2.hasNext();)
                                    altString2 += ((Element) iter2.next()).elementText("Line") + ". ";
                                postalAddressResource.addProperty(S.streetAddress, altString2, XSDDatatype.XSDstring);
                            }
                            if ((altString = altElement3.element("Country").elementText("IdentificationCode")) != null)
                                postalAddressResource.addProperty(S.addressCountry, altString, XSDDatatype.XSDstring);
                        }

                        // TenderResult/WinningParty/Contact
                        // TODO copiar esta manera de poner el contacto en otras apariciones de Contact
                        if (altElement2.element("Contact") != null) {
                            // TenderResult/WinningParty/Contact/Telephone
                            if ((altString = altElement2.element("Contact").elementText("Telephone")) != null)
                                supplierResource.addProperty(S.telephone, altString, XSDDatatype.XSDstring);
                            // TenderResult/WinningParty/Contact/Telefax
                            if ((altString = altElement2.element("Contact").elementText("Telefax")) != null)
                                supplierResource.addProperty(S.faxNumber, altString, XSDDatatype.XSDstring);
                            // TenderResult/WinningParty/Contact/ElectronicMail
                            if ((altString = altElement2.element("Contact").elementText("ElectronicMail")) != null)
                                supplierResource.addProperty(S.email, altString, XSDDatatype.XSDstring);
                        }
                    }

                    // TenderResult/AwardedTenderedProject/LegalMonetaryTotal
                    if (altElement.element("AwardedTenderedProject") != null
                            && (altElement2 = altElement.element("AwardedTenderedProject")
                                    .element("LegalMonetaryTotal")) != null) {

                        // TenderResult/AwardedTenderedProject/LegalMonetaryTotal/PayableAmount
                        if ((altString = altElement2.elementText("PayableAmount")) != null) {
                            Resource priceResource = model.createResource(tenderResource + "/OfferedPrice");
                            priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                            priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                            priceResource.addProperty(GR.hasCurrency, altElement2.element("PayableAmount")
                                    .attributeValue("currencyID"), XSDDatatype.XSDstring);
                            priceResource.addProperty(GR.valueAddedTaxIncluded, "true", XSDDatatype.XSDboolean);
                            tenderResource.addProperty(PC.offeredPrice, priceResource);
                        }

                        // TenderResult/AwardedTenderedProject/LegalMonetaryTotal/TaxExclusiveAmount
                        if ((altString = altElement2.elementText("TaxExclusiveAmount")) != null) {
                            Resource priceResource = model.createResource(tenderResource + "/OfferedPriceWithoutTaxes");
                            priceResource.addProperty(RDF.type, PPROC.BundlePriceSpecification);
                            priceResource.addProperty(GR.hasCurrencyValue, altString, XSDDatatype.XSDfloat);
                            priceResource.addProperty(GR.hasCurrency, altElement2.element("TaxExclusiveAmount")
                                    .attributeValue("currencyID"), XSDDatatype.XSDstring);
                            priceResource.addProperty(GR.valueAddedTaxIncluded, "false", XSDDatatype.XSDboolean);
                            tenderResource.addProperty(PC.offeredPrice, priceResource);
                        }
                    }
                }
            }
        }

        // LegalDocumentReference
        if (root.element("LegalDocumentReference") != null
                && (altString = root.element("LegalDocumentReference").element("Attachment")
                        .element("ExternalReference").elementText("URI")) != null) {
            Resource documentResource = model.createResource(altString);
            documentResource.addProperty(RDF.type, FOAF.Document);
            contractResource.addProperty(PPROC.legalDocumentReference, documentResource);

            // LegalDocumentReference/IssueDate
            if ((altString = root.element("LegalDocumentReference").elementText("IssueDate")) != null)
                documentResource.addProperty(DCTerms.dateSubmitted, altString);
        }

        // TechnicalDocumentReference
        if (root.element("TechnicalDocumentReference") != null
                && (altString = root.element("TechnicalDocumentReference").element("Attachment")
                        .element("ExternalReference").elementText("URI")) != null) {
            Resource documentResource = model.createResource(altString);
            documentResource.addProperty(RDF.type, FOAF.Document);
            contractResource.addProperty(PPROC.technicalDocumentReference, documentResource);

            // LegalDocumentReference/IssueDate
            if ((altString = root.element("TechnicalDocumentReference").elementText("IssueDate")) != null)
                documentResource.addProperty(DCTerms.dateSubmitted, altString);
        }

        // AdditionalDocumentReference
        if (root.element("AdditionalDocumentReference") != null
                && (altString = root.element("AdditionalDocumentReference").element("Attachment")
                        .element("ExternalReference").elementText("URI")) != null) {
            Resource documentResource = model.createResource(altString);
            documentResource.addProperty(RDF.type, FOAF.Document);
            contractResource.addProperty(PPROC.additionalDocumentReference, documentResource);

            // LegalDocumentReference/IssueDate
            if ((altString = root.element("AdditionalDocumentReference").elementText("IssueDate")) != null)
                documentResource.addProperty(DCTerms.dateSubmitted, altString);
        }

        // ContractModification
        // TODO ¿donde está la especificación de este nivel? ¿CODICE 2.02?
        if ((altElement = root.element("ContractModification")) != null) {

            // ContractModification/ContractModificationDocumentReference
            if (altElement.element("ContractModificationDocumentReference") != null
                    && (altString = altElement.element("Attachment").element("ExternalReference").elementText("URI")) != null) {
                Resource documentResource = model.createResource(altString);
                documentResource.addProperty(RDF.type, FOAF.Document);
                contractResource.addProperty(PPROC.additionalDocumentReference, documentResource);

                // ContractModification/ContractModificationDocumentReference/IssueDate
                if ((altString = altElement.elementText("IssueDate")) != null)
                    documentResource.addProperty(DCTerms.dateSubmitted, altString);
            }
        }

        // TODO notices
        // TODO IssueDate -> en anuncios (fecha de envio)
        // TODO IssueTime -> en anuncios (fecha de envio)
        // TODO Note -> en anuncios (descripción textual)

        // ProcurementProjectLot
        if (root.element("ProcurementProjectLot") == null) {
            contractResource.addProperty(RDF.type, PPROC.ContractWithoutLots);
        } else {
            contractResource.addProperty(RDF.type, PPROC.ContractWithLots);
            for (Iterator<?> iter = root.elementIterator("ProcurementProjectLot"); iter.hasNext();) {
                altElement = (Element) iter.next();
                altString = altElement.elementText("ID");
                Resource lotResource = model.createResource(contractResource + "/Lot_" + altString);
                lotResource.addProperty(RDF.type, PPROC.Contract);
                lotResource.addProperty(RDF.type, PPROC.Lot);
                contractResource.addProperty(PPROC.lot, lotResource);

                // ProcurementProjectLot/ProcurementProject
                if ((altElement2 = altElement.element("ProcurementProject")) != null)
                    parseProcurementProjectElement(model, altElement2, lotResource);

                // ProcurementProjectLot/TenderingTerms
                if (root.element("TenderingTerms") != null) {
                    // TenderingProcess/DocumentAvailabilityPeriod/PostalAddress
                    altString = null;
                    if (document.getRootElement().element("TenderingProcess") != null
                            && document.getRootElement().element("TenderingProcess")
                                    .element("DocumentAvailabilityPeriod") != null)
                        altString = document.getRootElement().element("TenderingProcess")
                                .element("DocumentAvailabilityPeriod").elementText("EndDate");
                    // ProcurementProjectLot/TenderingTerms
                    parseTenderingTermsElement(model, altElement, lotResource, altString);
                }
            }
        }

    }

    // TODO CODICE % used? PPROC % used?

    private static int eventId = 1;

    private static int getEventID() {
        return eventId++;
    }

}