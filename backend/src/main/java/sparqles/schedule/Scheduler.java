package sparqles.schedule;

import static sparqles.core.CONSTANTS.*;

import java.security.SecureRandom;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.analytics.RefreshDataHubTask;
import sparqles.avro.Endpoint;
import sparqles.avro.schedule.Schedule;
import sparqles.core.EndpointTask;
import sparqles.core.SPARQLESProperties;
import sparqles.core.Task;
import sparqles.core.TaskFactory;
import sparqles.core.availability.ATask;
import sparqles.schedule.iter.CronBasedIterator;
import sparqles.schedule.iter.ScheduleIterator;
import sparqles.utils.ExceptionHandler;
import sparqles.utils.FileManager;
import sparqles.utils.DbManager;

public class Scheduler {
  private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

  @Deprecated public static final String CRON_EVERY_HOUR = "0 0 0/1 1/1 * ? *";
  @Deprecated public static final String CRON_EVERY_ONETEN = "0 30 1 1/1 * ? *";
  private static final String CRON_EVERY_DAY_AT_715 = "0 15 7 1/1 * ? *";
  private static final String CRON_EVERY_DAY_AT_215 = "0 15 2 1/1 * ? *";
  private static final String CRON_EVERY_MON_WED_FRI_SUN_THU_AT_410 = "0 10 4 ? * WED,THU *";

  private static final String CRON_EVERY_SUN_AT_310 = "0 10 3 ? * SUN *";
  private static final String CRON_EVERY_SUN_AT_2330 = "0 30 23 ? * SUN *";
  private static final String CRON_EVERY_SAT_AT_310 = "0 10 3 ? * SAT *";
  private static final String CRON_FIRST_SAT_AT_MONTH_AT_TWO = "0 0 2 ? 1/1 SAT#1 *";
  private static final String CRON_EVERY_FIVE_MINUTES = "0 0/5 * 1/1 * ? *";

  /** The default schedules for various tasks http://www.cronmaker.com/ */
  //  private static final Map<String, String> taskSchedule = new HashMap<String, String>();
  private static final Random random = new SecureRandom();

  //
  //  static {
  //    // availability
  ////    taskSchedule.put(ATASK, CRON_EVERY_HOUR);
  //    // performance
  ////    taskSchedule.put(PTASK, CRON_EVERY_ONETEN);
  //    // interoperability
  //    taskSchedule.put(FTASK, CRON_EVERY_SUN_AT_310);
  //    // discoverability
  //    taskSchedule.put(DTASK, CRON_EVERY_SAT_AT_310);
  //    taskSchedule.put(CTASK, CRON_EVERY_SUN_AT_2330);
  //    // index
  //    taskSchedule.put(ITASK, CRON_EVERY_DAY_AT_715);
  //    // datahub refresh
  //    taskSchedule.put(ETASK, CRON_EVERY_DAY_AT_215);
  //  }

  private final ScheduledExecutorService SERVICE, ASERVICE;
  private FileManager _fm;
  private DbManager _dbm;

  private SchedulerMonitor _monitor;

  public Scheduler() {
    this(SPARQLESProperties.getTASK_THREADS());
  }

  public Scheduler(int threads) {
    int athreads = (int) (threads * 0.3);
    int tthreads = threads - athreads;
    // TODO: use virtual threads after JDK21
    //        SERVICE = Executors.newScheduledThreadPool(tthreads, Thread.ofVirtual().factory());
    //        ASERVICE = Executors.newScheduledThreadPool(athreads, Thread.ofVirtual().factory());
    SERVICE = Executors.newScheduledThreadPool(tthreads);
    ASERVICE = Executors.newScheduledThreadPool(athreads);

    _monitor = new SchedulerMonitor();
    _monitor.start();
    log.info("INIT Scheduler with {} athreads and {} threads", athreads, tthreads);
  }

  /**
   * Creates for all endpoints in the DB a default schedule
   *
   * @param dbm
   * @return
   */
  public static Collection<Schedule> createDefaultSchedule(DbManager dbm) {
    List<Schedule> l = new ArrayList<Schedule>();
    Collection<Endpoint> eps = dbm.get(Endpoint.class, Endpoint.SCHEMA$);
    for (Endpoint ep : eps) {
      Schedule s = defaultSchedule(ep);
      l.add(s);
    }

    // add the analytics schedules for
    Schedule s = new Schedule();
    s.setEndpoint(SPARQLESProperties.getSparqlesEndpoint());
    s.setITask(cronForITask(s.getEndpoint()));
    s.setETask(cronForETask(s.getEndpoint()));
    l.add(s);

    return l;
  }

  /**
   * Returns the default schedule element for the endpoints
   *
   * @param ep
   * @return
   */
  public static Schedule defaultSchedule(Endpoint ep) {
    Schedule s = new Schedule();
    s.setEndpoint(ep);

    s.setATask(randomATaskCron(ep));
    s.setPTask(randomPTaskCron(ep));
    s.setFTask(cronForFTask(ep));
    s.setDTask(cronForDTask(ep));
    s.setCTask(cronForCTask(ep));
    s.setITask(cronForITask(ep));

    return s;
  }

  /** Check endpoint availability once an hour, spread out evenly */
  private static CharSequence randomATaskCron(Endpoint ep) {
    return String.format("0 %d/60 * ? * * *", random.nextInt(60));
  }

  /** Avoid hitting SPARQL endpoints with expensive queries during "business" hours */
  private static CharSequence randomPTaskCron(Endpoint ep) {
    var randHours = random.nextInt(19, 24 + 5) % 24;
    var randMinutes = random.nextInt(60);

    return String.format("0 %d %d ? * * *", randMinutes, randHours);
  }

