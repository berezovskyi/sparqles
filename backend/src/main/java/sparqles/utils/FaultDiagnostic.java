package sparqles.utils;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
      if (log.isTraceEnabled()) {
        log.trace(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.trace("QueryExceptionHTTP message={}", qe.getMessage());
        log.trace("QueryExceptionHTTP responseMessage={}", qe.getResponseMessage());
        log.trace("QueryExceptionHTTP statusCode={}", qe.getStatusCode());
        log.trace("QueryExceptionHTTP statusLine={}", qe.getStatusLine());
        log.trace("QueryExceptionHTTP cause", qe.getCause());
        log.trace("QueryExceptionHTTP response={}", StringUtils.trunc(qe.getResponse()));
        log.trace("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
      }
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
    } else if (ExceptionUtils.indexOfThrowable(e, SocketException.class) != -1) {
      return FaultKind.DOWN_ENDPOINT;
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

  public static String interpretFault(FaultKind faultKind) {
    return switch (faultKind) {
      case UNKNOWN -> "Unknown fault";
      case DOWN_HOST_NOT_FOUND -> "🕳️ host not found";
      case DOWN_404_NOT_FOUND -> "🕳️ 404 endpoint not found";
      case DOWN_TLS_CONFIGURATION_ERROR -> "🔧 TLS misconfiguration (failed handshake)";
      case DOWN_TIMEOUT -> "🐌 connection timeout";
      case DOWN_BAD_GATEWAY -> "🔧 bad gateway";
      case DOWN_GONE_410 -> "💨 410 gone";
      case DOWN_ENDPOINT -> "🕳 endpoint down";
      case AUTH_401 -> "🛡️ server requires authentication";
      case AUTH_403 -> "🛡️ server denied access";
      case BAD_REQUEST -> "👾 host did not like our request (400)";
      case BAD_RESPONSE -> "🗑️ malformed response";
      case BAD_SERVER_ERROR -> "🔧 server error";
    };
  }
}
