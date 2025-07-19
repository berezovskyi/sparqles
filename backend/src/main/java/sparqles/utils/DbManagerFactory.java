package sparqles.utils;

import sparqles.core.SPARQLESProperties;

public class DbManagerFactory {

    public static DbManager createDbManager() {
        String dbEngine = SPARQLESProperties.getDB_engine();
        if ("mongodb".equalsIgnoreCase(dbEngine)) {
            return new MongoDBManager();
        } else if ("oxigraph".equalsIgnoreCase(dbEngine)) {
            return new OxigraphManager();
        } else {
            throw new IllegalArgumentException("Unsupported database engine: " + dbEngine);
        }
    }
}
