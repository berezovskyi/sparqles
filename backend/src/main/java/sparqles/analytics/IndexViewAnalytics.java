package sparqles.analytics;

import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.analytics.AvailabilityIndex;
import sparqles.avro.analytics.EPView;
import sparqles.avro.analytics.EPViewAvailability;
import sparqles.avro.analytics.EPViewAvailabilityDataPoint;
import sparqles.avro.analytics.EPViewCalculation;
import sparqles.avro.analytics.EPViewDiscoverability;
import sparqles.avro.analytics.EPViewDiscoverabilityData;
import sparqles.avro.analytics.EPViewInteroperability;
import sparqles.avro.analytics.EPViewInteroperabilityData;
import sparqles.avro.analytics.EPViewPerformance;
import sparqles.avro.analytics.EPViewPerformanceData;
import sparqles.avro.analytics.EPViewPerformanceDataValues;
import sparqles.avro.analytics.Index;
import sparqles.avro.analytics.IndexAvailabilityDataPoint;
import sparqles.avro.analytics.IndexViewCalculation;
import sparqles.avro.analytics.IndexViewCalculationData;
import sparqles.avro.analytics.IndexViewCalculationDataValues;
import sparqles.avro.analytics.IndexViewDiscoverability;
import sparqles.avro.analytics.IndexViewDiscoverabilityData;
import sparqles.avro.analytics.IndexViewDiscoverabilityDataValues;
import sparqles.avro.analytics.IndexViewInterData;
import sparqles.avro.analytics.IndexViewInterDataValues;
import sparqles.avro.analytics.IndexViewInteroperability;
import sparqles.avro.analytics.IndexViewPerformance;
import sparqles.avro.analytics.IndexViewPerformanceData;
import sparqles.avro.analytics.IndexViewPerformanceDataValues;
import sparqles.core.SPARQLESProperties;
import sparqles.core.Task;
import sparqles.utils.MongoDBManager;

public class IndexViewAnalytics implements Task<Index> {
  
  private static final Logger log = LoggerFactory.getLogger(IndexViewAnalytics.class);
  public static final int MAX_DATA_POINTS = 16_384;
  
  private MongoDBManager _dbm;
  final int askCold = 0, askWarm = 1, joinCold = 2, joinWarm = 3;
  final int sparql1_solMods = 0,
      sparql1_com = 1,
      sparql1_graph = 2,
      sparql11_agg = 3,
      sparql11_filter = 4,
      sparql11_other = 5;

  @Override
  public Index call() throws Exception {

    // get the index view
    Collection<Index> idxs = _dbm.get(Index.class, Index.SCHEMA$);
    Index idx = null;
    if (idxs == null || idxs.isEmpty()) {
      idx = createIndex();
      _dbm.insert(idx);
    } else if (idxs.size() > 1) {
      log.error("Too many results");
    } else {
      idx = idxs.iterator().next();
    }

    // get epview
    Collection<EPView> epviews = _dbm.get(EPView.class, EPView.SCHEMA$);
    log.info("Found {} idx views and {} epviews", idxs.size(), epviews.size());

    // Prepare aggregated analytics
    Map<String, SimpleHistogram> weekHist = new HashMap<String, SimpleHistogram>();

    SummaryStatistics[] perfStats = {
      new SummaryStatistics(),
      new SummaryStatistics(),
      new SummaryStatistics(),
      new SummaryStatistics()
    };
    var thresholdStats = new DescriptiveStatistics();

    SimpleHistogram[] interStats = {
      new SimpleHistogram(),
      new SimpleHistogram(),
      new SimpleHistogram(),
      new SimpleHistogram(),
      new SimpleHistogram(),
      new SimpleHistogram()
    };

    Count[] discoStats = {new Count<String>(), new Count<String>()};

    Count[] calcStats = {new Count<String>(), new Count<String>(), new Count<String>()};

    // analyse availability
    recalculateAvailabilityMonthly();

    // iterate over all epviews and analyse them
    for (EPView epv : epviews) {
      log.trace("EPView: {}", epv);
      analyseAvailability(epv.getAvailability(), weekHist);

      // analyse performance
      analysePerformance(epv.getPerformance(), perfStats, thresholdStats);

      // analyse interoperability
      analyseInteroperability(epv.getInteroperability(), interStats);

      // analyse discoverability
      analyseDiscoverability(epv.getDiscoverability(), discoStats);

      // analyse calculation
      analyseCalculation(epv.getCalculation(), calcStats);
    }

    // update the index view
    updateAvailabilityStats(idx, weekHist);

    // update performance stats
    updatePerformanceStats(idx, perfStats, thresholdStats);

    // update interoperability stats
    updateInteroperability(idx, interStats);

    // update discoverability stats
    updateDiscoverability(idx, discoStats);

    // update calculation stats
    updateCalculation(idx, calcStats);

    log.info("Updated view {}", idx);
    _dbm.update(idx);

    return idx;
  }

