/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.util;

import net.oauth.client.OAuthClient;
import net.oauth.client.URLConnectionClient;
import net.oauth.http.HttpClient;

/**
 * @author jiakuanwang
 */
public class TimeoutOAuthClient extends OAuthClient {

  public TimeoutOAuthClient() {
    super(new URLConnectionClient());

    getHttpParameters().put(HttpClient.CONNECT_TIMEOUT, 120 * 1000); // 3 minutes
    getHttpParameters().put(HttpClient.READ_TIMEOUT, 120 * 1000); // 3 minutes
  }
}
