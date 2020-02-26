package org.greports.services;

import org.apache.log4j.Logger;

public class LoggerService {

    private Logger logger;
    private boolean enabled;

    public LoggerService(Class clazz, boolean enabled) {
        this.logger = Logger.getLogger(clazz);
        this.enabled = enabled;
    }

    public static LoggerService forClass(Class clazz, boolean enabled) {
        return new LoggerService(clazz, enabled);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void trace(Object message){
        if(this.enabled) this.logger.trace(message);
    }

    public void trace(Object message, Throwable cause){
        if(this.enabled) this.logger.trace(message, cause);
    }

    public void info(Object message){
        if(this.enabled) this.logger.info(message);
    }

    public void info(Object message, Throwable cause){
        if(this.enabled) this.logger.info(message, cause);
    }

    public void debug(Object message) {
        if(this.enabled) this.logger.debug(message);
    }

    public void debug(Object message, Throwable cause) {
        if(this.enabled) this.logger.debug(message, cause);
    }

    public void error(Object message) {
        if(this.enabled) this.logger.error(message);
    }

    public void error(Object message, Throwable cause) {
        if(this.enabled) this.logger.error(message, cause);
    }

    public void fatal(Object message) {
        if(this.enabled) this.logger.fatal(message);
    }

    public void fatal(Object message, Throwable cause) {
        if(this.enabled) this.logger.fatal(message, cause);
    }
}
