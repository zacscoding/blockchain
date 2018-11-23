package org.demo.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zacconding
 * @Date 2018-05-02
 * @GitHub : https://github.com/zacscoding
 */
public class LogLevelUtil {

    public static void setOff() {
        setRootLevel(Level.OFF);
    }

    public static void setError() {
        setRootLevel(Level.ERROR);
    }

    public static void setWarn() {
        setRootLevel(Level.WARN);
    }

    public static void setInfo() {
        setRootLevel(Level.INFO);
    }

    public static void setDebug() {
        setRootLevel(Level.DEBUG);
    }

    public static void setTrace() {
        setRootLevel(Level.TRACE);
    }

    public static void setAll() {
        setRootLevel(Level.ALL);
    }

    public static void setRootLevel(Level level) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }
}
