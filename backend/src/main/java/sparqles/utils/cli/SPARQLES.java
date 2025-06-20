package sparqles.utils.cli;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.analytics.AnalyserInit;
import sparqles.analytics.IndexViewAnalytics;
import sparqles.analytics.RefreshDataHubTask;
import sparqles.analytics.StatsAnalyser;
import sparqles.avro.Dataset;
import sparqles.avro.Endpoint;
import sparqles.avro.availability.AResult;
import sparqles.avro.calculation.CResult;
import sparqles.avro.discovery.DResult;
import sparqles.avro.features.FResult;
import sparqles.avro.performance.PResult;
import sparqles.avro.schedule.Schedule;
import sparqles.core.CONSTANTS;
import sparqles.core.EndpointFactory;
import sparqles.core.SPARQLESProperties;
import sparqles.schedule.Scheduler;
import sparqles.utils.DatahubAccess;
import sparqles.utils.DateFormater;
import sparqles.utils.FileManager;
import sparqles.utils.MongoDBManager;

/**
 * Main CLI class for the SPARQL Endpoint status program
 *
 * @author UmbrichJ
 */
public class SPARQLES extends CLIObject {
  private static final Logger log = LoggerFactory.getLogger(SPARQLES.class);
  private Scheduler scheduler;
  private MongoDBManager dbm;
  private FileManager _fm;

  @Override
  public String getDescription() {
    return "Start and control SPARQLES";
  }

  @Override
  protected void addOptions(Options opts) {
    opts.addOption(ARGUMENTS.OPTION_PROP_FILE);
    opts.addOption(ARGUMENTS.OPTION_INIT);
    opts.addOption(ARGUMENTS.OPTION_UPDATE_EPS);
    opts.addOption(ARGUMENTS.OPTION_START);
    opts.addOption(ARGUMENTS.OPTION_STATS);
    opts.addOption(ARGUMENTS.OPTION_RECOMPUTE);
    opts.addOption(ARGUMENTS.OPTION_RECOMPUTELAST);
    opts.addOption(ARGUMENTS.OPTION_RESCHEDULE);

    opts.addOption(ARGUMENTS.OPTION_RUN);
    opts.addOption(ARGUMENTS.OPTION_INDEX);
    opts.addOption(ARGUMENTS.OPTION_ADD);
  }