  private void recalculateAvailabilityMonthly() {
    log.info("Recalculating availability monthly");
    AEvol.recalculateMonthly(_dbm);
  }

  private void analyseCalculation(EPViewCalculation calculation, Count<String>[] calcStats) {
    double coherence = calculation.getCoherence();
    if (coherence < 0)
      ; // DO NOTHING
    else if (coherence < 0.25) calcStats[1].add("[0.00-0.25[");
    else if (coherence < 0.5) calcStats[1].add("[0.25-0.50[");
    else if (coherence < 0.75) calcStats[1].add("[0.50-0.75[");
    else if (coherence < 0.95) calcStats[1].add("[0.75-0.95[");
    else calcStats[1].add("[0.95-1.00]");

    double rs = calculation.getRS();
    if (rs < 0)
      ; // DO NOTHING
    else if (rs < 10) calcStats[2].add("[0-10[");
    else if (rs < 100) calcStats[2].add("[10-100[");
    else if (rs < 1000) calcStats[2].add("[100-1000[");
    else if (rs < 10000) calcStats[2].add("[1000-10000[");
    else calcStats[2].add("[10000-)");

    if (!calculation.getVoID().toString().equals("")) calcStats[0].add("VoID");
    if (calculation.getVoIDPart()) calcStats[0].add("VoIDPart");
    if (!calculation.getSD().toString().equals("")) calcStats[0].add("SD");
    if (calculation.getSDPart()) calcStats[0].add("SDPart");
    if (calculation.getCoherence() >= 0) calcStats[0].add("Coherence");
    if (calculation.getRS() >= 0) calcStats[0].add("RS");

    calcStats[0].add("total");
  }

  private void updateCalculation(Index idx, Count[] object) {

    IndexViewCalculation iv = idx.getCalculation();

    Count<String> coherence = object[1];

    List<IndexViewCalculationData> l1 = new ArrayList<IndexViewCalculationData>();
    List<IndexViewCalculationDataValues> lv1 = new ArrayList<IndexViewCalculationDataValues>();

    TreeSet<IndexViewCalculationDataValues> set1 =
        new TreeSet<IndexViewCalculationDataValues>(
            new Comparator<IndexViewCalculationDataValues>() {

              @Override
              public int compare(
                  IndexViewCalculationDataValues o1, IndexViewCalculationDataValues o2) {
                int diff =
                    Comparator.comparingDouble(
                            (IndexViewCalculationDataValues value) -> value.getValue())
                        .compare(o1, o2);
                if (diff == 0) diff = -1;
                return diff;
              }
            });

    for (String k : coherence.keySet()) {
      set1.add(
          new IndexViewCalculationDataValues(k, coherence.get(k) / (double) coherence.getTotal()));
    }

    for (IndexViewCalculationDataValues d : set1.descendingSet()) {
      lv1.add(d);
    }
    l1.add(new IndexViewCalculationData("Coherence", lv1));

    iv.setCoherences(l1);

    Count<String> rs = object[2];

    List<IndexViewCalculationData> l2 = new ArrayList<IndexViewCalculationData>();
    List<IndexViewCalculationDataValues> lv2 = new ArrayList<IndexViewCalculationDataValues>();

    TreeSet<IndexViewCalculationDataValues> set2 =
        new TreeSet<IndexViewCalculationDataValues>(
            new Comparator<IndexViewCalculationDataValues>() {

              @Override
              public int compare(
                  IndexViewCalculationDataValues o1, IndexViewCalculationDataValues o2) {
                int diff =
                    Comparator.comparingDouble(
                            (IndexViewCalculationDataValues value) -> value.getValue())
                        .compare(o1, o2);
                if (diff == 0) diff = -1;
                return diff;
              }
            });

    for (String k : rs.keySet()) {
      set2.add(new IndexViewCalculationDataValues(k, rs.get(k) / (double) rs.getTotal()));
    }

    for (IndexViewCalculationDataValues d : set2.descendingSet()) {
      lv2.add(d);
    }
    l2.add(new IndexViewCalculationData("RS", lv2));

    iv.setRss(l2);

    Count<String> stats = object[0];
    double totalVal = (double) stats.get("total");
    iv.setVoID(stats.get("VoID") / totalVal);
    iv.setVoIDPart(stats.get("VoIDPart") / totalVal);
    iv.setSD(stats.get("SD") / totalVal);
    iv.setSDPart(stats.get("SDPart") / totalVal);
    iv.setCoherence(stats.get("Coherence") / totalVal);
    iv.setRS(stats.get("RS") / totalVal);
  }

