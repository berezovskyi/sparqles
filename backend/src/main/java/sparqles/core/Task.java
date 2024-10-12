package sparqles.core;

import org.apache.avro.specific.SpecificRecordBase;
import sparqles.utils.MongoDBManager;

import java.util.concurrent.Callable;

/**
 * A Task is a {@link Callable} connected to the database.
 *
 * @param <V> - return type restricted to AVRO objects
 * @author umbrichj
 */
public interface Task<V extends SpecificRecordBase> extends Callable<V> {

//	public void execute();
    
    /**
     * Set the MongoDBManager.
     *
     * @param dbm
     */
    void setDBManager(MongoDBManager dbm);
}
