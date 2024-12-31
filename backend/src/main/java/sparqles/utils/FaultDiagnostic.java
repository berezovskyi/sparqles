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
        //      result.setIsAvailable(false);
        //      String msg = "🕳️ host not found while connecting to " + _epURI;
        //      log.info(msg);
        //      result.setExplanation(msg);
        //      return result;
      }
      if (e.getCause() instanceof ConnectTimeoutException
          || e.getCause() instanceof ConnectException
          || e.getCause() instanceof HttpConnectTimeoutException) {
        return FaultKind.DOWN_TIMEOUT;
        //      result.setIsAvailable(false);
        //      String msg = "🐌 connection timeout while connecting to " + _epURI;
        //      log.info(msg);
        //      result.setExplanation(msg);
        //      return result;
      }
      if (e.getCause() instanceof SSLHandshakeException) {
        //      result.setIsAvailable(false);
        //      String msg = "🏰 failed to establish a TLS connection to " + _epURI;
        //      log.info(msg);
        //      log.debug(
        //        "SSLHandshakeException while connecting to {}: {}", _epURI,
        // e.getCause().getMessage());
        //      result.setExplanation(msg);
        //      return result;
        return FaultKind.DOWN_TLS_CONFIGURATION_ERROR;
      }
    } else if (e instanceof HttpException) {
      if (e.getMessage().contains("400") || e.getMessage().contains("501")) {
        //        result.setIsAvailable(false);
        //        String msg = "👾 host did not like our request (400); while connecting to " +
        // _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        return result;
        return FaultKind.BAD_REQUEST;
      } else if (e.getMessage().contains("401")) {
        //        result.setIsAvailable(false);
        //        String msg = "✋ host requires authn (401); while connecting to " + _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        result.setIsPrivate(true);
        //        return result;
        return FaultKind.AUTH_401;
      } else if (e.getMessage().contains("403")) {
        //        result.setIsAvailable(false);
        //        String msg = "✋ host requires authn (401); while connecting to " + _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        result.setIsPrivate(true);
        //        return result;
        return FaultKind.AUTH_403;
      } else if (e.getMessage().contains("500")) {
        //        result.setIsAvailable(false);
        //        String msg =
        //          "🕳 server is likely down behind reverse proxy (502); while connecting to " +
        // _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        return result;
        return FaultKind.BAD_SERVER_ERROR;
      } else if (e.getMessage().contains("502")) {
        //        result.setIsAvailable(false);
        //        String msg =
        //          "🕳 server is likely down behind reverse proxy (502); while connecting to " +
        // _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        return result;
        return FaultKind.DOWN_BAD_GATEWAY;
      } else if (e.getMessage().contains("503")) {
        //        result.setIsAvailable(false);
        //        String msg = "🕳 endpoint is overloaded or gone (503); while connecting to " +
        // _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        return result;
        return FaultKind.DOWN_ENDPOINT;
      } else if (e.getMessage().contains("504")) {
        //        result.setIsAvailable(false);
        //        String msg = "🕳 endpoint is overloaded or gone (503); while connecting to " +
        // _epURI;
        //        log.info(msg);
        //        result.setExplanation(msg);
        //        return result;
        return FaultKind.DOWN_TIMEOUT;
      }
    } else if (e instanceof ConnectTimeoutException || e instanceof ConnectException) {
      //      result.setIsAvailable(false);
      //      String msg = "🐌 connection timeout while connecting to " + _epURI;
      //      log.info(msg);
      //      result.setExplanation(msg);
      //      return result;
      return FaultKind.DOWN_TIMEOUT;
    } else if (e instanceof UnknownHostException) {
      //      result.setIsAvailable(false);
      //      String msg = "🕳️ host not found while connecting to " + _epURI;
      //      log.info(msg);
      //      result.setExplanation(msg);
      //      return result;
      return FaultKind.DOWN_HOST_NOT_FOUND;
    } else {
      if (e.getMessage() != null)
        if (e.getMessage().contains("401 Authorization Required")) return FaultKind.AUTH_401;
    }

    return FaultKind.UNKNOWN;
  }
}
