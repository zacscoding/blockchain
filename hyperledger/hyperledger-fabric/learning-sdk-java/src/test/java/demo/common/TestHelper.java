package demo.common;

/**
 *
 */
public class TestHelper {

    public static void out(String format, Object... args) {
        System.err.flush();
        System.out.flush();

        System.out.println(String.format(format, args));

        System.out.flush();
        System.err.flush();
    }
}
