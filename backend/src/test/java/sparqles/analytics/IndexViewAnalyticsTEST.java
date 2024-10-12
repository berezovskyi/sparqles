package sparqles.analytics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sparqles.core.SPARQLESProperties;
import sparqles.utils.MongoDBManager;

import java.io.File;

public class IndexViewAnalyticsTEST {
    
    private MongoDBManager m;
    
    @Before
    public void setUp() throws Exception {
        SPARQLESProperties.init(new File("src/test/resources/sparqles.properties"));
        m = new MongoDBManager();
    }
    
    @After
    public void tearDown() throws Exception {
        m.close();
    }
    
    @Test
    public void test() throws Exception {
        
        
        IndexViewAnalytics a = new IndexViewAnalytics();
        a.setDBManager(m);
        a.call();
    }
    
}