  private void analyseDiscoverability(
      EPViewDiscoverability discoverability, Count<String>[] discoStats) {
    discoStats[1].add(discoverability.getServerName().toString());

    boolean sd = false, voidd = false;
    if (discoverability.getSDDescription().size() != 0) {
      for (EPViewDiscoverabilityData d : discoverability.getSDDescription()) {
        if (d.getValue()) {
          discoStats[0].add("sd");
          sd = true;
          break;
        }
      }
    }
    if (discoverability.getVoIDDescription().size() != 0) {
      for (EPViewDiscoverabilityData d : discoverability.getVoIDDescription()) {
        if (d.getValue()) {
          discoStats[0].add("void");
          voidd = true;
          break;
        }
      }
    }
    if (!voidd && !sd) {
      discoStats[0].add("no");
    }

    discoStats[0].add("total");
  }

  private void updateDiscoverability(Index idx, Count[] object) {

    IndexViewDiscoverability iv = idx.getDiscoverability();
    Count<String> server = object[1];

    List<IndexViewDiscoverabilityData> l = new ArrayList<IndexViewDiscoverabilityData>();
    List<IndexViewDiscoverabilityDataValues> lv =
        new ArrayList<IndexViewDiscoverabilityDataValues>();

    TreeSet<IndexViewDiscoverabilityDataValues> set =
        new TreeSet<IndexViewDiscoverabilityDataValues>(
            new Comparator<IndexViewDiscoverabilityDataValues>() {

              @Override
              public int compare(
                  IndexViewDiscoverabilityDataValues o1, IndexViewDiscoverabilityDataValues o2) {
                int diff =
                    Comparator.comparingDouble(
                            (IndexViewDiscoverabilityDataValues value) -> value.getValue())
                        .compare(o1, o2);
                if (diff == 0) diff = -1;
                return diff;
              }
            });

    for (String k : server.keySet()) {
      set.add(
          new IndexViewDiscoverabilityDataValues(k, server.get(k) / (double) server.getTotal()));
    }

    for (IndexViewDiscoverabilityDataValues d : set.descendingSet()) {
      lv.add(d);
    }
    l.add(new IndexViewDiscoverabilityData("Server Names", lv));
    iv.setServerName(l);

    Count<String> stats = object[0];
    int v = 0;
    if (stats.containsKey("no")) {
      v = stats.get("no");
      iv.setNoDescription(v / (double) stats.get("total"));
    } else iv.setNoDescription(0D);

    v = stats.getOrDefault("sd", 0);

    Integer totalVal = stats.get("total");
    if (totalVal != null) {
      iv.setSDDescription(v / (double) totalVal);

      v = stats.getOrDefault("void", 0);
      iv.setVoIDDescription(v / (double) totalVal);
    } else {
      log.error("Total value is missing");
      iv.setSDDescription(-1.0);
      iv.setVoIDDescription(-1.0);
    }
  }

  private void updateInteroperability(Index idx, SimpleHistogram[] interStats) {
    IndexViewInteroperability iv = idx.getInteroperability();

    List<IndexViewInterData> v = new ArrayList<IndexViewInterData>();
    iv.setData(v);

    v.add(updateSPARQL1(interStats));
    v.add(updateSPARQL11(interStats));
  }

  private IndexViewInterData updateSPARQL11(SimpleHistogram[] interStats) {
    IndexViewInterData ivd = new IndexViewInterData();
    ivd.setColor("#2ca02c");
    ivd.setKey("SPARQL 1.1");

    ArrayList<IndexViewInterDataValues> v = new ArrayList<IndexViewInterDataValues>();
    // sparql1 mod
    double perc = interStats[sparql11_agg].bin[3] / (double) interStats[sparql11_agg].sampleSize;
    v.add(new IndexViewInterDataValues("Aggregate", perc));

    // sparql1 com
    perc = interStats[sparql11_filter].bin[3] / (double) interStats[sparql11_filter].sampleSize;
    v.add(new IndexViewInterDataValues("Filter", perc));

    // sparql1 graph
    perc = interStats[sparql11_other].bin[3] / (double) interStats[sparql11_other].sampleSize;
    v.add(new IndexViewInterDataValues("Other", perc));

    ivd.setData(v);

    return ivd;
  }

