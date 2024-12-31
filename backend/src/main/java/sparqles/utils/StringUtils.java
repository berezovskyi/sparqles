package sparqles.utils;

import sparqles.core.CONSTANTS;

public class StringUtils {

  public static String trunc(String val) {
    return trunc(val, CONSTANTS.STRING_LEN);
  }

  public static String trunc(String val, int cutoff) {
    if (val == null) return null;
    else if (val.length() < cutoff + 3) return val;
    else {
      return val.substring(0, cutoff) + "...";
    }
  }
}
