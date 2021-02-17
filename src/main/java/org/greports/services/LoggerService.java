package org.greports.services;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerService {

    private final Logger logger;
    private final Level level;
    private final boolean enabled;

    public LoggerService(Class<?> clazz, boolean enabled, Level level) {
        this.logger = LogManager.getLogger(clazz);
        Configurator.setLevel("org.greports", level);
        this.level = level;
        this.enabled = enabled;
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
        this.trace(message, true);
    }

    public void trace(Object message, boolean printIfTrue) {
        if(this.enabled && printIfTrue) this.logger.trace(message);
    }

    public void trace(Object message, Throwable cause) {
        if(this.enabled) this.logger.trace(message, cause);
    }

    public void debug(Object message) {
        this.debug(message, true);
    }

    public void debug(Object message, boolean printIfTrue) {
        if(this.enabled && printIfTrue) this.logger.debug(message);
    }

    public void debug(Object message, Throwable cause) {
        if(this.enabled) this.logger.debug(message, cause);
    }

    public void info(Object message) {
        this.info(message, true);
    }

    public void info(Object message, boolean printIfTrue){
        if(this.enabled && printIfTrue) this.logger.info(message);
    }

    public void info(Object message, Throwable cause){
        if(this.enabled) this.logger.info(message, cause);
    }

    public void warn(Object message){
        this.warn(message, true);
    }

    public void warn(Object message, boolean printIfTrue){
        if(this.enabled && printIfTrue) this.logger.warn(message);
    }

    public void warn(Object message, Throwable cause){
        if(this.enabled) this.logger.warn(message, cause);
    }

    public void error(Object message) {
        this.error(message, true);
    }

    public void error(Object message, boolean printIfTrue) {
        if(this.enabled && printIfTrue) this.logger.error(message);
    }

    public void error(Object message, Throwable cause) {
        if(this.enabled) this.logger.error(message, cause);
    }

    public void fatal(Object message) {
        this.fatal(message, true);
    }

    public void fatal(Object message, boolean printIfTrue) {
        if(this.enabled && printIfTrue) this.logger.fatal(message);
    }

    public void fatal(Object message, Throwable cause) {
        if(this.enabled) this.logger.fatal(message, cause);
    }
}
