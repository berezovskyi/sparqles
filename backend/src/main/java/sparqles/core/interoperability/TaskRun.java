package sparqles.core.interoperability;

import java.util.Iterator;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.sparql.util.FmtUtils;
import org.slf4j.Logger;
import sparqles.avro.Endpoint;
import sparqles.avro.performance.Run;
import sparqles.utils.*;

public abstract class TaskRun {

  public static final long FIRST_RESULT_TIMEOUT = 60 * 1000;
  public static final long EXECUTION_TIMEOUT = 15 * 60 * 1000;
  public static final long A_FIRST_RESULT_TIMEOUT = 10 * 1000;
  //  public static final long A_EXECUTION_TIMEOUT = 60 * 1000;
  protected FileManager _fm;
  protected String _query;
  protected String _queryFile;
  protected Endpoint _ep;
  private Long _start;
  private Logger log;

  public TaskRun(Endpoint ep, String queryFile, String queryRoot, Long start, Logger log) {
    _queryFile = queryFile;
    this.log = log;

    _query = QueryManager.getQuery(queryRoot, queryFile);
    _ep = ep;
    _start = start;
  }

  public String getQuery() {
    return _query;
  }

  protected Run run() {

    Run r = new Run();
    r.setFrestout(FIRST_RESULT_TIMEOUT);
    r.setExectout(EXECUTION_TIMEOUT);

    long b4 = 0;
    long cnxion = 0;
    int sols = 0;
    try {
      b4 = System.currentTimeMillis();

      Query q = QueryFactory.create(this._query);
      QueryExecution qexec = QueryManager.getExecution(_ep, _query);

      qexec.getContext().set(ARQ.httpQueryTimeout, FIRST_RESULT_TIMEOUT);
      cnxion = System.currentTimeMillis();

      if (q.isSelectType()) {

        ResultSet results = qexec.execSelect();
        if (_fm != null) sols = _fm.writeSPARQLResults(results, _queryFile, _ep, _start);
        else {
          sols = skipSPARQLResults(results, _queryFile, _ep, _start);
        }

      } else if (q.isAskType()) {
        boolean result = qexec.execAsk();
        if (result) sols = 1;
        else sols = -1;
      } else if (q.isDescribeType()) {
        Iterator<Triple> triples = qexec.execDescribeTriples();

        if (_fm != null) sols = _fm.writeSPARQLResults(triples, _queryFile, _ep, _start);
        else {
          sols = skipSPARQLResults(triples, _queryFile, _ep, _start);
        }

      } else if (q.isConstructType()) {
        Iterator<Triple> triples = qexec.execConstructTriples();
        if (_fm != null) sols = _fm.writeSPARQLResults(triples, _queryFile, _ep, _start);
        else {
          sols = skipSPARQLResults(triples, _queryFile, _ep, _start);
        }
      } else {
        throw new UnsupportedOperationException(
            "What query is this? (Not SELECT|ASK|DESCRIBE|CONSTRUCT). " + q);
      }

      long iter = System.currentTimeMillis();
      qexec.close();
      long close = System.currentTimeMillis();

      r.setSolutions(sols);
      r.setInittime((cnxion - b4));
      r.setExectime((iter - b4));
      r.setClosetime((close - b4));

      //            System.out.println( _ep.getUri() + "\t" + sols
      //                    + "\t" + (cnxion - b4) + "\t" + (iter - b4) + "\t"
      //                    + (close - b4));
    } catch (Exception e) {
      var faultKind = FaultDiagnostic.faultKindForJenaQuery(e);
      if (faultKind == FaultKind.UNKNOWN) {
        //        String ex = ExceptionHandler.logAndtoString(e);
        r.setException(StringUtils.trunc(e.getMessage()));
        log.warn(
            "Unknown error encountered while attempting a SPARQL query (type={})",
            e.getClass().getName());
        log.debug("Stacktrace", e);
      } else {
        r.setException(FaultDiagnostic.interpretFault(faultKind));
      }
    }

    return r;
  }

  public void setFileManager(FileManager fm) {
    _fm = fm;
  }

  public int skipSPARQLResults(
      Iterator<Triple> triples, String queryFile, Endpoint ep, Long start) {

    int sols = 0;
    while (triples.hasNext()) {
      triples.next();
      sols++;
    }
    return sols;
  }

  public int skipSPARQLResults(ResultSet results, String queryFile, Endpoint ep, Long start) {

    int sols = 0;
    while (results.hasNext()) {
      QuerySolution qs = results.nextSolution();
      //	            toString(qs, sols == 0);
      sols++;
    }

    return sols;
  }

  private static String toString(QuerySolution qs, boolean first) {
    StringBuffer vars = new StringBuffer();
    StringBuffer sb = new StringBuffer();
    Iterator<String> varns = qs.varNames();
    while (varns.hasNext()) {
      String varName = varns.next();
      if (first) {
        vars.append(varName + "\t");
      }
      sb.append(FmtUtils.stringForObject(qs.get(varName)) + "\t");
    }

    if (first) return vars.toString() + "\n" + sb.toString();
    return sb.toString();
  }
}
