package com.pi.xerosync.dbconnect;

import com.jolbox.bonecp.BoneCPDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.sql.DataSource;

/**
 * User: thomas Date: 18/02/14
 */
public abstract class ConnectionManagerBase  {

  private DataSource dataSource = null;

  abstract String getJdbcDriver();

  abstract String getJdbcUrl();

  abstract String getUsername();

  abstract String getPassword();

  public DataSource getDataSource() throws IOException {
    if (dataSource == null) {
      // More information please see http://jolbox.com/
      dataSource = new BoneCPDataSource();
      ((BoneCPDataSource) dataSource).setDriverClass(getJdbcDriver());
      ((BoneCPDataSource) dataSource).setJdbcUrl(getJdbcUrl());
      ((BoneCPDataSource) dataSource).setUsername(getUsername());
      ((BoneCPDataSource) dataSource).setPassword(getPassword());
    }
    return dataSource;
  }

}
