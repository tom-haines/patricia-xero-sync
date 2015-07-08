/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.rendering.Decorated;

import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import ch.qos.logback.classic.Level;

/**
 * @author jiakuanwang
 */
@At("/")
@Decorated
public class Home extends Template {

  private static final Logger log = LoggerFactory.getLogger(Home.class);
  @Inject
  private HttpServletRequest request;
  @Inject
  private PatriciaExactPSJournalGateway patriciaExactPSJournalGateway;

  private Integer remainingCount = -1;
  private String errorMessage;
  private String runId = StringUtils.EMPTY;

  @Get
  public void get() {
    if (StringUtils.isNotBlank(request.getParameter("runId"))) {
      runId = request.getParameter("runId");
    }
    log.info("Home page is opened, runId: {}", runId);
    try {
      remainingCount = patriciaExactPSJournalGateway.getRemainingCount();
    } catch (IOException | SQLException e) {
      LogBus.get(runId).log(Level.ERROR,e.getMessage());
      errorMessage = e.getMessage();
      log.error(e.getMessage(),e);
    }
  }

  @Override
  public String getHomeLinkStyle() {
    return "active";
  }

  public Integer getRemainingCount() {
    return remainingCount;
  }

  public String getRunId() {
    return runId;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getContextPath() {
    return request.getContextPath();
  }
}
