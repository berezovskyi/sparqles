package sparqles.core;

public class CONSTANTS {
  /** Default length at which most strings will be cut off (in logs, message strings etc.) */
  public static final int STRING_LEN = 1024;

  private static final String SPARQLES_VERSION = "0.3.0";
  public static final String DEFAULT_HOST = "http://sparqles.okfn.org/";

  /** Availability task */
  public static final String ATASK = "ATask";

  /** Performance task */
  public static final String PTASK = "PTask";

  /** Discoverability task */
  public static final String DTASK = "DTask";

  /** Interoperability task */
  public static final String FTASK = "FTask";

  /** Index view refresh task */
  public static final String ITASK = "ITask";

  /** Endpoint list refresh task (from Datahub) */
  public static final String ETASK = "ETask";

  /** Coherence task (proposed measure by the 3DFed project) */
  public static final String CTASK = "CTask";

  /** Used only for robots.txt */
  public static final String USER_AGENT_STRING_RAW = "SPARQLESbot";

  public static final String USER_AGENT_STRING =
      "Mozilla/5.0 (compatible; SPARQLESbot/" + SPARQLES_VERSION + "; +%s)";

  @Deprecated public static final int SOCKET_TIMEOUT = 16 * 1000;
  @Deprecated public static final int CONNECTION_TIMEOUT = 16 * 1000;

  public static final String ANY_RDF_MIME_ACCEPT =
      "application/rdf+xml, application/x-turtle, application/rdf+n3, application/xml, text/turtle,"
          + " text/rdf, text/plain;q=0.1";
}