  private IndexViewInterData updateSPARQL1(SimpleHistogram[] interStats) {
    IndexViewInterData ivd = new IndexViewInterData();
    ivd.setColor("#1f77b4");
    ivd.setKey("SPARQL 1.0");

    ArrayList<IndexViewInterDataValues> v = new ArrayList<IndexViewInterDataValues>();
    // sparql1 mod
    double perc =
        interStats[sparql1_solMods].bin[3] / (double) interStats[sparql1_solMods].sampleSize;
    v.add(new IndexViewInterDataValues("Solution Modifiers", perc));

    // sparql1 com
    perc = interStats[sparql1_com].bin[3] / (double) interStats[sparql1_com].sampleSize;
    v.add(new IndexViewInterDataValues("Common Operators and Filters", perc));

    // sparql1 graph
    perc = interStats[sparql1_graph].bin[3] / (double) interStats[sparql1_graph].sampleSize;
    v.add(new IndexViewInterDataValues("Graph and other", perc));

    ivd.setData(v);

    return ivd;
  }

  private void analyseInteroperability(
      EPViewInteroperability interoperability, SimpleHistogram[] interStats) {
    boolean[] all = new boolean[6];
    Arrays.fill(all, true);

    for (EPViewInteroperabilityData d : interoperability.getSPARQL1Features()) {

      String l = d.getLabel().toString();

      boolean bv = d.getValue();
      /*
      SEL[.]*ORDERBY*OFFSET
      SEL[.]*ORDERBY-ASC
      SEL[.]*ORDERBY-DESC
      SEL[.]*ORDERBY
      SEL-DISTINCT[.]
      SEL-REDUCED[.]
       */
      if (l.contains("orderby") || l.contains("distinct") || l.contains("reduced")) {
        all[sparql1_solMods] = all[sparql1_solMods] && bv;
      }

      /*
      SEL[.]
      SEL[JOIN]
      SEL[OPT]
      SEL[UNION]
      -- matches fil --
      SEL[FIL(!BOUND)]
      SEL[FIL(BLANK)]
      SEL[FIL(BOOL)]
      SEL[FIL(IRI)]
      SEL[FIL(NUM)]
      SEL[FIL(REGEX)]
      SEL[FIL(REGEX-i)]
      SEL[FIL(STR)]
      SEL[BNODE]  -> bnode
      SEL[EMPTY]  -> empty
       */
      else if (l.contains("fil")
          || l.contains("bnode")
          || l.contains("empty")
          || l.contains("union")
          || l.contains("opt")
          || l.contains("join")
          || l.contains("sel[.]")) {

        all[sparql1_com] = all[sparql1_com] && bv;

      }
      /*
      SEL[FROM]
      SEL[GRAPH]
      SEL[GRAPH;JOIN]
      SEL[GRAPH;UNION]
      CON[.]
      CON[JOIN]
      CON[OPT]
      ASK[.]
       */
      else if (l.contains("graph")
          || l.contains("con")
          || l.contains("ask")
          || l.contains("from")) {
        all[sparql1_graph] = all[sparql1_graph] && bv;
      } else {
        log.info("Could not match {}", l);
      }
    }
    for (EPViewInteroperabilityData d : interoperability.getSPARQL11Features()) {
      String l = d.getLabel().toString();
      boolean bv = d.getValue();
      /*
      Aggregate
      SEL[AVG]*GROUPBY
      SEL[AVG]
      SEL[COUNT]*GROUPBY
      SEL[MAX]
      SEL[MIN]
      SEL[MINUS]
      SEL[SUM]
       */
      if (l.contains("avg")
          || l.contains("count")
          || l.contains("max")
          || l.contains("min")
          || l.contains("minus")
          || l.contains("sum")) {
        all[sparql11_agg] = all[sparql11_agg] && bv;
      }
      /*
      Filter
      SEL[FIL(!EXISTS)]
      SEL[FIL(ABS)]
      SEL[FIL(CONTAINS)]
      SEL[FIL(EXISTS)]
      SEL[FIL(START)]
       */
      else if (l.contains("fil") || l.contains("distinct") || l.contains("reduced")) {
        all[sparql11_filter] = all[sparql11_filter] && bv;
      }
      /*
      Other
      ASK[FIL(!IN)]
      CON-[.]
      SEL[BIND]
      SEL[PATHS]
      SEL[SERVICE]
      SEL[SUBQ]
      SEL[SUBQ;GRAPH]
      SEL[VALUES]
       */
      else if (l.contains("ask")
          || l.contains("con")
          || l.contains("bind")
          || l.contains("paths")
          || l.contains("service")
          || l.contains("subq")
          || l.contains("values")) {
        all[sparql11_other] = all[sparql11_other] && bv;
      } else {
        log.info("Could not match {}", l);
      }
    }

    boolean update = false;
    for (int i = 0; i < all.length; i++) {
      update = update || all[i];
    }

    if (update) {
      for (int i = 0; i < all.length; i++) {
        if (all[i]) interStats[i].add(1D);
        else interStats[i].add(0D);
      }
    }
    //		System.out.println(Arrays.toString(interStats));

  }

