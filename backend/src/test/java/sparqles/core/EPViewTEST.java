package sparqles.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sparqles.avro.Endpoint;
import sparqles.utils.MongoDBManager;

import java.io.File;

public class EPViewTEST {
    
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
    public void test() {
        
        Endpoint ep = Endpoints.DBPEDIA;
        
        
    }
}
