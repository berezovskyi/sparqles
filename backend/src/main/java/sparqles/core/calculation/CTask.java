package sparqles.core.calculation;

import static java.time.temporal.ChronoUnit.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.avro.EndpointResult;
import sparqles.avro.calculation.CResult;
import sparqles.core.EndpointTask;
import sparqles.utils.QueryManager;

public class CTask extends EndpointTask<CResult> {
  private static final Logger log = LoggerFactory.getLogger(CTask.class);

  private static final String sparqDescNS = "http://www.w3.org/ns/sparql-service-description#";
  private static final String voidNS = "http://rdfs.org/ns/void#";
  private static final String dctermsNS = "http://purl.org/dc/terms/";
  private static final String foafNS = "http://xmlns.com/foaf/0.1/";

  private static final String queryPingEndpoint = "" + "ASK { ?s ?p ?o }";

  private static final String queryNumberOfTriples =
      "" + "SELECT (COUNT (*) as ?value)\n" + "WHERE { ?s ?p ?o }";

  private static final String queryNumberOfEntities =
      ""
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
          + "SELECT (COUNT (DISTINCT ?entity) as ?value)\n"
          + "WHERE { ?entity rdf:type ?class }";

  private static final String queryNumberOfClasses =
      ""
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
          + "SELECT (COUNT (DISTINCT ?class) as ?value)\n"
          + "WHERE { ?entity rdf:type ?class }";

  private static final String queryNumberOfProperties =
      "" + "SELECT (COUNT (DISTINCT ?p) as ?value)\n" + "WHERE { ?s ?p ?o }";

  private static final String queryNumberOfSubjects =
      "" + "SELECT (COUNT (DISTINCT ?s) as ?value)\n" + "WHERE { ?s ?p ?o }";

  private static final String queryNumberOfObjects =
      "" + "SELECT (COUNT (DISTINCT ?o) as ?value)\n" + "WHERE { ?s ?p ?o }";

  private static final String queryExampleResource =
      ""
          + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
          + "SELECT ?value\n"
          + "WHERE { ?value rdf:type ?class }\n"
          + "LIMIT 3";

  public CTask(Endpoint ep) {
    super(ep);
  }

