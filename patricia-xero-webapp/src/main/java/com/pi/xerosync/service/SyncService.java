/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.headless.Reply;
import com.google.sitebricks.headless.Service;
import com.google.sitebricks.http.Get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jiakuanwang
 */
@At("/sync")
@Service
public class SyncService {

  private static final Logger log = LoggerFactory.getLogger(SyncService.class);

  @Inject
  private HttpServletRequest request;

  @Inject
  private SyncManager syncManager;

  @Get
  public Reply<?> get() {
    final String runId = UUID.randomUUID().toString();
    new Thread() {
      public void run() {
        log.info("Started sync in a new thread.");
        syncManager.syncRecords(LogBus.get(runId));
      }

      ;
    }.start();
    String url = request.getContextPath() + "?runId=" + runId;
    log.info("Redirect to: {}", url);
    return Reply.saying().redirect(url);
  }
}
