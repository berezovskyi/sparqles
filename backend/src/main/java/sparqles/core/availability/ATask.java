package sparqles.core.availability;

import org.apache.http.HttpException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.jena.query.QueryExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.avro.EndpointResult;
import sparqles.avro.availability.AResult;
import sparqles.core.EndpointTask;
import sparqles.core.interoperability.TaskRun;
import sparqles.utils.ExceptionHandler;
import sparqles.utils.QueryManager;

import javax.net.ssl.SSLHandshakeException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;

/**
 * This class performs the required task to study the availability of an endpoint.
 * <p>
 * We first perform a ASK query to check if an endpoint is available.
 * If the ask query is successful but returns false, we perform a SELECT LIMIT 1 query
 *
 * @author UmbrichJ
 */
public class ATask extends EndpointTask<AResult> {
    
    /**
     * static class logger
     */
    private static final Logger log = LoggerFactory.getLogger(ATask.class);
    
    private final static String ASKQUERY = "ASK WHERE{?s ?p ?o}";
    private final static String SELECTQUERY = "SELECT ?s WHERE{?s ?p ?o} LIMIT 1";
    
    public ATask(Endpoint ep) {
        super(ep);
    }
    
    
    @Override
    public AResult process(EndpointResult epr) {
        AResult result = new AResult();
        result.setEndpointResult(epr);
        result.setExplanation("Endpoint is operating normally");
        
        long start = System.currentTimeMillis();
        try {
            QueryExecution qe = QueryManager.getExecution(epr.getEndpoint(), ASKQUERY);
            // FIXME: find a new way
            //            qe.setTimeout(TaskRun.A_FIRST_RESULT_TIMEOUT, TaskRun.A_FIRST_RESULT_TIMEOUT);
            boolean response = qe.execAsk();
            if (response) {
                result.setResponseTime((System.currentTimeMillis() - start));
                if ((System.currentTimeMillis() - start) > 20000) {
                    result.setIsAvailable(false);
                    result.setExplanation("SPARQL Endpoint is timeout");
                } else {
                    result.setIsAvailable(response);
                    result.setExplanation("Endpoint is operating normally");
                }
                log.debug("executed ask {}", epr.getEndpoint().getUri().toString());
                return result;
            } else {
                return testSelect(epr);
            }
        } catch (InterruptedException e) {
            String ex = ExceptionHandler.logAndtoString(e);
            result.setException(ex);
            result.setExplanation(ex);
            
            log.warn("failed ASK query for {}, {}", _epURI, ExceptionHandler.logAndtoString(e, true));
            return result;
        } catch (Exception e) {
            return testSelect(epr);
        }
    }
    
    private AResult testSelect(EndpointResult epr) {
        AResult result = new AResult();
        result.setEndpointResult(epr);
        result.setExplanation("Endpoint is operating normally");
        long start = System.currentTimeMillis();
        try {
            QueryExecution qe = QueryManager.getExecution(epr.getEndpoint(), SELECTQUERY);
            // FIXME
            //            qe.setTimeout(TaskRun.A_FIRST_RESULT_TIMEOUT, TaskRun.A_FIRST_RESULT_TIMEOUT);
            boolean response = qe.execSelect().hasNext();
            
            if (response) {
                result.setResponseTime((System.currentTimeMillis() - start));
                if ((System.currentTimeMillis() - start) > TaskRun.A_FIRST_RESULT_TIMEOUT) {
                    result.setIsAvailable(false);
                    result.setExplanation("SPARQL Endpoint is timeout");
                } else {
                    result.setIsAvailable(response);
                    result.setExplanation("Endpoint is operating normally");
                }
                log.debug("executed select {}", epr.getEndpoint().getUri().toString());
                return result;
            } else {
                result.setIsAvailable(response);
                log.debug("executed no response {}", epr.getEndpoint().getUri().toString());
                return result;
            }
        } catch (ConnectTimeoutException | ConnectException e) {
            result.setIsAvailable(false);
            String msg = "🐌 connection timeout while connecting to " + _epURI;
            log.info(msg);
            result.setExplanation(msg);
            return result;
        } catch (UnknownHostException e) {
            result.setIsAvailable(false);
            String msg = "🕳️ host not found while connecting to " + _epURI;
            log.info(msg);
            result.setExplanation(msg);
            return result;
        } catch (HttpException e) {
            if (e.getCause() instanceof UnknownHostException) {
                result.setIsAvailable(false);
                String msg = "🕳️ host not found while connecting to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                return result;
            }
            if (e.getCause() instanceof ConnectTimeoutException || e.getCause() instanceof ConnectException || e.getCause() instanceof HttpConnectTimeoutException) {
                result.setIsAvailable(false);
                String msg = "🐌 connection timeout while connecting to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                return result;
            }
            if (e.getCause() instanceof SSLHandshakeException) {
                result.setIsAvailable(false);
                String msg = "🏰 failed to establish a TLS connection to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                return result;
            }
            if (e.getMessage().contains("400")) {
                result.setIsAvailable(false);
                String msg = "👾 host did not like our request (400); while connecting to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                return result;
            } else if (e.getMessage().contains("401")) {
                result.setIsAvailable(false);
                String msg = "✋ host requires authn (401); while connecting to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                result.setIsPrivate(true);
                return result;
            } else if (e.getMessage().contains("502")) {
                result.setIsAvailable(false);
                String msg = "🕳 server is likely down behind reverse proxy (502); while connecting to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                return result;
            } else if (e.getMessage().contains("503")) {
                result.setIsAvailable(false);
                String msg = "🕳 endpoint is overloaded or gone (503); while connecting to " + _epURI;
                log.info(msg);
                result.setExplanation(msg);
                return result;
            }
        } catch (Exception e1) {
            result.setIsAvailable(false);
            String ex = ExceptionHandler.logAndtoString(e1);
            result.setException(ex);
            result.setExplanation(ex);
            if (e1.getMessage() != null)
                if (e1.getMessage().contains("401 Authorization Required"))
                    result.setIsPrivate(true);
            
            log.warn("failed SELECT query for {}, {}", _epURI, ExceptionHandler.logAndtoString(e1, true));
        }
        return result;
    }
}
