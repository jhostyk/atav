package function.external.kaviar;

import global.Data;

/**
 *
 * @author nick
 */
public class KaviarCommand {

    public static boolean isListKaviar = false;
    public static boolean isIncludeKaviar = false;
    public static float maxKaviarMaf = Data.NO_FILTER;
    public static int maxKaviarAlleleCount = Data.NO_FILTER;

    public static boolean isMaxMafValid(float value) {
        if (maxKaviarMaf == Data.NO_FILTER) {
            return true;
        }

        return value <= maxKaviarMaf
                || value == Data.FLOAT_NA;
    }

    public static boolean isMaxAlleleCountValid(int value) {
        if (maxKaviarAlleleCount == Data.NO_FILTER) {
            return true;
        }

        return value <= maxKaviarAlleleCount
                || value == Data.INTEGER_NA;
    }
}
