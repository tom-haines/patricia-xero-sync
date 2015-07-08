/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import ch.qos.logback.classic.Level;

/**
 * @author jiakuanwang
 */
public class LogBus extends ArrayList<String> {

  private static final long serialVersionUID = 7445592209978889248L;
  private static final Logger log = LoggerFactory.getLogger(LogBus.class);

  // <runId, LogCache>
  private static final LoadingCache<String, LogBus> logBusCache = CacheBuilder
      .newBuilder().build(new CacheLoader<String, LogBus>() {
        @Override
        public LogBus load(String runId) throws Exception {
          return new LogBus(runId);
        }
      });

  private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
  private Object lock = new Object();
  private String runId;

  public LogBus(String runId) {
    this.runId = runId;
  }

  public synchronized static final LogBus get(String runId) {
    LogBus logCache = null;
    try {
      logCache = logBusCache.get(runId);
    } catch (ExecutionException e) {
      log.error(e.getMessage(), e);

      logCache = new LogBus(runId);
      logBusCache.put(runId, logCache);
    }
    return logCache;
  }

  public Object getLock() {
    return lock;
  }

  public void log(Level level, String message) {
    synchronized (lock) {
      add(new StringBuffer().append(LOG_DATE_FORMAT.format(new Date()))
              .append(" ").append(level.toString()).append(" - ").append(message)
              .toString());
      log.debug("Logging to cache: runId={}, cacheSiz={}, msg={}", runId,
                size(), message);
    }
  }
}
