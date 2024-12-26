package sparqles.analytics;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.avro.analytics.CalculationView;
import sparqles.avro.analytics.EPView;
import sparqles.avro.analytics.EPViewCalculation;
import sparqles.avro.calculation.CResult;
import sparqles.utils.MongoDBManager;

public class CAnalyser extends Analytics<CResult> {
    private static final Logger log = LoggerFactory.getLogger(CAnalyser.class);

    public CAnalyser(MongoDBManager db) {
        super(db);
    }

    @Override
    public boolean analyse(CResult pres) {
        log.info("[Analytics] {}", pres.getEndpointResult().getEndpoint());

        Endpoint ep = pres.getEndpointResult().getEndpoint();

        EPView epview = getEPView(ep);
        CalculationView cview = getView(ep);

        if (pres.getVoIDPart()) {
            cview.setVoIDPart(true);
            cview.setVoID(false);
        } else {
            cview.setVoID(!pres.getVoID().toString().equals(""));
            cview.setVoIDPart(false);
        }
        if (pres.getSDPart()) {
            cview.setSDPart(true);
            cview.setSD(false);
        } else {
            cview.setSD(!pres.getSD().toString().equals(""));
            cview.setSDPart(false);
        }
        cview.setCoherence(pres.getCoherence());
        cview.setRS(pres.getRS());

        cview.setLastUpdate(pres.getEndpointResult().getEnd());

        EPViewCalculation cepview = epview.getCalculation();
        cepview.setTriples(pres.getTriples());
        cepview.setEntities(pres.getEntities());
        cepview.setClasses(pres.getClasses());
        cepview.setProperties(pres.getProperties());
        cepview.setDistinctSubjects(pres.getDistinctSubjects());
        cepview.setDistinctObjects(pres.getDistinctObjects());
        cepview.setExampleResources(pres.getExampleResources());
        cepview.setVoID(pres.getVoID());
        cepview.setVoIDPart(pres.getVoIDPart());
        cepview.setSD(pres.getSD());
        cepview.setSDPart(pres.getSDPart());
        cepview.setCoherence(pres.getCoherence());
        cepview.setRS(pres.getRS());

        _db.update(cview);
        _db.update(epview);

        return true;
    }

    private CalculationView getView(Endpoint ep) {
        CalculationView view = null;
        List<CalculationView> views = new ArrayList<CalculationView>();
        if (_db != null) {
            views = _db.getResults(ep, CalculationView.class, CalculationView.SCHEMA$);
        }
        if (views.size() != 1) {
            log.warn("We have {} CalculationView, expected was 1", views.size());
        }
        if (views.size() == 0) {
            view = new CalculationView();
            view.setEndpoint(ep);
            view.setSD(false);
            view.setSDPart(false);
            view.setVoID(false);
            view.setVoIDPart(false);
            view.setCoherence(-1.0);
            view.setRS(-1.0);
            view.setLastUpdate(-1L);
            if (_db != null) _db.insert(view);
        } else {
            view = views.get(0);
        }
        return view;
    }
}
