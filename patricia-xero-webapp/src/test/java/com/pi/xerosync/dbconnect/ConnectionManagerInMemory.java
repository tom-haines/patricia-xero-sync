package com.pi.xerosync.dbconnect;

import com.jolbox.bonecp.BoneCPDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Singleton;
import javax.sql.DataSource;

@Singleton
public class ConnectionManagerInMemory implements ConnectionManager {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private DataSource dataSource = null;

  public DataSource getDataSource() throws IOException {
    if (dataSource == null) {
      // More information please see http://jolbox.com/
      dataSource = new BoneCPDataSource();
      ((BoneCPDataSource) dataSource).setDriverClass("org.h2.Driver");
      ((BoneCPDataSource) dataSource).setJdbcUrl("jdbc:h2:mem:PATDB");
      ((BoneCPDataSource) dataSource).setUsername("PATDB");
      ((BoneCPDataSource) dataSource).setPassword("PATDB");

      // try to create the dbo schema
      try {
        dataSource.getConnection().prepareStatement("CREATE SCHEMA IF NOT EXISTS DBO").execute();
      } catch (SQLException e) {
        log.warn(e.getMessage());
      }
    }
    return dataSource;
  }

}
