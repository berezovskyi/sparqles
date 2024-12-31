package sparqles.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.jena.http.sys.HttpRequestModifier;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.sparql.exec.http.Params;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparqles.avro.Endpoint;
import sparqles.core.interoperability.TaskRun;

public class QueryManager {

  public static final UserAgentRequestModifier USER_AGENT_REQUEST_MODIFIER = new UserAgentRequestModifier();
  private static final Logger log = LoggerFactory.getLogger(QueryManager.class);

  public static String getQuery(String folder, String qFile) {
    log.info("getQuery {}, {}", folder, qFile);
    String content = null;
    Scanner scanner = null;
    if (folder.startsWith("file:")) {
      File fold = new File(folder.replace("file:", ""));
      try {
        scanner = new Scanner(new File(fold, qFile));
      } catch (FileNotFoundException e) {
        log.error("Query file not found: {}", qFile, e);
      }
    } else {
      InputStream res = ClassLoader.getSystemResourceAsStream(folder + qFile);
      if (res != null) scanner = new Scanner(res);
    }
    if (scanner == null) {
      log.warn("FAILED Could not load query file {} from {}", qFile, folder);
      return null;
    }

    if (scanner.hasNext()) content = scanner.useDelimiter("\\Z").next();

    log.debug("PARSED input:{},output:{}", qFile, content);
    scanner.close();
    return substitute(content);
  }

  private static String substitute(String query) {
    long time = System.currentTimeMillis();
    if (query.contains("%%uri1")) {
      String url1 = "<http://nonsensical.com/1/" + time + ">";
      query = query.replace("%%uri1", url1);
    }
    if (query.contains("%%uri2")) {
      String url2 = "<http://nonsensical.com/2/" + time + ">";
      query = query.replace("%%uri2", url2);
    }
    if (query.contains("%%uri3")) {
      String url3 = "<http://nonsensical.com/3/" + time + ">";
      query = query.replace("%%uri3", url3);
    }
    return query;
  }

  public static QueryExecution getExecution(Endpoint ep, String query) throws Exception {
    return getExecution(ep.getUri().toString(), query, -1);
  }

  public static QueryExecution getExecution(String epURL, String query) {
    return getExecution(epURL, query, -1);
  }

  public static QueryExecution getExecution(Endpoint ep, String query, Duration timeout) {
    return getExecution(ep.getUri().toString(), query, timeout.toSeconds());
  }

  public static QueryExecution getExecution(String epURL, String query, Duration timeout) {
    return getExecution(epURL, query, timeout.toSeconds());
  }

  /**
   *
   * @param epURL HTTP SPARQL endpoint URI
   * @param query a valid SPARQL query
   * @param timeout in seconds
   * @return
   * @throws Exception
   */
  public static QueryExecution getExecution(String epURL, String query, long timeout) {
    log.debug("INIT QueryExecution for {} with query  {}", epURL, query.replaceAll("\n", ""));
    QueryExecutionHTTP qe = QueryExecution.service(epURL, query);
    if (timeout != -1) {
      qe.getContext().set(ARQ.httpQueryTimeout, timeout);
    }
    qe.getContext().set(ARQ.httpRequestModifer, USER_AGENT_REQUEST_MODIFIER);
    return qe;
  }

  public static class UserAgentRequestModifier implements HttpRequestModifier {
    @Override
    public void modify(Params params, Map<String, String> httpHeaders) {
      httpHeaders.put("User-Agent", "Mozilla/5.0 (compatible; SPARQLES/0.3.0; +https://sparqles.sv.berezovskyi.me)");
    }
  }
}
