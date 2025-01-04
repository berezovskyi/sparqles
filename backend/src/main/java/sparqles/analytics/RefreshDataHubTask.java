package sparqles.analytics;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.avro.analytics.Index;
import sparqles.core.Task;
import sparqles.schedule.Scheduler;
import sparqles.utils.DatahubAccess;
import sparqles.utils.MongoDBManager;

public class RefreshDataHubTask implements Task<Index> {

  private static final Logger log = LoggerFactory.getLogger(RefreshDataHubTask.class);
  private MongoDBManager _dbm;
  private Scheduler _s;

  @Override
  public Index call() throws Exception {
    log.info("execute updating ckan catalog");

    Collection<Endpoint> datahub = DatahubAccess.checkEndpointList();

    if (datahub.size() == 0) return null;

    // flush the endpoint collection
    _dbm.initEndpointCollection();

    int newEPs = 0, upEPs = 0, remEPs = 0;
    for (Endpoint ep : datahub) {
      if (_dbm.insert(ep)) {
        newEPs++;
      }
    }

    /*
    		//Collection<Endpoint>  db = _dbm.get(Endpoint.class, Endpoint.SCHEMA$);
    		TreeSet<Endpoint> ckan = new TreeSet<Endpoint>(new EndpointComparator());
    		ckan.addAll(datahub);




    		TreeSet<Endpoint> sparqles = new TreeSet<Endpoint>(new EndpointComparator());
    		sparqles.addAll(db);


    		int newEPs = 0, upEPs=0, remEPs=0;
    		for(Endpoint ep : ckan){
    			if(! sparqles.contains(ep)){
    				log.info("New endpoint {}",ep);
    				//new

    				if(_dbm.insert(ep)){
    					newEPs++;
    //					Schedule sch = _s.defaultSchedule(ep);
    //					_dbm.insert(sch);

    //					_s.initSchedule(sch);
    				}


    			}else{
    				//update
    				log.info("Update endpoint {}",ep);
    				if( _dbm.update(ep))
    					upEPs++;

    			}
    		}

    		for(Endpoint ep : sparqles){
    			if(ep.getUri().equals(CONSTANTS.SPARQLES.getUri())) continue;
    			if(! ckan.contains(ep)){
    				//remove
    				log.info("Remove endpoint {}",ep);
    				if( _dbm.cleanup(ep)){
    					remEPs++;

    				}

    			}
    		}

    		log.info("executed updating ckan catalog, {} total, {} updates, {} new, {} removals",ckan.size(),upEPs, newEPs,remEPs);
    		*/
    log.info("executed updating ckan catalog, {} new", newEPs);
    return null;
  }

  @Override
  public void setDBManager(MongoDBManager dbm) {
    _dbm = dbm;
  }

  public void setScheduler(Scheduler scheduler) {
    _s = scheduler;
  }
}
