package com.pi.xerosync.dbconnect;

import com.pi.xerosync.util.Settings;

/**
 * User: thomas Date: 18/02/14
 */
public class ConnectionManagerImpl extends ConnectionManagerBase implements ConnectionManager {

  @Override
  String getJdbcUrl() {
    return Settings.getSetting(Settings.SettingsKey.PATRICIA_DB_JDBC_URL);
  }

  @Override
  String getJdbcDriver() {
    return Settings.getSetting(Settings.SettingsKey.PATRICIA_DB_SQL_DRIVER);
  }

  @Override
  String getUsername() {
    return Settings.getSetting(Settings.SettingsKey.PATRICIA_DB_USERNAME);
  }

  @Override
  String getPassword() {
    return Settings.getSetting(Settings.SettingsKey.PATRICIA_DB_PASSWORD);
  }

}
