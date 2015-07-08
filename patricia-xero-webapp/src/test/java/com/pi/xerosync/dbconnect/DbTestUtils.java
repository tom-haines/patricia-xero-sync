package com.pi.xerosync.dbconnect;

import com.pi.xerosync.util.XeroDbUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * User: thomas Date: 18/02/14
 */
public class DbTestUtils implements PatriciaExportSchema {
  public static final Logger log = LoggerFactory.getLogger(DbTestUtils.class);

  public static void dropCreateDatabase(DataSource dataSource) throws SQLException {
    // we only execute these commands for an in-memory test
    if (isH2(dataSource) || isDerby(dataSource)) {
      dropAndCreate(dataSource);
    } else {
      throw new UnsupportedOperationException("The database connection was not confirmed as an in-memory test.");
    }
  }

  public static void executeSql(DataSource dataSource, String sql) throws SQLException {
    // we only execute these commands for an in-memory test
    if (isH2(dataSource) || isDerby(dataSource)) {
      final Connection conn = dataSource.getConnection();
      Statement stmt = conn.createStatement();
      final int rows = stmt.executeUpdate(sql);
      XeroDbUtils.cleanupConnection(conn, null, stmt);
    } else {
      throw new UnsupportedOperationException("The database connection was not confirmed as an in-memory test.");
    }
  }

  private static void dropAndCreate(DataSource dataSource) throws SQLException {
    createEmptyTable(dataSource, PAT_NAMES_ADDRESS_TYPE_TABLE, CREATE_TAB_PAT_NAMES_ADDRESS_TYPE);
    createEmptyTable(dataSource, PAT_NAMES_TABLE, CREATE_TAB_PAT_NAMES);
    createEmptyTable(dataSource, PAT_NAMES_ADDRESS_TABLE, CREATE_TAB_PAT_NAMES_ADDRESS);
    createEmptyTable(dataSource, PAT_NAMES_ENTITY_TABLE, CREATE_TAB_PAT_NAMES_ENTITY);
    createEmptyTable(dataSource, EXACT_P_S_JOURNAL_TABLE, CREATE_TAB_EXACT_P_S_JOURNAL);
    createEmptyTable(dataSource, EXACT_ACTOR_TABLE, CREATE_TAB_EXACT_ACTOR);
    createEmptyTable(dataSource, PAT_CONNECTION_POOL_TABLE, CREATE_TAB_PAT_CONNECTION_POOL);
    createEmptyTable(dataSource, TEAM_EFFORT_TABLE, CREATE_TAB_TEAM_EFFORT);
    createEmptyTable(dataSource, WORK_GROUP_TABLE, CREATE_TAB_WORK_GROUP);
  }

  private static void createEmptyTable(DataSource dataSource, String tableName, String createTableStatement) throws SQLException {
    final Connection conn = dataSource.getConnection();
    DatabaseMetaData dbmd = conn.getMetaData();
    ResultSet rs = dbmd.getTables(null, null, tableName, null);
    PreparedStatement ps;
    if(!rs.next()) {
      log.debug(tableName + " table does not exists. Trying to create it.");
      ps = conn.prepareStatement(createTableStatement);
      ps.executeUpdate();
    } else {
      final String delStr = String.format("DELETE FROM %s.%s", SCHEMA_NAME, tableName);
      log.debug(tableName + " table exists. Clearing it's content using {}",delStr);
      ps = conn.prepareStatement(delStr);
      ps.executeUpdate();
    }
    XeroDbUtils.cleanupConnection(conn, rs, ps);
  }

  private static boolean isH2(DataSource dataSource) throws SQLException {
    DatabaseMetaData dbmd = dataSource.getConnection().getMetaData();
    final String prodName = dbmd.getDatabaseProductName();
    boolean h2 = "h2".equalsIgnoreCase(prodName);
    log.debug("name={}, isH2={}", prodName, h2);
    return h2;
  }

  private static boolean isDerby(DataSource dataSource) throws SQLException {
    DatabaseMetaData dbmd = dataSource.getConnection().getMetaData();
    final String prodName = dbmd.getDatabaseProductName();
    boolean derby = prodName.toLowerCase().indexOf("derby") > 0;
    log.debug("name={}, isDerby={}", prodName, derby);
    return derby;
  }
}
