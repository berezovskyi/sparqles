package sparqles.analytics;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.avro.availability.AResult;
import sparqles.avro.calculation.CResult;
import sparqles.avro.discovery.DResult;
import sparqles.avro.features.FResult;
import sparqles.avro.performance.PResult;
import sparqles.utils.DbManager;

public class AnalyserInit {

  private static final Logger log = LoggerFactory.getLogger(AnalyserInit.class);

  private DbManager _db;

  private boolean _onlyLast;

  public AnalyserInit(DbManager db) {
    this(db, false);
  }

  public AnalyserInit(DbManager db, boolean onlyLast) {
    _db = db;
    _onlyLast = onlyLast;
  }

  /** Computes the aggregated statistics for the Availability task */
  public void run() {

    // List<Endpoint> eps = _db.get(Endpoint.class, Endpoint.SCHEMA$);
    // AAnalyser a = new AAnalyser(_db);
    // PAnalyser p = new PAnalyser(_db);
    // DAnalyser d = new DAnalyser(_db);
    // FAnalyser f = new FAnalyser(_db);
    // CAnalyser c = new CAnalyser(_db);

    // log.info("Analysing {} endpoints", eps.size());
    // for (Endpoint ep : eps) {
    //   log.info("ANALYSE {}", ep.getUri());

    //   availability(ep, a);
    //   discoverability(ep, d);
    //   interoperability(ep, f);
    //   performance(ep, p);
    //   calculation(ep, c);
    // }
  }

  private void discoverability(Endpoint ep, DAnalyser d) {
    TreeSet<DResult> res =
        new TreeSet<DResult>(
            new Comparator<DResult>() {
              public int compare(DResult o1, DResult o2) {
                int diff =
                    Comparator.comparingLong(
                            (DResult value) -> value.getEndpointResult().getStart())
                        .compare(o1, o2);
                return diff;
              }
            });

    List<DResult> epRes = _db.getResults(ep, DResult.class, DResult.SCHEMA$);
    for (DResult epres : epRes) {
      res.add(epres);
    }
    log.info("Analyse {} Performance results", epRes.size());
    if (_onlyLast && epRes.size() != 0) {
      d.analyse(res.last());
    } else {
      for (DResult ares : res) {
        d.analyse(ares);
      }
    }
    log.info("ANALYSE DISCOVERABILITY {} and {}", ep, epRes.size());
  }

  private void performance(Endpoint ep, PAnalyser p) {
    TreeSet<PResult> res =
        new TreeSet<PResult>(
            new Comparator<PResult>() {
              public int compare(PResult o1, PResult o2) {
                int diff =
                    Comparator.comparingLong(
                            (PResult value) -> value.getEndpointResult().getStart())
                        .compare(o1, o2);
                return diff;
              }
            });

    List<PResult> epRes = _db.getResults(ep, PResult.class, PResult.SCHEMA$);
    for (PResult epres : epRes) {
      res.add(epres);
    }
    log.info("Analyse {} Performance results", epRes.size());
    if (_onlyLast && epRes.size() != 0) {
      p.analyse(res.last());
    } else {
      for (PResult ares : res) {
        p.analyse(ares);
      }
    }
    log.info("ANALYSE PERFORMANCE {} and {}", ep, epRes.size());
  }

  private void interoperability(Endpoint ep, FAnalyser f) {
    TreeSet<FResult> res =
        new TreeSet<FResult>(
            new Comparator<FResult>() {
              public int compare(FResult o1, FResult o2) {
                int diff =
                    Comparator.comparingLong(
                            (FResult value) -> value.getEndpointResult().getStart())
                        .compare(o1, o2);
                return diff;
              }
            });

    List<FResult> epRes = _db.getResults(ep, FResult.class, FResult.SCHEMA$);
    for (FResult epres : epRes) {
      res.add(epres);
    }
    log.info("Analyse {} Interoperability results", epRes.size());
    if (_onlyLast && epRes.size() != 0) {
      f.analyse(res.last());
    } else {
      for (FResult ares : res) {
        f.analyse(ares);
      }
    }
    log.info("ANALYSE INTEROPERABILITY {} and {}", ep, epRes.size());
  }

  private void availability(Endpoint ep, AAnalyser a) {

    TreeSet<AResult> res =
        new TreeSet<AResult>(
            new Comparator<AResult>() {
              public int compare(AResult o1, AResult o2) {
                int diff =
                    Comparator.comparingLong(
                            (AResult value) -> value.getEndpointResult().getStart())
                        .compare(o1, o2);
                return diff;
              }
            });

    List<AResult> epRes = _db.getResults(ep, AResult.class, AResult.SCHEMA$);
    for (AResult epres : epRes) {
      res.add(epres);
    }
    if (_onlyLast && epRes.size() != 0) {
      a.analyse(res.last());
    } else {
      for (AResult ares : res) {
        a.analyse(ares);
      }
    }
    log.info("ANALYSE AVAILABILITY {} and {}", ep.getUri(), epRes.size());
  }

  private void calculation(Endpoint ep, CAnalyser c) {

    TreeSet<CResult> res =
        new TreeSet<CResult>(
            new Comparator<CResult>() {
              public int compare(CResult o1, CResult o2) {
                int diff =
                    Comparator.comparingLong(
                            (CResult value) -> value.getEndpointResult().getStart())
                        .compare(o1, o2);
                return diff;
              }
            });

    List<CResult> epRes = _db.getResults(ep, CResult.class, CResult.SCHEMA$);
    for (CResult epres : epRes) {
      res.add(epres);
    }
    if (_onlyLast && epRes.size() != 0) {
      c.analyse(res.last());
    } else {
      for (CResult cres : res) {
        c.analyse(cres);
      }
    }
    log.info("ANALYSE CALCULATION {} and {}", ep.getUri(), epRes.size());
  }
}
