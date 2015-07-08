package com.pi.xerosync.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author thomas.haines.
 */
public class Settings {

  public enum SettingsKey {
    // Required env variables to run the sync app
    PATRICIA_DB_JDBC_URL,
    PATRICIA_DB_SQL_DRIVER,
    PATRICIA_DB_USERNAME,
    PATRICIA_DB_PASSWORD,
    XERO_CONSUMER_KEY,
    XERO_CONSUMER_SECRET,
    XERO_PRIVATE_KEY_PATH,
  }

  public static String getSetting(SettingsKey key) {
    String keyVal = System.getProperty(key.name());
    if (StringUtils.isBlank(keyVal)) {
      throw new UnsupportedOperationException("No value available for settings key " + key.name());
    }
    return keyVal;
  }

}
