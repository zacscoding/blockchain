package org.web3jtest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple Logger for dev...
 * @author zacconding
 * @Date 2018-01-14
 * @GitHub : https://github.com/zacscoding
 */
public class SimpleLogger {

    public static String NEW_LINE;
    private static PrintStream PS;
    public static SimpleDateFormat SIMPLE_DATE_FORMAT;

    static {
        NEW_LINE = System.getProperty("line.separator");
        if (NEW_LINE == null || NEW_LINE.length() == 0) {
            NEW_LINE = "\n";
        }
        SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyMMdd HH:mm:ss.SSS");
        PS = System.out;
    }

    public static void print(String message, Object... args) {
        StringBuilder sb = new StringBuilder();
        parseContent(sb, message, args);
        PS.print(sb.toString());
    }

    public static void println(String message, Object... args) {
        print(message, args);
        PS.println();
    }

    public static void info(String message, Object... args) {
        String prefix = SIMPLE_DATE_FORMAT.format(new Date()) + " : " + getClassName() + " ";
        println(prefix + message, args);
    }

    public static void error(Throwable t) {
        error(null, t);
    }

    public static void error(String message, Throwable t) {
        String prefix = SIMPLE_DATE_FORMAT.format(new Date()) + " [ERROR] " + getClassName() + " : ";
        println(prefix + (message == null ? "" : message));
        if (t != null) {
            t.printStackTrace(PS);
        }
    }

    public static String getStackTraceString(int cursor) {
        StackTraceElement[] elts = Thread.currentThread().getStackTrace();
        if (elts == null || elts.length == 1) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int start, size;
        if (cursor >= 0) {
            start = cursor + 2;
            size = elts.length;
        } else {
            start = 2;
            size = start - cursor + 1;
        }

        return getStackTraceString(elts, start, size);
    }

    public static String getStackTraceString(StackTraceElement[] se, int start, int size) {
        if (se == null) {
            return "";
        }

        if (size < 0) {
            size = 0;
        }
        size = Math.min(size, se.length);
        if (start >= size) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = start; i < size; i++) {
            sb.append("\t").append(se[i].toString());
            if (i != size - 1) {
                sb.append(NEW_LINE);
            }
        }

        return sb.toString();
    }

    public static String toJson(Object inst) {
        return toJson(new Gson(), inst);
    }

    public static String toJsonWithPretty(Object inst) {
        return toJson(new GsonBuilder().setPrettyPrinting().create(), inst);
    }

    public static void printJSON(Object inst) {
        PS.println(toJson(inst));
    }

    public static void printJSONPretty(Object inst) {
        PS.println(toJsonWithPretty(inst));
    }

    public static String toJson(Gson gson, Object inst) {
        if (gson == null || inst == null) {
            return "{}";
        }

        return gson.toJson(inst);
    }

    private static void parseContent(StringBuilder sb, String content, Object[] args) {
        if (args == null || args.length == 0 || content == null || content.length() < 2) {
            sb.append(content);
            return;
        }

        int argIdx = 0;
        int length = content.length();

        for (int i = 0; i < length; i++) {
            char curChar = content.charAt(i);
            if ((content.charAt(i) == '{') && (i + 1 < length) && (content.charAt(i + 1) == '}') && (isRange(args, argIdx))) {
                sb.append(args[argIdx++]);
                i++;
            } else {
                sb.append(curChar);
            }
        }
    }

    private static boolean isRange(Object[] array, int idx) {
        if (idx < 0 || array == null || array.length <= idx) {
            return false;
        }
        return true;
    }

    private static String getClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }


    public static SimpleLogger build() {
        return new SimpleLogger();
    }

    private StringBuilder sb;

    private SimpleLogger() {
        sb = new StringBuilder();
    }

    public SimpleLogger append(String message, Object... args) {
        parseContent(this.sb, message, args);
        return this;
    }

    public SimpleLogger appendln(String message, Object... args) {
        parseContent(this.sb, message, args);
        sb.append(NEW_LINE);
        return this;
    }

    public SimpleLogger appendTab(String message, Object... args) {
        appendTab(1, message, args);
        return this;
    }

    public SimpleLogger appendTab(int tabSize, String message, Object... args) {
        parseContent(this.sb, message, args);

        if (tabSize > 0) {
            for (int i = 0; i < tabSize; i++) {
                sb.append('\t');
            }
        }

        return this;
    }

    public SimpleLogger appendRepeat(int repeat, String message, Object... args) {
        if (repeat > 0) {
            for (int i = 0; i < repeat; i++) {
                parseContent(this.sb, message, args);
            }
        }

        return this;
    }

    public SimpleLogger newLine() {
        sb.append(NEW_LINE);
        return this;
    }

    public void flush(PrintStream ps) {
        if (ps != null) {
            ps.print(toString());
        }
    }

    public void flush() {
        flush(System.out);
    }

    @Override
    public String toString() {
        return sb == null ? "" : sb.toString();
    }

    /**
     * Simple Test from console...
     */
    public static void main(String[] args) {
        // this is print...this is println...
        SimpleLogger.print("this is print...");
        SimpleLogger.println("this is println...");
        // arg1 : 1, not arg1 : { , arg2 : test, not arg2 : }
        SimpleLogger.println("arg1 : {}, not arg1 : { , arg2 : {}, not arg2 : }", 1, "test");

        // 180322 23:14:16.513 [ERROR] SimpleLogger : test exception
        // java.lang.RuntimeException
        // at SimpleLogger.main(SimpleLogger.java:194)
        try {
            boolean temp = true;
            if (temp) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            SimpleLogger.error("test exception", e);
        }

        // this is append..this is appendln...
        // arg1 : 1, not arg1 : { , arg2 : test, not arg2 : }
        // after new line..
        SimpleLogger.build().append("this is append..").appendln("this is appendln...")
                    .append("arg1 : {}, not arg1 : { , arg2 : {}, not arg2 : }", 1, "test").newLine()
                    .appendln("after new line..").flush();

        // tab1	tab2
        SimpleLogger.build().appendTab("tab1").appendln("tab2").flush();
        // tab1			tab2
        SimpleLogger.build().appendTab(3, "tab1").appendln("tab2").flush();
        // ==================== test ====================
        SimpleLogger.build().appendRepeat(20, "=").append(" test ").appendRepeat(20, "=").flush();
    }
}