  @Override
  public CResult process(EndpointResult epr) {
    CResult result = new CResult();
    result.setEndpointResult(epr);
    // if (!_epURI.equals("http://en.openei.org/sparql"))
    //    return result;
    log.info("### execute {}", _epURI);

    // Code for generating a VoID and SPARQL Service Description profile for the endpoint.
    // author: Milos Jovanovik (@mjovanovik)

    long triples = -1;
    long entities = -1;
    long classes = -1;
    long properties = -1;
    long distinctSubjects = -1;
    long distinctObjects = -1;
    java.util.List<java.lang.CharSequence> exampleResourceList = new java.util.ArrayList<>();
    String VoID = "";
    boolean VoIDPart = false;
    String SD = "";
    boolean SDPart = false;
    double coherence = -1.0;
    double relationshipSpecialty = -1.0;

    // Check if the endpoint is accessible or not.
    // If not, there's no need to try and generate a VoID profile for it.
    Boolean ping = false;
    //        Query query1 = QueryFactory.create(queryPingEndpoint);
    try {
      var qexec1 = QueryManager.getExecution(_epURI, queryPingEndpoint);
      ping = qexec1.execAsk();
    } catch (Exception e) {
      log.info("[Error executing SPARQL query for {}]", _epURI);
      log.info("[Error details: {}]", e.toString());
    }

    // If the endpoint is accessible, try to gather VoID statistics and generate the profile.
    if (ping) {
      log.info("[GENERATION of VoiD] {}", _epURI);
      triples = executeLongQuery(_epURI, queryNumberOfTriples);
      if (triples == -1) {
        VoIDPart = true;
        SDPart = true;
      }
      log.info("Number of triples in {}: {}", _epURI, triples);
      // if (!_epURI.equals("http://sparql.uniprot.org")) // TODO: fix this hack
      entities = executeLongQuery(_epURI, queryNumberOfEntities);
      if (entities == -1) VoIDPart = true;
      log.info("Number of entities in {}: {}", _epURI, entities);
      classes = executeLongQuery(_epURI, queryNumberOfClasses);
      if (classes == -1) VoIDPart = true;
      log.info("Number of classes in {}: {}", _epURI, classes);
      properties = executeLongQuery(_epURI, queryNumberOfProperties);
      if (properties == -1) VoIDPart = true;
      log.info("Number of properties in {}: {}", _epURI, properties);
      // if (!_epURI.equals("http://fr.dbpedia.org/sparql") &&
      // !_epURI.equals("http://sparql.uniprot.org")) // TODO: fix this hack
      distinctSubjects = executeLongQuery(_epURI, queryNumberOfSubjects);
      if (distinctSubjects == -1) VoIDPart = true;
      log.info("Number of distinct subjects in {}: {}", _epURI, distinctSubjects);
      // if (!_epURI.equals("http://fr.dbpedia.org/sparql") &&
      // !_epURI.equals("http://sparql.uniprot.org")) // TODO: fix this hack
      distinctObjects = executeLongQuery(_epURI, queryNumberOfObjects);
      if (distinctObjects == -1) VoIDPart = true;
      log.info("Number of distinct objects in {}: {}", _epURI, distinctObjects);
      exampleResourceList = executeQuery(_epURI, queryExampleResource);
      if (exampleResourceList.size() == 0) VoIDPart = true;
      log.info("Number of example resources in {}: {}", _epURI, exampleResourceList.size());

      try {
        log.info("Coherence calculation for {} ...", _epURI);
        coherence = calculateCoherence(_epURI);
      } catch (Exception e) {
        log.warn("[Error details: {}]", e.toString());
      }
      try {
        log.info("Relationship Specialty calculation {} ...", _epURI);
        if (triples != -1 && distinctSubjects != -1)
          relationshipSpecialty = calculateRelationshipSpecialty(_epURI, triples, distinctSubjects);
      } catch (Exception e) {
        log.warn("[Error details: {}]", e.toString());
      }

      // Separate model for the SPARQL Service Description
      Model modelSD = ModelFactory.createDefaultModel();

      // Separate model for the VoID Profile
      Model modelVoID = ModelFactory.createDefaultModel();

      // Resources for the SPARQL Service Description
      Resource endpointEntitySD = modelSD.createResource(_epURI);
      Resource sdService = modelSD.createResource(sparqDescNS + "Service");
      Resource sdDataset = modelSD.createResource(sparqDescNS + "Dataset");
      Resource sdGraph = modelSD.createResource(sparqDescNS + "Graph");

      // Resources for the VoID Profile
      Resource endpointEntityVoiD = modelVoID.createResource(_epURI);
      Resource endpointEntityVoiDDescription = modelVoID.createResource(_epURI + "/profile");
      Resource voidDatasetDescription = modelVoID.createResource(voidNS + "DatasetDescription");
      Resource voidDataset = modelVoID.createResource(voidNS + "Dataset");
      Resource sparqlesEntity =
          modelVoID.createResource(
              "https://sparqles.demo.openlinksw.com"); // TODO: This is hardcoded for
      // now, needs to
      // be dynamic

      // Properties for the SPARQL Service Description
      Property sdendpoint = modelSD.createProperty(sparqDescNS + "endpoint");
      Property sddefaultDataset = modelSD.createProperty(sparqDescNS + "defaultDataset");
      Property sddefaultGraph = modelSD.createProperty(sparqDescNS + "defaultGraph");
      Property voidtriplesSD = modelVoID.createProperty(voidNS + "triples");

      // Properties for the VoID Profile
      Property dctermsTitle = modelVoID.createProperty(dctermsNS + "title");
      Property dctermsCreator = modelVoID.createProperty(dctermsNS + "creator");
      Property dctermsDate = modelVoID.createProperty(dctermsNS + "date");
      Property foafprimaryTopic = modelVoID.createProperty(foafNS + "primaryTopic");
      Property voidtriples = modelVoID.createProperty(voidNS + "triples");
      Property voidentities = modelVoID.createProperty(voidNS + "entities");
      Property voidclasses = modelVoID.createProperty(voidNS + "classes");
      Property voidproperties = modelVoID.createProperty(voidNS + "properties");
      Property voiddistinctSubjects = modelVoID.createProperty(voidNS + "distinctSubjects");
      Property voiddistinctObjects = modelVoID.createProperty(voidNS + "distinctObjects");
      Property voidsparqlEndpoint = modelVoID.createProperty(voidNS + "sparqlEndpoint");
      Property voidexampleResource = modelVoID.createProperty(voidNS + "exampleResource");
      Property coherenceValue =
          modelVoID.createProperty("https://www.3dfed.com/ontology/coherence");
      Property relationshipSpecialtyValue =
          modelVoID.createProperty("https://www.3dfed.com/ontology/relationshipSpecialty");

      // get current date
      LocalDate currentDate = LocalDate.now();
      String currentDateString = currentDate.format(DateTimeFormatter.ISO_DATE);
      // Literal currentDateLiteral = model.createTypedLiteral(currentDateString, XSD.date);
      Literal currentDateLiteral = modelVoID.createLiteral(currentDateString);

      // construct the SPARQL Service Description in RDF
      endpointEntitySD.addProperty(RDF.type, sdService);
      endpointEntitySD.addProperty(sdendpoint, endpointEntitySD);
      endpointEntitySD.addProperty(
          sddefaultDataset,
          modelSD
              .createResource()
              .addProperty(RDF.type, sdDataset)
              .addProperty(
                  sddefaultGraph,
                  modelSD
                      .createResource()
                      .addProperty(RDF.type, sdGraph)
                      .addProperty(voidtriplesSD, Long.toString(triples))));

      // construct the VoID Profile in RDF
      endpointEntityVoiDDescription.addProperty(RDF.type, voidDatasetDescription);
      endpointEntityVoiDDescription.addProperty(
          dctermsTitle, "Automatically constructed VoID description for a SPARQL Endpoint");
      endpointEntityVoiDDescription.addProperty(dctermsCreator, sparqlesEntity);
      endpointEntityVoiDDescription.addProperty(dctermsDate, currentDateLiteral);
      endpointEntityVoiDDescription.addProperty(foafprimaryTopic, endpointEntityVoiD);

      endpointEntityVoiD.addProperty(RDF.type, voidDataset);
      endpointEntityVoiD.addProperty(voidsparqlEndpoint, endpointEntityVoiD);
      for (int i = 0; i < exampleResourceList.size(); i++)
        endpointEntityVoiD.addProperty(
            voidexampleResource, modelVoID.createResource(exampleResourceList.get(i).toString()));
      endpointEntityVoiD.addProperty(voidtriples, Long.toString(triples));
      endpointEntityVoiD.addProperty(voidentities, Long.toString(entities));
      endpointEntityVoiD.addProperty(voidclasses, Long.toString(classes));
      endpointEntityVoiD.addProperty(voidproperties, Long.toString(properties));
      endpointEntityVoiD.addProperty(voiddistinctSubjects, Long.toString(distinctSubjects));
      endpointEntityVoiD.addProperty(voiddistinctObjects, Long.toString(distinctObjects));

      // add the Coherence and Relationship Specialty values for the endpoint
      endpointEntityVoiD.addProperty(coherenceValue, Double.toString(coherence));
      endpointEntityVoiD.addProperty(
          relationshipSpecialtyValue, Double.toString(relationshipSpecialty));

      // the SD and VoID profiles have been generated, now we persist it
      java.io.StringWriter stringModelVoID = new java.io.StringWriter();
      modelVoID.write(stringModelVoID, "TURTLE");
      VoID = stringModelVoID.toString();

      java.io.StringWriter stringModelSD = new java.io.StringWriter();
      modelSD.write(stringModelSD, "TURTLE");
      SD = stringModelSD.toString();
    }

    result.setTriples(triples);
    result.setEntities(entities);
    result.setClasses(classes);
    result.setProperties(properties);
    result.setDistinctSubjects(distinctSubjects);
    result.setDistinctObjects(distinctObjects);
    result.setExampleResources(exampleResourceList);
    result.setVoID(VoID);
    result.setVoIDPart(VoIDPart);
    result.setSD(SD);
    result.setSDPart(SDPart);
    result.setCoherence(coherence);
    result.setRS(relationshipSpecialty);

    log.info("$$$ executed {}", this);

    return result;
  }

