package com.pi.xerosync.util;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: thomas Date: 18/02/14
 */
public class XeroDbUtils {

  public static final Logger log = LoggerFactory.getLogger(XeroDbUtils.class);

  public static void cleanupConnection(@Nullable Connection conn, @Nullable ResultSet rs, @Nullable Statement statement) {
    closeQuietly(statement);
    closeQuietly(rs);
    closeQuietly(conn);
  }

  public static void closeQuietly(@Nullable ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        log.info(e.getMessage());
      }
    }
  }

  public static void closeQuietly(@Nullable Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        log.info(e.getMessage());
      }
    }
  }

  public static void closeQuietly(@Nullable Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        log.info(e.getMessage());
      } finally {
        try {
          conn.close();
        } catch (SQLException e) {
          log.info(e.getMessage());
        }
      }
    }
  }
}
