package sparqles.utils;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import javax.net.ssl.SSLHandshakeException;
import org.apache.http.HttpException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

public class FaultDiagnostic {
  public static FaultKind faultKindForJenaQuery(Exception e) {
    if (e == null) {
      throw new NullPointerException("Exception shall not be null");
    }
    if (e instanceof QueryExceptionHTTP) {
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
      } else if (e.getCause() instanceof HttpException) {
        var e1 = e.getCause();
        return faultKindForApacheHttpException(e1);
      }
    } else if (e instanceof HttpException) {
      return faultKindForApacheHttpException(e);
    } else if (e instanceof ConnectTimeoutException || e instanceof ConnectException) {
      return FaultKind.DOWN_TIMEOUT;
    } else if (e instanceof UnknownHostException) {
      return FaultKind.DOWN_HOST_NOT_FOUND;
    } else {
      if (e.getMessage() != null)
        if (e.getMessage().contains("401 Authorization Required")) return FaultKind.AUTH_401;
    }

    return FaultKind.UNKNOWN;
  }

  public static FaultKind faultKindForApacheHttpException(Throwable e) {
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
    return FaultKind.UNKNOWN;
  }
}
