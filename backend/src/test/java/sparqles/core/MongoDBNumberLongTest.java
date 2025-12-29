package sparqles.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import java.util.Iterator;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;
import sparqles.avro.performance.PResult;
import sparqles.utils.MongoDBManager;

public class MongoDBNumberLongTest {

  @ClassRule
  public static MongoDBContainer mongo =
      new MongoDBContainer("mongodb/mongodb-community-server:4.4.30-ubuntu2204");

  @Test
  public void testNumberLongDeserialization() {
    String host = mongo.getHost();
    Integer port = mongo.getFirstMappedPort();

    // 1. Insert a document with NumberLong using the Mongo driver directly
    MongoClient client = new MongoClient(host, port);
    DB db = client.getDB("testdb");
    MongoDBManager m = new MongoDBManager(client, "testdb");

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

    // 2. Try to read it back using MongoDBManager (which uses Avro)
    // Test getIterator (which uses next())
    Iterator<PResult> iter = m.getIterator(PResult.class, PResult.SCHEMA$);

    assertTrue("Should have at least one result", iter.hasNext());

    try {
      PResult result = iter.next();
      assertNotNull(result);
      assertEquals(
          "http://example.org/sparql", result.getEndpointResult().getEndpoint().getUri().toString());
      
      // Check if the long value is correctly retrieved
      // Avro uses Utf8 for map keys
      sparqles.avro.performance.PSingleResult pSingle = result.getResults().get(new org.apache.avro.util.Utf8("ASKPO"));
      assertNotNull("PSingleResult for ASKPO should not be null", pSingle);
      
      long frestout = pSingle.getCold().getFrestout();
      assertEquals(60000L, frestout);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("Deserialization failed in iterator: " + e.getMessage());
    }

    // 3. Test getResults (which uses scan())
    try {
        List<PResult> list = m.getResults(null, PResult.class, PResult.SCHEMA$);
        assertTrue("Should have at least one result in list", list.size() > 0);
        PResult result = list.get(0);
        
        sparqles.avro.performance.PSingleResult pSingle = result.getResults().get(new org.apache.avro.util.Utf8("ASKPO"));
        assertNotNull("PSingleResult for ASKPO should not be null in list", pSingle);
        
        long frestout = pSingle.getCold().getFrestout();
        assertEquals(60000L, frestout);

    } catch (Exception e) {
        e.printStackTrace();
        fail("Deserialization failed in getResults: " + e.getMessage());
    }
  }
}
