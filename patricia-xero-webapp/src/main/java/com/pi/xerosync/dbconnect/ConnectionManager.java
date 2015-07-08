package com.pi.xerosync.dbconnect;

import java.io.IOException;

import javax.sql.DataSource;

/**
 * User: thomas Date: 17/02/14
 */
public interface ConnectionManager {
  public DataSource getDataSource() throws IOException;
}
