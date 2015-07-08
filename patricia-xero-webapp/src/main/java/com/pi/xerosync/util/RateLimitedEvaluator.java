/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * @author jiakuanwang
 */
public class RateLimitedEvaluator extends EventEvaluatorBase<ILoggingEvent> {

  private int maxEmails = 2; // default one mail limit per cycleSeconds.
  private int timeFrame = 5; // default: 5 minutes
  private int timeFrameInMillis = timeFrame * 60 * 1000;
  private long lastSentMail = -1;
  private int totalMailsSent;

  /**
   * The mails are spaced out for every time Frame. Eg: If 5 mails are to be sent in 5mins. 1 mail will be sent per minute.
   * Not all 5 mails will be sent at same time.
   */
  public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
    if (event.getLevel().levelInt >= Level.ERROR_INT) {
      if (lastSentMail + timeFrameInMillis > System.currentTimeMillis()) {
        totalMailsSent = 0;
      }

      long currentTime = System.currentTimeMillis();
      long timeSpacing = timeFrameInMillis / maxEmails;
      if (lastSentMail + timeSpacing < currentTime && totalMailsSent < maxEmails) {
        lastSentMail = currentTime;
        totalMailsSent++;
        return true;
      }
    }
    return false;
  }
}
