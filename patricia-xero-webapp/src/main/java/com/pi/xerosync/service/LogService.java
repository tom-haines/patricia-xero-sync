/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jiakuanwang
 */
@At("/log")
@Service
public class LogService {

  private static final Logger log = LoggerFactory.getLogger(LogService.class);
  @Inject
  private HttpServletRequest request;

  @Get
  public Reply<String> get() {
    String runId = request.getParameter("runId");
    if (StringUtils.isBlank(runId)) {
      return Reply.with(StringUtils.EMPTY);
    }

    LogBus logBus = LogBus.get(runId);
    synchronized (logBus.getLock()) {
      if (logBus.size() > 0) {
        log.debug("Found logs from cache, size={}", logBus.size());
        StringBuffer logs = new StringBuffer();
        for (String log : logBus) {
          logs.append(log).append("\n");
        }

        // Clean log cache after get all logs
        logBus.clear();
        return Reply.with(logs.toString());
      }
    }
    return Reply.with(StringUtils.EMPTY);
  }
}
