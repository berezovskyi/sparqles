package sparqles.core.availability;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Duration;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.avro.EndpointResult;
import sparqles.avro.availability.AResult;
import sparqles.core.EndpointTask;
import sparqles.core.interoperability.TaskRun;
import sparqles.utils.*;

/**
 * This class performs the required task to study the availability of an endpoint.
 *
 * <p>We first perform a ASK query to check if an endpoint is available. If the ask query is
 * successful but returns false, we perform a SELECT LIMIT 1 query
 *
 * @author UmbrichJ
 */
public class ATask extends EndpointTask<AResult> {

  /** static class logger */
  private static final Logger log = LoggerFactory.getLogger(ATask.class);

  private static final String ASKQUERY = "ASK WHERE{?s ?p ?o}";
  private static final String SELECTQUERY = "SELECT ?s WHERE{?s ?p ?o} LIMIT 1";

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
      QueryExecution qe =
          QueryManager.getExecution(
              epr.getEndpoint(), ASKQUERY, Duration.of(TaskRun.A_FIRST_RESULT_TIMEOUT, MILLIS));
      qe.getContext().set(ARQ.httpQueryTimeout, TaskRun.A_FIRST_RESULT_TIMEOUT);

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
    } catch (Exception e) {
      var faultKind = FaultDiagnostic.faultKindForJenaQuery(e);
      if (faultKind == FaultKind.UNKNOWN) {
        result.setIsAvailable(false);
        String ex = ExceptionHandler.logAndtoString(e);
        result.setException(StringUtils.stringCutoff(ex));
        result.setExplanation(
            "Unknown error encountered while attempting an ASK query fallback (SELECT LIMIT 1)");
        log.warn(
            "Unknown error encountered while attempting an ASK query fallback (SELECT LIMIT 1)"
                + " (type={})",
            e.getClass().getName());
        log.debug("Stacktrace", e);
      } else {
        if (faultKind == FaultKind.BAD_REQUEST
            || faultKind == FaultKind.BAD_RESPONSE
            || faultKind == FaultKind.DOWN_TIMEOUT
            || faultKind == FaultKind.BAD_SERVER_ERROR) {
          return testSelect(epr);
        } else {
          updateAResultFromFault(faultKind, result);
        }
      }
    }
    return result;
  }

  private AResult testSelect(EndpointResult epr) {
    AResult result = new AResult();
    result.setEndpointResult(epr);
    result.setExplanation("Endpoint is operating normally");
    long start = System.currentTimeMillis();
    try {
      QueryExecution qe =
          QueryManager.getExecution(
              epr.getEndpoint(), SELECTQUERY, Duration.of(TaskRun.A_FIRST_RESULT_TIMEOUT, MILLIS));
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
    } catch (Exception e) {
      var faultKind = FaultDiagnostic.faultKindForJenaQuery(e);
      if (faultKind == FaultKind.UNKNOWN) {
        result.setIsAvailable(false);
        String ex = ExceptionHandler.logAndtoString(e);
        result.setException(StringUtils.stringCutoff(ex));
        result.setExplanation(
            "Unknown error encountered while attempting an ASK query fallback (SELECT LIMIT 1)");
        log.warn(
            "Unknown error encountered while attempting an ASK query fallback (SELECT LIMIT 1)"
                + " (type={})",
            e.getClass().getName());
        log.debug("Stacktrace", e);
      } else {
        updateAResultFromFault(faultKind, result);
      }
    }
    return result;
  }

  private void updateAResultFromFault(FaultKind faultKind, AResult result) {
    result.setIsAvailable(false);
    switch (faultKind) {
      case UNKNOWN -> {
        throw new IllegalArgumentException();
      }
      case DOWN_HOST_NOT_FOUND -> result.setExplanation("🕳️ host not found");
      case DOWN_404_NOT_FOUND -> result.setExplanation("🕳️ 404 endpoint not found");
      case DOWN_TLS_CONFIGURATION_ERROR ->
          result.setExplanation("🔧 TLS misconfiguration (failed handshake)");
      case DOWN_TIMEOUT -> result.setExplanation("🐌 connection timeout");
      case DOWN_BAD_GATEWAY -> result.setExplanation("🔧 bad gateway");
      case DOWN_GONE_410 -> result.setExplanation("💨 410 gone");
      case DOWN_ENDPOINT -> result.setExplanation("🕳 endpoint down");
      case AUTH_401 -> result.setExplanation("🛡️ server requires authentication");
      case AUTH_403 -> result.setExplanation("🛡️ server denied access");
      case BAD_REQUEST -> result.setExplanation("👾 host did not like our request (400)");
      case BAD_RESPONSE -> result.setExplanation("🗑️ malformed response");
    }
  }
}
