/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.inject.Inject;
import com.google.sitebricks.Show;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jiakuanwang
 */
@Show("Template.html")
public abstract class Template {

  @Inject
  private HttpServletRequest request;

  public String getContextPath() {
    return request.getContextPath();
  }

  public String getHomeLinkStyle() {
    return "";
  }

  public String getCustomerLinkStyle() {
    return "";
  }

  public String getFixLicenseLinkStyle() {
    return "";
  }
}