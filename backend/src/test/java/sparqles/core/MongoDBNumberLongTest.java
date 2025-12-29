package sparqles.core;

import static org.junit.Assert.*;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;
import sparqles.avro.performance.PResult;
import sparqles.utils.MongoDBManager;

public class MongoDBNumberLongTest {

  @ClassRule
  public static MongoDBContainer mongoDBContainer =
      new MongoDBContainer("mongodb/mongodb-community-server:4.4.30-ubuntu2204");

  protected MongoDBManager m;

  @Before
  public void setUp() throws Exception {
    Properties props = new Properties();
    props.load(new FileInputStream(new File("src/test/resources/sparqles.properties")));
    props.setProperty("db.host", mongoDBContainer.getHost());
    props.setProperty("db.port", mongoDBContainer.getMappedPort(27017).toString());
    SPARQLESProperties.init(props);
    m = new MongoDBManager();
  }

  @After
  public void tearDown() throws Exception {
    m.close();
  }

  @Test
  public void testNumberLongDeserialization() {
    // 1. Insert a document with NumberLong directly using MongoClient
    MongoClient client =
        new MongoClient(mongoDBContainer.getHost(), mongoDBContainer.getMappedPort(27017));
    DB db = client.getDB(SPARQLESProperties.getDB_NAME());
    DBCollection coll = db.getCollection(MongoDBManager.COLL_PERF);

    // Construct the document structure matching PResult
    // endpointResult
    BasicDBObject endpoint = new BasicDBObject();
    endpoint.put("uri", "http://example.org/sparql");
    endpoint.put("datasets", new java.util.ArrayList());

    BasicDBObject endpointResult = new BasicDBObject();
    endpointResult.put("endpoint", endpoint);
    endpointResult.put("start", 1728593798782L);
    endpointResult.put("end", 1728593812516L);

    // Run object with long values
    BasicDBObject run = new BasicDBObject();
    run.put("frestout", 60000L); // This will be NumberLong in Mongo
    run.put("solutions", 0);
    run.put("inittime", 0L);
    run.put("exectime", 0L);
    run.put("closetime", 0L);
    run.put("Exception", null);
    run.put("exectout", 900000L);

    // PSingleResult
    BasicDBObject pSingleResult = new BasicDBObject();
    pSingleResult.put("query", "ASK WHERE { ?s ?p ?o }");
    pSingleResult.put("cold", run);
    pSingleResult.put("warm", run);

    // results map
    BasicDBObject results = new BasicDBObject();
    results.put("ASKPO", pSingleResult);

    // PResult
    BasicDBObject pResultDoc = new BasicDBObject();
    pResultDoc.put("endpointResult", endpointResult);
    pResultDoc.put("results", results);

    coll.insert(pResultDoc);

    client.close();

    // 2. Try to read it back using MongoDBManager (which uses Avro)
    Iterator<PResult> iter = m.getIterator(PResult.class, PResult.SCHEMA$);

    assertTrue("Should have at least one result", iter.hasNext());

    try {
      PResult result = iter.next();
      assertNotNull(result);
      assertEquals(
          "http://example.org/sparql", result.getEndpointResult().getEndpoint().getUri().toString());
      
      // Check if the long value is correctly retrieved
      long frestout = result.getResults().get("ASKPO").getCold().getFrestout();
      assertEquals(60000L, frestout);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Deserialization failed: " + e.getMessage());
    }
  }
}