  private void analysePerformance(
      EPViewPerformance performance,
      SummaryStatistics[] perfStats,
      DescriptiveStatistics thresholdStats) {
    update(performance.getAsk(), perfStats[askCold], perfStats[askWarm]);
    update(performance.getJoin(), perfStats[joinCold], perfStats[joinWarm]);
    updateThreshold(performance.getThreshold(), thresholdStats);
  }

  private void updateThreshold(long threshold, DescriptiveStatistics thresholdStats) {
    log.debug("Found threshold: {}", threshold);
    // 100002 is the LIMIT used to detect a threshold
    if (threshold > 0 && threshold < 100_002) {
      thresholdStats.addValue(threshold);
    }
  }

  private void analyseAvailability(
      EPViewAvailability availability, Map<String, SimpleHistogram> weekHist) {
    for (EPViewAvailabilityDataPoint value : takeLast(availability.getData().getValues(), MAX_DATA_POINTS)) {
      update(value, weekHist);
    }
  }
  
  private static <T> List<T> takeLast(List<T> values, int n) {
    return values.subList(values.size()-Math.min(values.size(),n), values.size());
  }
  
  private void updatePerformanceStats(
      Index idx, SummaryStatistics[] perfStats, DescriptiveStatistics thresholdStats) {
    ArrayList<IndexViewPerformanceData> data = new ArrayList<IndexViewPerformanceData>();
    List<IndexViewPerformanceDataValues> l = new ArrayList<IndexViewPerformanceDataValues>();
    IndexViewPerformanceData cold = new IndexViewPerformanceData("Cold Tests", "#1f77b4", l);
    l = new ArrayList<IndexViewPerformanceDataValues>();
    IndexViewPerformanceData warm = new IndexViewPerformanceData("Warm Tests", "#2ca02c", l);
    data.add(cold);
    data.add(warm);

    double v = (perfStats[askCold].getN() == 0) ? -1D : perfStats[askCold].getMean();
    cold.getData().add(new IndexViewPerformanceDataValues("Average ASK", v));
    v = (perfStats[joinCold].getN() == 0) ? -1D : perfStats[joinCold].getMean();
    cold.getData().add(new IndexViewPerformanceDataValues("Average JOIN", v));

    v = (perfStats[askWarm].getN() == 0) ? -1D : perfStats[askWarm].getMean();
    warm.getData().add(new IndexViewPerformanceDataValues("Average ASK", v));
    v = (perfStats[joinWarm].getN() == 0) ? -1D : perfStats[joinWarm].getMean();
    warm.getData().add(new IndexViewPerformanceDataValues("Average JOIN", v));

    // median
    var medianThresh = (long) thresholdStats.getPercentile(50);
    log.debug("Median thresh: {}", medianThresh);
    idx.getPerformance().setThreshold(medianThresh);
    idx.getPerformance().setData(data);
  }

  private void updateAvailabilityStats(Index idx, Map<String, SimpleHistogram> weekHist) {
    List<AvailabilityIndex> aidxs = idx.getAvailability();
    // update availability stats
    for (Entry<String, SimpleHistogram> week : weekHist.entrySet()) {
      SimpleHistogram sh = week.getValue();

      int total = sh.sampleSize;

      for (AvailabilityIndex aidx : aidxs) {
        int value = 0;
        if (aidx.getKey().equals("[0;5]")) value = sh.bin[0];
        if (aidx.getKey().equals("]5;90]")) value = sh.bin[1];
        if (aidx.getKey().equals("]90;95]")) value = sh.bin[2];
        if (aidx.getKey().equals("]95;100]")) value = sh.bin[3];

        boolean exists = false;
        for (IndexAvailabilityDataPoint i : aidx.getValues()) {
          if (i.getX().equals(week.getKey())) {
            exists = true;
            i.setY(value / (double) total);
          }
        }
        if (!exists)
          aidx.getValues()
              .add(new IndexAvailabilityDataPoint(week.getKey(), value / (double) total));
      }
    }
  }