  private static CharSequence cronForDTask(Endpoint ep) {
    return CRON_EVERY_SAT_AT_310;
  }

  private static CharSequence cronForFTask(Endpoint ep) {
    return CRON_EVERY_SUN_AT_310;
  }

  private static CharSequence cronForCTask(Endpoint ep) {
    return CRON_EVERY_SUN_AT_2330;
  }

  private static CharSequence cronForITask(Endpoint ep) {
    return CRON_EVERY_DAY_AT_715;
  }

  private static CharSequence cronForETask(Endpoint etask) {
    //    return CRON_EVERY_DAY_AT_215;
    return null; // old.datahub is not updated any more, disable the task for now
  }

  /**
   * Initial the scheduler with the schedules from the underlying DB.
   *
   * @param db
   */
  public void init(DbManager db) {

    Collection<Schedule> schedules = db.get(Schedule.class, Schedule.SCHEMA$);
    log.info("Scheduling tasks for {} endpoints", schedules.size());

    for (Schedule sd : schedules) {
      initSchedule(sd);
    }
  }

  public void initSchedule(Schedule sd) {
    Endpoint ep = sd.getEndpoint();

    try {
      if (sd.getATask() != null) {
        schedule(
            TaskFactory.create(ATASK, ep, _dbm, _fm),
            new CronBasedIterator(sd.getATask().toString()));
      }
      if (sd.getPTask() != null) {
        schedule(
            TaskFactory.create(PTASK, ep, _dbm, _fm),
            new CronBasedIterator(sd.getPTask().toString()));
      }
      if (sd.getFTask() != null) {
        schedule(
            TaskFactory.create(FTASK, ep, _dbm, _fm),
            new CronBasedIterator(sd.getFTask().toString()));
      }
      if (sd.getDTask() != null) {
        schedule(
            TaskFactory.create(DTASK, ep, _dbm, _fm),
            new CronBasedIterator(sd.getDTask().toString()));
      }
      if (sd.getCTask() != null) {
        schedule(
            TaskFactory.create(CTASK, ep, _dbm, _fm),
            new CronBasedIterator(sd.getCTask().toString()));
      }
      if (sd.getITask() != null) {
        schedule(
            TaskFactory.create(ITASK, ep, _dbm, _fm),
            new CronBasedIterator(sd.getITask().toString()));
      }
      if (sd.getETask() != null) {
        RefreshDataHubTask task = (RefreshDataHubTask) TaskFactory.create(ETASK, ep, _dbm, _fm);
        task.setScheduler(this);
        schedule(task, new CronBasedIterator(sd.getITask().toString()));
      }
    } catch (ParseException e) {
      log.warn(
          "EXEC ParseException: {} for {}", ep.getUri(), ExceptionHandler.logAndtoString(e, true));
    }
  }

  public void schedule(Task task, ScheduleIterator iter) {
    Date time = iter.next();
    schedule(task, iter, time);
  }

  public void schedule(Task task, ScheduleIterator iter, Date time) {
    long startTime = time.getTime() - System.currentTimeMillis();

    SchedulerTimerTask t = new SchedulerTimerTask(task, iter);

    if (task instanceof ATask)
      _monitor.submitA(ASERVICE.schedule(t, startTime, TimeUnit.MILLISECONDS));
    else _monitor.submit(SERVICE.schedule(t, startTime, TimeUnit.MILLISECONDS));

    //		log.info("SCHEDULED {} next:'{}' ",task, time);
    log.debug("SCHEDULED {} next:'{}' policy:'{}'", task, time, iter);
  }

  public void shutdown() {
    SERVICE.shutdown();
  }

  private void reschedule(Task task, ScheduleIterator iter) {
    Date time = iter.next();
    if (time.getTime() < System.currentTimeMillis()) {
      log.error("PAST stop scheduling task, next date is in the past!");
      return;
    }
    if (task instanceof EndpointTask) {
      EndpointTask t = (EndpointTask) task;
      Endpoint ep = _dbm.getEndpoint(t.getEndpoint());
      if (ep == null) {
        log.warn("Endpoint {} was removed from DB, stop schedulingl", ep);
        return;
      }
      t.setEndpoint(ep);
    }
    schedule(task, iter, time);

    Object[] s = {task, time, iter};
    log.debug("RESCHEDULED {} next:'{}' policy:'{}'", s);
  }

  public void close() {
    log.info("Shutting down scheduler service");
    List<Runnable> tasks = SERVICE.shutdownNow();
    log.info("{} Tasks were scheduled after the shutdown command", tasks.size());
    _monitor.halt();
    if (_dbm != null) {
      _dbm.close();
    }
  }

  public void useDB(DbManager dbm) {
    _dbm = dbm;
  }

  public void useFileManager(FileManager fm) {
    _fm = fm;
  }

  public void delSchedule(Endpoint ep) {
    // TODO Auto-generated method stub

  }

  /**
   * A timer task which executes the assigned task and automatically reschedules the data
   *
   * @author umbrichj
   */
  class SchedulerTimerTask implements Runnable {
    private ScheduleIterator iterator;
    private Task schedulerTask;

    public SchedulerTimerTask(Task schedulerTask, ScheduleIterator iterator) {
      this.schedulerTask = schedulerTask;
      this.iterator = iterator;
    }

    public void run() {
      try {
        schedulerTask.call();
        reschedule(schedulerTask, iterator);
      } catch (Exception e) {
        log.error("Exception: {} {}", schedulerTask, ExceptionHandler.logAndtoString(e, true));
      }
    }
  }
}
