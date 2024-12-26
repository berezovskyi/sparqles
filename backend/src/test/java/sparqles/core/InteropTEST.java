package sparqles.core;

import static org.junit.Assert.*;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sparqles.avro.Endpoint;
import sparqles.avro.features.FResult;
import sparqles.utils.MongoDBManager;

public class InteropTEST {

    private MongoDBManager m;

    @Before
    public void setUp() throws Exception {
        SPARQLESProperties.init(new File("src/test/resources/ends.properties"));
        m = new MongoDBManager();
    }

    @After
    public void tearDown() throws Exception {
        m.close();
    }

    @Test
    public void testSingle() throws Exception {
        Endpoint ep = Endpoints.DBPEDIA;

        Task<FResult> t = TaskFactory.create(CONSTANTS.FTASK, ep, m, null);
        FResult res = t.call();
        System.out.println(res);
        m.insert(res);
    }

    //	@Test
    //	public void testGroup() throws Exception {
    //
    //		Endpoint [] eps = {Endpoints.DBPEDIA,Endpoints.AEMET};
    //		for(Endpoint ep: eps){
    //		Task<PResult> t = TaskFactory.create(CONSTANTS.PTASK, ep, m, null);
    //		PResult res = t.call();
    //		System.out.println(res);
    //		m.insert(res);
    //		}
    //
    //	}
    //
    //
    //	@Test
    //	public void testIterator() {
    //
    //		Endpoint ep = Endpoints.DBPEDIA;
    //
    //		m.initEndpointCollection();
    //		m.initAggregateCollections();
    //		m.insert(ep);
    //
    //		Schedule sc = new Schedule();
    //		sc.setEndpoint(ep);
    //		sc.setPTask("0 0/2 * 1/1 * ? *");
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
