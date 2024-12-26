package sparqles.core;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ARQRequestTEST {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    @Ignore("requires a server, not a unit test")
    public void test() {
        //        HttpOp.setUserAgent(CONSTANTS.USER_AGENT);
        try (QueryExecution ex =
                QueryExecution.service(
                        "http://localhost:8000/sparql", "SELECT * WHERE { ?s ?p ?o . }")) {
            ResultSet res;
            res = ex.execSelect();
            while (res.hasNext()) {
                System.out.println("nextS");
            }
        }
    }
}