  public long executeLongQuery(String endpointURL, String queryText) {
    long result = -1;
    try {
      ResultSet results;
      try (QueryExecution qexec =
          QueryManager.getExecution(endpointURL, queryText, Duration.of(10, MINUTES))) {
        results = qexec.execSelect();
        if (results.hasNext()) {
          QuerySolution thisRow = results.next();
          result = ((Literal) thisRow.get("value")).getLong();
        }
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpointURL);
      log.warn("[SPARQL query: {}]", queryText);
      log.warn("[Error details: {}]", e.toString());
    }
    return result;
  }

  public java.util.List<java.lang.CharSequence> executeQuery(String endpointURL, String queryText) {
    java.util.List<java.lang.CharSequence> list = new java.util.ArrayList<>();
    try {
      //            Query query = QueryFactory.create(queryText);
      ResultSet results;
      try (QueryExecution qexec = QueryManager.getExecution(endpointURL, queryText)) {
        //            qexec.setTimeout(10, TimeUnit.MINUTES);
        results = qexec.execSelect();
        if (results != null) {
          while (results.hasNext()) {
            QuerySolution thisRow = results.next();
            list.add(((Resource) thisRow.get("value")).toString());
          }
        }
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpointURL);
      log.warn("[SPARQL query: {}]", queryText);
      log.warn("[Error details: {}]", e.toString());
    }
    return list;
  }

