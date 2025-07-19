package sparqles.utils;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.core.SPARQLESProperties;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import sparqles.avro.Endpoint;


public class OxigraphManager implements DbManager {

    private static final Logger log = LoggerFactory.getLogger(OxigraphManager.class);

    private final String sparqlEndpoint;

    public OxigraphManager() {
        this.sparqlEndpoint = "http://" + SPARQLESProperties.getDB_HOST() + ":7878/sparql";
    }

    public ResultSet query(String queryString) {
        log.debug("Executing query: {}", queryString);
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(sparqlEndpoint).query(query).build()) {
            return qexec.execSelect();
        } catch (Exception e) {
            log.error("Error executing query: {}", e.getMessage());
            return null;
        }
    }

    public boolean update(String updateString) {
        log.debug("Executing update: {}", updateString);
        UpdateRequest updateRequest = UpdateFactory.create(updateString);
        try {
            UpdateProcessor updateProcessor = UpdateExecutionFactory.createRemote(updateRequest, sparqlEndpoint);
            updateProcessor.execute();
            return true;
        } catch (Exception e) {
            log.error("Error executing update: {}", e.getMessage());
            return false;
        }
    }

    public boolean load(Model model, String graphUri) {
        try {
            // A more efficient way to load data is needed here.
            // For now, we serialize the model and insert it with a SPARQL query.
            java.io.StringWriter sw = new java.io.StringWriter();
            model.write(sw, "TURTLE");
            String data = sw.toString();

            String query = "INSERT DATA { GRAPH <" + graphUri + "> {\n" + data + "\n}}";
            return update(query);
        } catch (Exception e) {
            log.error("Error loading data: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isRunning() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initEndpointCollection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initScheduleCollection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initAggregateCollections() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <V extends SpecificRecordBase> boolean insert(Collection<V> results) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <V extends SpecificRecordBase> boolean insert(V res) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <V extends SpecificRecordBase> boolean update(V res) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <V extends SpecificRecordBase> List<V> get(Class<V> cls, Schema schema) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <V extends SpecificRecordBase> Iterator<V> getIterator(Class<V> cls, Schema schema) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Endpoint getEndpoint(Endpoint ep) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> List<T> getResults(Endpoint ep, Class<T> cls, Schema schema) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> Iterator<T> getResultsIterator(Endpoint ep, Class<T> cls, Schema schema) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends SpecificRecordBase> List<T> getResultsSince(Endpoint ep, Class<T> cls, Schema schema, long since) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T extends SpecificRecordBase> List<T> getResultsSince(Endpoint ep, Class<T> cls, Schema schema, long from, long to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean cleanup(Endpoint ep) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(Endpoint ep, Class cls) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getFirstAvailabitlityTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