  @Override
  protected void execute(CommandLine cmd) {
    parseCMD(cmd);
    //    System.setProperty("javax.net.debug", "ssl:handshake:verbose");

    // reinitialise datahub
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_INIT)) {
      // check the endpoint list
      Collection<Endpoint> eps = DatahubAccess.checkEndpointList();
      dbm.initEndpointCollection();
      dbm.setup();
      dbm.insert(eps);
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_UPDATE_EPS)) {
      // check the endpoint list
      RefreshDataHubTask t = new RefreshDataHubTask();
      t.setDBManager(dbm);
      t.setScheduler(scheduler);

      try {
        t.call();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_RESCHEDULE)) {
      Collection<Schedule> epss = Scheduler.createDefaultSchedule(dbm);
      log.info("Created a new schedule for {} endpoints", epss.size());
      dbm.initScheduleCollection();
      dbm.setup();
      dbm.insert(epss);
      log.info("Persisted the schedule for {} endpoints", epss.size());
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_RECOMPUTE)) {
      recomputeAnalytics(false);
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_RECOMPUTELAST)) {
      recomputeAnalytics(true);
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_INDEX)) {
      recomputeIndexView();
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_STATS)) {
      computeStats();
    }
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_ADD)) {
      String[] opts = CLIObject.getOptionValue(cmd, ARGUMENTS.PARAM_ADD).trim().split(";");
      String endpointUri = opts[0];
      String label = "";
      if (opts.length > 1) label = opts[1];
      addEndpoint(endpointUri, label);
    }

    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_RUN)) {
      String task = CLIObject.getOptionValue(cmd, ARGUMENTS.PARAM_RUN).trim();
      if (task.equalsIgnoreCase(CONSTANTS.ITASK)) {
        IndexViewAnalytics a = new IndexViewAnalytics();
        a.setDBManager(dbm);
        try {
          a.call();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (task.equalsIgnoreCase(CONSTANTS.ATASK)) {
        OneTimeExecution<AResult> ex = new OneTimeExecution<AResult>(dbm, _fm);
        ex.run(CONSTANTS.ATASK);
      } else if (task.equalsIgnoreCase(CONSTANTS.FTASK)) {
        OneTimeExecution<FResult> ex = new OneTimeExecution<FResult>(dbm, _fm);
        ex.run(CONSTANTS.FTASK);
      } else if (task.equalsIgnoreCase(CONSTANTS.PTASK)) {
        OneTimeExecution<PResult> ex = new OneTimeExecution<PResult>(dbm, _fm);
        ex.run(CONSTANTS.PTASK);
      } else if (task.equalsIgnoreCase(CONSTANTS.DTASK)) {
        OneTimeExecution<DResult> ex = new OneTimeExecution<DResult>(dbm, _fm);
        ex.run(CONSTANTS.DTASK);
      } else if (task.equalsIgnoreCase(CONSTANTS.CTASK)) {
        OneTimeExecution<CResult> ex = new OneTimeExecution<CResult>(dbm, _fm);
        ex.run(CONSTANTS.CTASK);
      } else {
        log.warn("Task {} not known", task);
      }
    }

    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_FLAG_START)) {
      start();
    }

    Runtime.getRuntime().addShutdownHook(new ShutdownThread(this));
  }

  private void recomputeIndexView() {
    IndexViewAnalytics a = new IndexViewAnalytics();
    a.setDBManager(dbm);
    try {
      a.call();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void computeStats() {
    StatsAnalyser stats = new StatsAnalyser();
    stats.setDBManager(dbm);
    try {
      stats.call();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void recomputeAnalytics(boolean onlyLast) {
    dbm.initAggregateCollections();

    AnalyserInit a = new AnalyserInit(dbm, onlyLast);
    a.run();
  }

  private void addEndpoint(String endpointUri, String label) {
    log.info("Adding endpoint with uri \"{}\" and label \"{}\"", endpointUri, label);
    try {
      Endpoint ep = EndpointFactory.newEndpoint(endpointUri);
      if (!label.equals("")) {
        Dataset d = new Dataset();
        d.setLabel(label);
        d.setUri(endpointUri);
        List<Dataset> l = ep.getDatasets();
        l.add(d);
        ep.setDatasets(l);
      }
      dbm.insert(ep);
    } catch (URISyntaxException e) {
      log.warn("URISyntaxException:{}", e.getMessage());
    }
  }

  private void start() {
    scheduler.init(dbm);
    try {
      long start = System.currentTimeMillis();
      int cycleCount = 0;
      while (true) {
        log.info(
            "Running since {}", DateFormater.formatInterval(System.currentTimeMillis() - start));
        
        // Memory monitoring and optimization every 10 cycles (30 minutes)
        if (++cycleCount % 10 == 0) {
          logMemoryUsage();
          
          // Force garbage collection if memory usage is high
          Runtime runtime = Runtime.getRuntime();
          long usedMemory = runtime.totalMemory() - runtime.freeMemory();
          long maxMemory = runtime.maxMemory();
          double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
          
          if (memoryUsagePercent > 80) {
            log.warn("High memory usage detected: {:.1f}%. Forcing garbage collection.", memoryUsagePercent);
            System.gc();
            Thread.sleep(1000); // Give GC time to work
            
            // Log memory after GC
            usedMemory = runtime.totalMemory() - runtime.freeMemory();  
            memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            log.info("Memory usage after GC: {:.1f}%", memoryUsagePercent);
          }
        }
        
        Thread.sleep(1800000); // 30 minutes
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
  
  private void logMemoryUsage() {
    Runtime runtime = Runtime.getRuntime();
    long maxMemory = runtime.maxMemory();
    long totalMemory = runtime.totalMemory();
    long freeMemory = runtime.freeMemory();
    long usedMemory = totalMemory - freeMemory;
    
    log.info("Memory Usage - Used: {}MB, Free: {}MB, Total: {}MB, Max: {}MB", 
             usedMemory / 1024 / 1024, 
             freeMemory / 1024 / 1024,
             totalMemory / 1024 / 1024, 
             maxMemory / 1024 / 1024);
  }

  private void parseCMD(CommandLine cmd) {
    // load the Properties
    if (CLIObject.hasOption(cmd, ARGUMENTS.PARAM_PROP_FILE)) {
      File propFile = new File(CLIObject.getOptionValue(cmd, ARGUMENTS.PARAM_PROP_FILE));
      if (propFile.exists()) {
        log.info("Reading properties from {}", propFile);
        SPARQLESProperties.init(propFile);
      } else {
        log.warn("Specified property file ({}) does not exist", propFile);
      }
    }
    setup(true, false);
  }

  public void init(String[] arguments) {
    CommandLine cmd = verifyArgs(arguments);
    parseCMD(cmd);
  }

  private void setup(boolean useDB, boolean useFM) {
    // Init the scheduler
    scheduler = new Scheduler();

    if (useDB) {
      dbm = new MongoDBManager();
      scheduler.useDB(dbm);
    }
    if (useFM) {
      _fm = new FileManager();
    }
    scheduler.useFileManager(_fm);
  }

  public void stop() {
    log.info("[START] [SHUTDOWN] Shutting down the system");
    scheduler.close();
    log.info("[SUCCESS] [SHUTDOWN] Everything closed normally");
  }

  class ShutdownThread extends Thread {
    private SPARQLES _s;

    public ShutdownThread(SPARQLES s) {
      _s = s;
    }

    @Override
    public void run() {
      _s.stop();
    }
  }
}
