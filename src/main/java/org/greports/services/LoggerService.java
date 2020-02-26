package org.greports.services;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LoggerService {

    private Logger logger;
    private Level level;
    private boolean enabled;

    public LoggerService(Class<?> clazz, boolean enabled) {
        this(clazz, enabled, Level.ALL);
    }

    public LoggerService(Class<?> clazz, boolean enabled, Level level) {
        this.logger = Logger.getLogger(clazz);
        this.enabled = enabled;
        this.level = level;
    }

    public static LoggerService forClass(Class<?> clazz, boolean enabled, Level level) {
        return new LoggerService(clazz, enabled, level);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Level getLevel() {
        return level;
    }

    public void trace(Object message) {
        if(this.enabled && level.isGreaterOrEqual(Level.TRACE)) this.logger.trace(message);
    }

    public void trace(Object message, Throwable cause) {
        if(this.enabled && level.isGreaterOrEqual(Level.TRACE)) this.logger.trace(message, cause);
    }

    public void info(Object message){
        if(this.enabled && level.isGreaterOrEqual(Level.INFO)) this.logger.info(message);
    }

    public void info(Object message, Throwable cause){
        if(this.enabled && level.isGreaterOrEqual(Level.INFO)) this.logger.info(message, cause);
    }

    public void debug(Object message) {
        if(this.enabled && level.isGreaterOrEqual(Level.DEBUG)) this.logger.debug(message);
    }

    public void debug(Object message, Throwable cause) {
        if(this.enabled && level.isGreaterOrEqual(Level.DEBUG)) this.logger.debug(message, cause);
    }

    public void error(Object message) {
        if(this.enabled && level.isGreaterOrEqual(Level.ERROR)) this.logger.error(message);
    }

    public void error(Object message, Throwable cause) {
        if(this.enabled && level.isGreaterOrEqual(Level.ERROR)) this.logger.error(message, cause);
    }

    public void fatal(Object message) {
        if(this.enabled && level.isGreaterOrEqual(Level.FATAL)) this.logger.fatal(message);
    }

    public void fatal(Object message, Throwable cause) {
        if(this.enabled && level.isGreaterOrEqual(Level.FATAL)) this.logger.fatal(message, cause);
    }
}
