/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.misc;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class RoundingTest {

  private static final Logger log = LoggerFactory.getLogger(RoundingTest.class);

  @Test
  public void testRounding() {
    log.debug("12.390 => {}",
              new BigDecimal("12.390").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.391 => {}",
              new BigDecimal("12.391").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.392 => {}",
              new BigDecimal("12.392").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.393 => {}",
              new BigDecimal("12.393").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.394 => {}",
              new BigDecimal("12.394").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.395 => {}",
              new BigDecimal("12.395").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.396 => {}",
              new BigDecimal("12.396").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.397 => {}",
              new BigDecimal("12.397").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.398 => {}",
              new BigDecimal("12.398").setScale(2, BigDecimal.ROUND_HALF_UP));
    log.debug("12.399 => {}",
              new BigDecimal("12.399").setScale(2, BigDecimal.ROUND_HALF_UP));
  }
}
