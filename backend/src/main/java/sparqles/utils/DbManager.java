package sparqles.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import sparqles.avro.Endpoint;

public interface DbManager {

    boolean isRunning();

    void setup();

    void initEndpointCollection();

    void initScheduleCollection();

    void initAggregateCollections();

    <V extends SpecificRecordBase> boolean insert(Collection<V> results);

    <V extends SpecificRecordBase> boolean insert(V res);

    <V extends SpecificRecordBase> boolean update(V res);

    <V extends SpecificRecordBase> List<V> get(Class<V> cls, Schema schema);

    <V extends SpecificRecordBase> Iterator<V> getIterator(Class<V> cls, Schema schema);

    Endpoint getEndpoint(Endpoint ep);

    <T> List<T> getResults(Endpoint ep, Class<T> cls, Schema schema);

    <T> Iterator<T> getResultsIterator(Endpoint ep, Class<T> cls, Schema schema);

    <T extends SpecificRecordBase> List<T> getResultsSince(
            Endpoint ep, Class<T> cls, Schema schema, long since);

    <T extends SpecificRecordBase> List<T> getResultsSince(
            Endpoint ep, Class<T> cls, Schema schema, long from, long to);

    boolean cleanup(Endpoint ep);

    boolean remove(Endpoint ep, Class cls);

    boolean close();

    long getFirstAvailabitlityTime();
}
