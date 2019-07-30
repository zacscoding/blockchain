package demo.common;

import static java.lang.String.format;

/**
 *
 */
public class TestHelper {

    public static void out(String format, Object... args) {
        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();
    }
}
