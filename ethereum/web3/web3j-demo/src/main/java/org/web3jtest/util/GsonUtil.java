package org.web3jtest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintStream;

/**
 * @author zacconding
 * @Date 2018-05-01
 * @GitHub : https://github.com/zacscoding
 */
public class GsonUtil {

    public static String toString(Object inst) {
        if (inst == null) {
            return "null";
        }

        return GsonFactory.createDefaultGson().toJson(inst);
    }

    public static String toStringPretty(Object inst) {
        if (inst == null) {
            return "{}";
        }

        return GsonFactory.createPrettyGson().toJson(inst);
    }

    public static void printGson(Object inst) {
        printGson(null, inst);
    }

    public static void printGson(PrintStream ps, Object inst) {
        if (ps == null) {
            ps = System.out;
        }
        ps.println(toString(inst));
    }

    public static void printGsonPretty(Object inst) {
        printGsonPretty(null, inst);
    }

    public static void printGsonPretty(PrintStream ps, Object inst) {
        if (ps == null) {
            ps = System.out;
        }

        ps.println(toStringPretty(inst));
    }

    public static class GsonFactory {

        public static Gson createDefaultGson() {
            return new GsonBuilder().serializeNulls().create();
        }

        public static Gson createPrettyGson() {
            return new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        }
    }
}