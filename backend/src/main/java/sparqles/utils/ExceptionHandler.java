package sparqles.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);
    private static final AtomicInteger excID = new AtomicInteger();
    private static final AtomicLong excCounter = new AtomicLong();
    
    private static final HashMap<Class, Integer> exceptionID = new HashMap<Class, Integer>();
    
    
    public static String logAndtoString(Exception e) {
        return logAndtoString(e, false);
    }
    
    
    public static String logAndtoString(Exception e, boolean withExceptionID) {
        
        
        String id = ExceptionHandler.getExceptionID(e);
        ExceptionHandler.log(id, e);
        
//        if(ExceptionUtils.indexOfThrowable(e, Error.class) != -1) {
        if(ExceptionUtils.indexOfThrowable(e, VirtualMachineError.class) != -1) {
//        if(ExceptionUtils.indexOfThrowable(e, OutOfMemoryError.class) != -1) {
            // 1) no point going further
            // 2) no clue who/how a descendant of java.lang.Error was caught and not rethrown
            // REVISIT: consider terminating on Error.class
            // TODO: remove this crutch and ensure no part of the system does something that
            // requires this hack
            try {
                System.exit(1);
            } catch(Throwable e2) { // last resort
                e2.printStackTrace(); // last resort
                Runtime.getRuntime().exit(255); // last resort
            }
        }
        
        StringBuilder sb = new StringBuilder();
        if (withExceptionID) {
            sb.append(id).append("> ");
        }
        sb.append(e.getClass().getSimpleName())
            .append(" msg:").append(e.getMessage())
            .append(" cause:").append(e.getCause()).toString();
        
        return sb.toString();
        
    }
    
    
    public static String toFullString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    
    private static String getExceptionID(Exception e) {
//		System.out.println(exceptionID);
        
        Integer id = exceptionID.get(e.getClass());
        if (id == null) {
            id = excID.getAndIncrement();
            exceptionID.put(e.getClass(), id);
            log.info("New Exception ID: {} -> {}", id, e.getClass());
        }
        return "EXC@" + id + "#" + excCounter.getAndIncrement();
    }
    
    private static void log(String id, Exception e) {
        log.info(id, e);
    }
    
    public static String getExceptionSummary(String message) {
        if (StringUtils.isBlank(message)) {
            return "N/A";
        }
        int cutoff = message.indexOf('\n');
        if (cutoff == -1 ) {
            cutoff = message.length();
        }
        if (cutoff > 160) {
            cutoff = 160;
        }
        return message.substring(0, cutoff);
    }
}