  public double calculateCoherence(String endpointUrl) {
    Set<String> types = getRDFTypes(endpointUrl);
    int typesSize = types.size();
    log.info("Number of types in {}: {}", endpointUrl, typesSize);
    // if(types.size()==0) return 0; // the SPARQL query has failed, so we cannot calculate the
    // coherence
    double weightedDenomSum = getTypesWeightedDenomSum(types, endpointUrl);
    log.info("Weighted denom sum in {}: {}", endpointUrl, weightedDenomSum);
    // if(weightedDenomSum==0) return 0; // the SPARQL query has failed, so we cannot calculate
    // the
    // coherence
    double structuredness = 0;
    int i = 1;
    for (String type : types) {
      log.info("Processing type {}/{} in coherence of {}", i, typesSize, endpointUrl);
      long occurenceSum = 0;
      Set<String> typePredicates = getTypePredicates(type, endpointUrl);
      long typeInstancesSize = getTypeInstancesSize(type, endpointUrl);
      // if(typeInstancesSize==0) return 0; // the SPARQL query has failed, so we cannot
      // calculate
      // the coherence
      for (String predicate : typePredicates) {
        long predicateOccurences = getOccurences(predicate, type, endpointUrl);
        occurenceSum = (occurenceSum + predicateOccurences);
      }
      double denom = typePredicates.size() * typeInstancesSize;
      if (typePredicates.size() == 0) denom = 1;
      double coverage = occurenceSum / denom;
      double weightedCoverage = (typePredicates.size() + typeInstancesSize) / weightedDenomSum;
      structuredness = (structuredness + (coverage * weightedCoverage));
      i++;
    }
    return structuredness;
  }