  private void update(
      List<EPViewPerformanceData> results, SummaryStatistics cold, SummaryStatistics warm) {

    for (EPViewPerformanceData pdata : results) {
      if (pdata.getKey().toString().contains("Cold")) {
        for (EPViewPerformanceDataValues v : pdata.getData()) {
          cold.addValue(v.getValue());
        }
      }
      if (pdata.getKey().toString().contains("Warm")) {
        for (EPViewPerformanceDataValues v : pdata.getData()) {
          warm.addValue(v.getValue());
        }
      }
    }
  }

  private Index createIndex() {
    Index idx = new Index();
    idx.setEndpoint(SPARQLESProperties.getSparqlesEndpoint());

    AvailabilityIndex aidx =
        new AvailabilityIndex("[0;5]", new ArrayList<IndexAvailabilityDataPoint>());
    List<AvailabilityIndex> aidxs = new ArrayList<AvailabilityIndex>();

    aidxs.add(aidx);

    aidx = new AvailabilityIndex("]5;90]", new ArrayList<IndexAvailabilityDataPoint>());
    aidxs.add(aidx);

    aidx = new AvailabilityIndex("]90;95]", new ArrayList<IndexAvailabilityDataPoint>());
    aidxs.add(aidx);

    aidx = new AvailabilityIndex("]95;100]", new ArrayList<IndexAvailabilityDataPoint>());
    aidxs.add(aidx);

    idx.setAvailability(aidxs);

    IndexViewPerformance idxp = new IndexViewPerformance();
    idxp.setThreshold(-1L);
    idxp.setData(new ArrayList<IndexViewPerformanceData>());
    idx.setPerformance(idxp);

    IndexViewInteroperability idxi = new IndexViewInteroperability();
    idxi.setData(new ArrayList<IndexViewInterData>());
    idx.setInteroperability(idxi);

    IndexViewDiscoverability idxd = new IndexViewDiscoverability();
    idxd.setNoDescription(-1D);
    idxd.setSDDescription(-1D);
    idxd.setVoIDDescription(-1D);
    idxd.setServerName(new ArrayList<IndexViewDiscoverabilityData>());
    idx.setDiscoverability(idxd);

    IndexViewCalculation idxc = new IndexViewCalculation();
    idxc.setVoID(-1D);
    idxc.setVoIDPart(-1D);
    idxc.setSD(-1D);
    idxc.setSDPart(-1D);
    idxc.setCoherence(-1D);
    idxc.setRS(-1D);
    idxc.setCoherences(new ArrayList<IndexViewCalculationData>());
    idxc.setRss(new ArrayList<IndexViewCalculationData>());
    idx.setCalculation(idxc);

    return idx;
  }

  private void update(EPViewAvailabilityDataPoint value, Map<String, SimpleHistogram> weekHist) {
    String key = "" + value.getX();
    SimpleHistogram sh = weekHist.get(key);
    if (sh == null) {
      sh = new SimpleHistogram();
      weekHist.put(key, sh);
    }
    sh.add(value.getY());
  }

  @Override
  public void setDBManager(MongoDBManager dbm) {
    _dbm = dbm;
  }

  class SimpleHistogram {

    int sampleSize = 0;
    int[] bin = {0, 0, 0, 0};

    public void add(Double d) {
      if (d <= 0.05) bin[0]++;
      else if (0.05 < d && d <= 0.9) bin[1]++;
      else if (0.9 < d && d <= 0.95) bin[2]++;
      else if (0.95 < d && d <= 1) bin[3]++;

      sampleSize++;
    }

    @Override
    public String toString() {
      return Arrays.toString(bin) + ":" + sampleSize;
    }
  }

  class Count<T> extends TreeMap<T, Integer> {
    int sampleSize = 0;

    public void add(T t) {
      if (this.containsKey(t)) {
        this.put(t, this.get(t) + 1);
      } else this.put(t, 1);

      sampleSize++;
    }

    public double getTotal() {
      return sampleSize;
    }
  }
}
