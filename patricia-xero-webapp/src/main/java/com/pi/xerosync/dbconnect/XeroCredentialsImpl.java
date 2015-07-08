package com.pi.xerosync.dbconnect;

import com.pi.xerosync.util.Settings;

/**
 * User: thomas Date: 18/02/14
 */
public class XeroCredentialsImpl implements XeroCredentials {

  @Override
  public String getXeroConsumerKey() {
    return Settings.getSetting(Settings.SettingsKey.XERO_CONSUMER_KEY);
  }

  @Override
  public String getXeroConsumerSecret() {
    return Settings.getSetting(Settings.SettingsKey.XERO_CONSUMER_SECRET);
  }

  @Override
  public String getPrivateKeyPath() {
    return Settings.getSetting(Settings.SettingsKey.XERO_PRIVATE_KEY_PATH);
  }

}