  public static Set<String> getRDFTypes(String endpoint) {
    Set<String> types = new HashSet<String>();
    String queryString = "" + "SELECT DISTINCT ?type\n" + "WHERE { ?s a ?type }";
    try {
      //            Query query = QueryFactory.create(queryString);
      ResultSet res;
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //            qExec.setTimeout(10, TimeUnit.MINUTES);
        res = qExec.execSelect();
        while (res.hasNext()) {
          types.add(res.next().get("type").toString());
        }
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return types;
  }

  public static double getTypesWeightedDenomSum(Set<String> types, String endpoint) {
    double sum = 0;
    int typesSize = types.size();
    int i = 1;
    for (String type : types) {
      log.info("Processing type {}/{} in coherence of {}", i, typesSize, endpoint);
      long typeInstancesSize = getTypeInstancesSize(type, endpoint);
      long typePredicatesSize = getTypePredicates(type, endpoint).size();
      sum = sum + typeInstancesSize + typePredicatesSize;
      i++;
    }
    return sum;
  }

  public static long getTypeInstancesSize(String type, String endpoint) {
    long typeInstancesSize = 0;
    String queryString =
        ""
            + "SELECT (COUNT (DISTINCT ?s) as ?cnt ) \n"
            + "WHERE {\n"
            + "   ?s a <"
            + type.replaceAll("\\s", "")
            + "> . "
            + "   ?s ?p ?o"
            + "}";
    try {
      //        Query query = QueryFactory.create(queryString);
      ResultSet res;
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //                qExec.setTimeout(10, TimeUnit.MINUTES);

        res = qExec.execSelect();
        while (res.hasNext()) {
          typeInstancesSize = Long.parseLong(res.next().get("cnt").asLiteral().getString());
        }
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return typeInstancesSize;
  }

  public static Set<String> getTypePredicates(String type, String endpoint) {
    Set<String> typePredicates = new HashSet<String>();
    String queryString =
        ""
            + "SELECT DISTINCT ?typePred \n"
            + "WHERE { \n"
            + "   ?s a <"
            + type.replaceAll("\\s", "")
            + "> . "
            + "   ?s ?typePred ?o"
            + "}";
    try {
      //            Query query = QueryFactory.create(queryString);
      ResultSet res;
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //                qExec.setTimeout(10, TimeUnit.MINUTES);

        res = qExec.execSelect();
        while (res.hasNext()) {
          String predicate = res.next().get("typePred").toString();
          if (!predicate.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
            typePredicates.add(predicate);
        }
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return typePredicates;
  }

  public static long getOccurences(String predicate, String type, String endpoint) {
    long predicateOccurences = 0;
    String queryString =
        ""
            + "SELECT (COUNT (DISTINCT ?s) as ?occurences) \n"
            + "WHERE { \n"
            + "   ?s a <"
            + type.replaceAll("\\s", "")
            + "> . "
            + "   ?s <"
            + predicate
            + "> ?o"
            + "}";
    try {
      //            Query query = QueryFactory.create(queryString);
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //                qExec.setTimeout(10, TimeUnit.MINUTES);

        ResultSet res = qExec.execSelect();
        while (res.hasNext())
          predicateOccurences =
              Long.parseLong(res.next().get("occurences").asLiteral().getString());
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return predicateOccurences;
  }

  public double calculateRelationshipSpecialty(
      String endpoint, long numOfTriples, long numOfSubjects) {
    Set<String> predicates = getRelationshipPredicates(endpoint);
    int predicatesSize = predicates.size();
    log.info("Number of predicates in {}: {}", endpoint, predicatesSize);
    long datasetSize = numOfTriples;
    long subjects = numOfSubjects;
    Kurtosis kurt = new Kurtosis();
    double relationshipSpecialty = 0;
    int i = 1;
    for (String predicate : predicates) {
      log.info("Processing predicate {}/{} in RS of {}", i, predicatesSize, endpoint);
      double[] occurences = getOccurences(predicate, endpoint, subjects);
      double kurtosis = kurt.evaluate(occurences);
      // long tpSize = getPredicateSize(predicate, endpoint, namedGraph);
      long tpSize = getPredicateSize(predicate, endpoint);
      relationshipSpecialty = relationshipSpecialty + (tpSize * kurtosis / datasetSize);
      i++;
    }
    return relationshipSpecialty;
  }

  public static Set<String> getRelationshipPredicates(String endpoint) {
    Set<String> predicates = new HashSet<String>();
    String queryString;
    queryString = "SELECT DISTINCT ?p WHERE {?s ?p ?o . FILTER isIRI(?o) } ";
    try {
      //            Query query = QueryFactory.create(queryString);
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //                qExec.setTimeout(10, TimeUnit.MINUTES);
        ResultSet res = qExec.execSelect();
        while (res.hasNext()) predicates.add(res.next().get("p").toString());
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return predicates;
  }

  public static double[] getOccurences(String predicate, String endpoint, long subjects) {
    double[] occurences = new double[(int) subjects + 1];
    String queryString;
    queryString =
        "SELECT (count(?o) as ?occ) WHERE { ?res <" + predicate + "> ?o . } Group by ?res";
    try {
      //            Query query = QueryFactory.create(queryString);
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //                qExec.setTimeout(10, TimeUnit.MINUTES);
        ResultSet res = qExec.execSelect();
        int i = 0;
        while (res.hasNext()) {
          occurences[i] = res.next().get("occ").asLiteral().getDouble();
          i++;
        }
        if (i == 0) occurences[0] = 1;
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return occurences;
  }

  public static long getPredicateSize(String predicate, String endpoint) {
    long count = 0;
    String queryString = "";
    queryString =
        ""
            + "SELECT (COUNT (*) as ?total) \n"
            + "WHERE { \n"
            + "   ?s <"
            + predicate
            + "> ?o"
            + "}";
    try {
      //            Query query = QueryFactory.create(queryString);
      try (QueryExecution qExec = QueryManager.getExecution(endpoint, queryString)) {
        //                qExec.setTimeout(10, TimeUnit.MINUTES);
        ResultSet res = qExec.execSelect();
        while (res.hasNext())
          count = Long.parseLong(res.next().get("total").asLiteral().getString());
      }
    } catch (Exception e) {
      log.warn("[Error executing SPARQL query for {}]", endpoint);
      log.warn("[SPARQL query: {}]", queryString);
      throw new IllegalStateException(e);
    }
    return count;
  }
}
