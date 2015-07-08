/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import ch.qos.logback.classic.Level;

/**
 * @author jiakuanwang
 */
@At("/remainingcount")
@Service
public class RemainingCountService {

  private static final Logger log = LoggerFactory.getLogger(RemainingCountService.class);

  @Inject
  private HttpServletRequest request;

  @Inject
  private PatriciaExactPSJournalGateway patriciaExactPSJournalGateway;

  @Get
  public Reply<Integer> get() {
    String runId = StringUtils.EMPTY;
    if (StringUtils.isNotBlank(request.getParameter("runId"))) {
      runId = request.getParameter("runId");
    }
    Integer remainingCount = -1;
    try {
      remainingCount = patriciaExactPSJournalGateway.getRemainingCount();
    } catch (IOException | SQLException e) {
      LogBus.get(runId).log(Level.ERROR,e.getMessage());
      log.error(e.getMessage(),e);
    }
    return Reply.with(remainingCount);
  }
}