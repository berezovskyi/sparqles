package sparqles.core;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sparqles.avro.availability.AResult;
import sparqles.core.availability.ATask;
import sparqles.utils.MongoDBManager;

public class AvailabilityTEST {

  private MongoDBManager m;

  @Before
  public void setUp() throws Exception {
    SPARQLESProperties.init(new File("src/main/resources/sparqles.properties"));
    //		m = new MongoDBManager();

  }

  @After
  public void tearDown() throws Exception {
    m.close();
  }

  @Test
  public void testSingle() throws Exception {

    ATask a = new ATask(Endpoints.DBPEDIA);
    // a.setDBManager(m);

    AResult ar = a.call();

    //		m.insert(ar);
  }

  //	@Test
  //	public void test() {
  //
  //		Endpoint ep = Endpoints.DBPEDIA;
  //
  //		m.initEndpointCollection();
  //		m.initAggregateCollections();
  //		m.insert(ep);
  //
  //		Schedule sc = new Schedule();
  //		sc.setEndpoint(ep);
  //		sc.setATask("0 0/2 * 1/1 * ? *");
  //		m.insert(sc);
  //
  //		Scheduler s = new Scheduler();
  //
  //		s.useDB(m);
  //		s.init(m);
  //
  //		try {
  //			Thread.sleep(60*60*1000);
  //		} catch (InterruptedException e) {
  //			// TODO Auto-generated catch block
  //			e.printStackTrace();
  //		}
  //
  //		s.close();
  //		m.close();
  //
  //	}
}
