package sparqles.utils;

import sparqles.core.CONSTANTS;

public class StringUtils {

  public static String stringCutoff(String val) {
    return stringCutoff(val, CONSTANTS.STRING_LEN);
  }

  public static String stringCutoff(String val, int cutoff) {
    if (val == null) return null;
    else if (val.length() < cutoff + 3) return val;
    else {
      return val.substring(0, cutoff) + "...";
    }
  }
}
