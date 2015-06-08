
/**
 * Defines helper functions
 *
 * @author Boris Strandjev
 */
public class AuxiliaryFunctions {
    /** Returns the time in seconds. */
    public static double get_time() {
        return System.currentTimeMillis() / 1000.0;
    }

    /** Outputs the given string. */
    public static void print(String format, Object... args) {
        System.out.print(String.format(format, args));
    }
}
