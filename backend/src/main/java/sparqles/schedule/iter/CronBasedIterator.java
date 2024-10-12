package sparqles.schedule.iter;

import java.text.ParseException;
import java.util.Date;

import sparqles.schedule.CronExpression;

public class CronBasedIterator implements ScheduleIterator {
    private final CronExpression _cron;
    
    private Date next;
    
    public CronBasedIterator(String cronExpression) throws ParseException {
        _cron = new CronExpression(cronExpression);
        next = _cron.getNextValidTimeAfter(new Date());
    }
    
    public Date next() {
        Date res = next;
        next = _cron.getNextValidTimeAfter(res);
        return res;
    }
    
    @Override
    public String toString() {
        return "" + this.getClass().getSimpleName() + "(" + _cron.getCronExpression() + ")";
    }
}
