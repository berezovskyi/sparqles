package sparqles.utils;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.jena.query.QueryException;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.apache.jena.sparql.resultset.ResultSetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaultDiagnostic {
  private static final Logger log = LoggerFactory.getLogger(FaultDiagnostic.class);

  public static FaultKind faultKindForJenaQuery(Exception e) {
    log.trace("Diagnosing Jena query fault", e);
    if (e == null) {
      throw new NullPointerException("Exception shall not be null");
    }
    if (e instanceof QueryExceptionHTTP qe) {
      log.trace("QueryExceptionHTTP message={}", qe.getMessage());
      log.trace("QueryExceptionHTTP responseMessage={}", qe.getResponseMessage());
      log.trace("QueryExceptionHTTP statusCode={}", qe.getStatusCode());
      log.trace("QueryExceptionHTTP statusLine={}", qe.getStatusLine());
      log.trace("QueryExceptionHTTP cause", qe.getCause());
      log.trace("QueryExceptionHTTP response={}", StringUtils.trunc(qe.getResponse()));
      if (e.getCause() instanceof UnknownHostException) {
        return FaultKind.DOWN_HOST_NOT_FOUND;
      }
      if (e.getCause() instanceof ConnectTimeoutException
          || e.getCause() instanceof ConnectException
          || e.getCause() instanceof HttpConnectTimeoutException) {
        return FaultKind.DOWN_TIMEOUT;
      }
      if (e.getCause() instanceof SSLHandshakeException) {
        return FaultKind.DOWN_TLS_CONFIGURATION_ERROR;
      } else if (e.getCause() instanceof HttpException || qe.getStatusCode() >= 400) {
        return faultKindForApacheHttpException(qe.getStatusCode());
      }
    } else if (e instanceof ResultSetException) {
      if (e.getMessage().contains("Not a result set syntax:")) {
        // (potentially) RDF but not a suitable for this SPARQL query response
        return FaultKind.BAD_RESPONSE;
      }
      log.debug("Unknown fault", e);
      return FaultKind.UNKNOWN;
    } else if (e instanceof HttpException) {
      return faultKindForApacheHttpException(e);
    } else if (e instanceof ConnectTimeoutException
        || e instanceof ConnectException
        || e instanceof HttpConnectTimeoutException) {
      return FaultKind.DOWN_TIMEOUT;
    } else if (e instanceof UnknownHostException) {
      return FaultKind.DOWN_HOST_NOT_FOUND;
    } else if (e instanceof QueryException) {
      if (e.getMessage().contains("Endpoint returned Content-Type:")) {
        return FaultKind.BAD_RESPONSE;
      }
      log.debug("Unknown fault", e);
      return FaultKind.UNKNOWN;
    } else {
      if (e.getMessage() != null)
        if (e.getMessage().contains("401 Authorization Required")) return FaultKind.AUTH_401;
    }

    log.debug("Unknown fault", e);
    return FaultKind.UNKNOWN;
  }

  public static FaultKind faultKindForApacheHttpException(Throwable e) {
    if (e == null) {
      throw new NullPointerException("Exception shall not be null");
    }
    if (e.getMessage().contains("400") || e.getMessage().contains("501")) {
      return FaultKind.BAD_REQUEST;
    } else if (e.getMessage().contains("401")) {
      return FaultKind.AUTH_401;
    } else if (e.getMessage().contains("403")) {
      return FaultKind.AUTH_403;
    } else if (e.getMessage().contains("500")) {
      return FaultKind.BAD_SERVER_ERROR;
    } else if (e.getMessage().contains("502")) {
      return FaultKind.DOWN_BAD_GATEWAY;
    } else if (e.getMessage().contains("503")) {
      return FaultKind.DOWN_ENDPOINT;
    } else if (e.getMessage().contains("504")) {
      return FaultKind.DOWN_TIMEOUT;
    }

    log.debug("Unknown fault", e);
    return FaultKind.UNKNOWN;
  }

  public static FaultKind faultKindForApacheHttpException(int code) {
    if (code == 400 || code == 501) {
      return FaultKind.BAD_REQUEST;
    } else if (code == 401) {
      return FaultKind.AUTH_401;
    } else if (code == 403) {
      return FaultKind.AUTH_403;
    } else if (code == 404) {
      return FaultKind.DOWN_404_NOT_FOUND;
    } else if (code == 500) {
      return FaultKind.BAD_SERVER_ERROR;
    } else if (code == 502) {
      return FaultKind.DOWN_BAD_GATEWAY;
    } else if (code == 503) {
      return FaultKind.DOWN_ENDPOINT;
    } else if (code == 504) {
      return FaultKind.DOWN_TIMEOUT;
    }

    log.debug("Unknown fault code={}", code);
    return FaultKind.UNKNOWN;
  }
}
