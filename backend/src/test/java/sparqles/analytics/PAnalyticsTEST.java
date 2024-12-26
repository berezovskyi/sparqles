package sparqles.analytics;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sparqles.avro.Endpoint;
import sparqles.avro.performance.PResult;
import sparqles.core.EndpointFactory;
import sparqles.core.SPARQLESProperties;
import sparqles.utils.MongoDBManager;

public class PAnalyticsTEST {

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
    public void test() throws URISyntaxException {
        m.initAggregateCollections();
        PAnalyser a = new PAnalyser(m);

        Endpoint ep = EndpointFactory.newEndpoint("http://dbpedia.org/sparql");
        System.out.println("Analyse");

        Collection<PResult> ress = m.getResults(ep, PResult.class, PResult.SCHEMA$);
        for (PResult pr : ress) {
            a.analyse(pr);
        }

        //		a.analyse(ep);

    }
}
