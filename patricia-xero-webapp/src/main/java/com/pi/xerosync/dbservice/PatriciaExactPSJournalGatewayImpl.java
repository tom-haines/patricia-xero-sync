package com.pi.xerosync.dbservice;

import com.pi.xerosync.common.JournalLine;
import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.dbconnect.ConnectionManager;
import com.pi.xerosync.service.LogBus;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.qos.logback.classic.Level;

/**
 * User: thomas Date: 17/02/14
 */
@Singleton
public class PatriciaExactPSJournalGatewayImpl implements PatriciaExactPSJournalGateway {

  private static final Logger
      log =
      LoggerFactory.getLogger(PatriciaExactPSJournalGatewayImpl.class);

  @Inject
  private ConnectionManager dbConnectManager;

  @Override
  public List<Integer> getListOfInvoiceNumbersToSync() throws IOException, SQLException {
    Connection conn = dbConnectManager.getDataSource().getConnection();
    try {
      QueryRunner run = new QueryRunner();
      ResultSetHandler<List<Integer>> handler = new ResultSetHandler<List<Integer>>() {
        @Override
        public List<Integer> handle(ResultSet rs) throws SQLException {
          List<Integer> arrayList = new ArrayList<>();
          while (rs.next()) {
            arrayList.add(rs.getInt(1));
          }
          return arrayList;
        }
      };

      String
          sql =
          "SELECT DISTINCT PSJ_ENTRY_NUMBER FROM dbo.EXACT_P_S_JOURNAL WHERE PSJ_EXPORTED_DATE IS NULL";

      if (System.getProperty("limit.to.invoice.no") != null) {
        sql =
            "SELECT DISTINCT PSJ_ENTRY_NUMBER FROM dbo.EXACT_P_S_JOURNAL WHERE PSJ_ENTRY_NUMBER="
            + System.getProperty("limit.to.invoice.no");
      }

      List<Integer> pendingInvoiceList = run.query(conn,
                                                   sql,
                                                   handler);

      return pendingInvoiceList;
    } finally {
      DbUtils.close(conn);
    }
  }

  @Override
  public void markInvoiceCompleted(String invoiceNumber, LogBus logBus)
      throws IOException, SQLException {
    Connection conn = dbConnectManager.getDataSource().getConnection();
    try {
      log.info("Marking " + invoiceNumber + " as completed");
      logBus.log(Level.INFO, "Marking " + invoiceNumber + " as completed");
      PreparedStatement
          setAsProcessed =
          conn.prepareStatement(
              "UPDATE dbo.EXACT_P_S_JOURNAL SET PSJ_EXPORTED_DATE=? WHERE PSJ_ENTRY_NUMBER=?");
      setAsProcessed.setDate(1, new java.sql.Date((new java.util.Date()).getTime()));
      setAsProcessed.setString(2, invoiceNumber);
      setAsProcessed.executeUpdate();
    } finally {
      DbUtils.close(conn);
    }
  }

  public String getTeamLeader(Connection conn, Integer caseId) throws IOException, SQLException {
    // SELECT * FROM dbo.TEAM_EFFORT INNER JOIN dbo.WORK_GROUP ON ( dbo.TEAM_EFFORT.TEAM_ID = dbo.WORK_GROUP.TEAM_ID ) WHERE IS_TEAM_LEADER=1 AND dbo.WORK_GROUP.CASE_ID=2504;
    QueryRunner run = new QueryRunner();
    ResultSetHandler<String> h = new ResultSetHandler<String>() {
      @Override
      public String handle(ResultSet rs) throws SQLException {
        if (!rs.next()) {
          return null;
        } else {
          return rs.getString(1);
        }
      }
    };
    return run.query(conn,
                     "SELECT dbo.TEAM_EFFORT.LOGIN_ID FROM dbo.TEAM_EFFORT INNER JOIN dbo.WORK_GROUP ON ( dbo.TEAM_EFFORT.TEAM_ID = dbo.WORK_GROUP.TEAM_ID ) WHERE IS_TEAM_LEADER=1 AND dbo.WORK_GROUP.CASE_ID=?",
                     h, caseId);
  }

  @Override
  public Integer getRemainingCount() throws IOException, SQLException {
    Connection conn = dbConnectManager.getDataSource().getConnection();
    try {
      QueryRunner run = new QueryRunner();
      ResultSetHandler<Integer> h = new ResultSetHandler<Integer>() {
        @Override
        public Integer handle(ResultSet rs) throws SQLException {
          if (!rs.next()) {
            return 0;
          }
          int count = 1;
          while (rs.next()) {
            count++;
          }
          return count;
        }
      };
      Integer remainingCountResult = run
          .query(
              conn,
              "SELECT DISTINCT PSJ_ENTRY_NUMBER FROM dbo.EXACT_P_S_JOURNAL WHERE PSJ_EXPORTED_DATE IS NULL",
              h);
      if (remainingCountResult == null) {
        log.error("SQL check pending count failed by returning null");
        return 0;
      } else {
        return remainingCountResult;
      }
    } finally {
      DbUtils.close(conn);
    }
  }

  @Override
  public List<JournalLine> getJournalLines(Integer invoiceNum, @Nullable final LogBus logBus)
      throws IOException, SQLException {
    final Connection conn = dbConnectManager.getDataSource().getConnection();
    try {
      QueryRunner run = new QueryRunner();

      ResultSetHandler<List<JournalLine>> h2 = new ResultSetHandler<List<JournalLine>>() {
        @Override
        public List<JournalLine> handle(ResultSet results) throws SQLException {
          List<JournalLine> journalLineList = new ArrayList<>();
          while (results.next()) {
            JournalLine journalLine = new JournalLine();
            journalLine.psj_journal_type = results.getString("psj_journal_type");
            journalLine.psj_line_number = results.getInt("psj_line_number");
            journalLine.psj_journal_number = results.getInt("psj_journal_number");
            journalLine.psj_entry_number = results.getString("psj_entry_number");
            journalLine.psj_description = results.getString("psj_description");
            journalLine.psj_comment = results.getString("psj_comment");

            // Format the Patricia date format to a Date object
            SimpleDateFormat formatPatriciaDate = new SimpleDateFormat("yyyyMMdd");
            try {
              Date jDate = formatPatriciaDate.parse("20" + results.getString("psj_date"));
              journalLine.psj_date = LocalDate.fromDateFields(jDate);
            } catch (ParseException e) {
              throw new SQLException("Error parsing date " + "20" + results.getString("psj_date"));
            }

            journalLine.psj_general_ledger_account =
                results.getString("psj_general_ledger_account");
            journalLine.psj_debtor_number = results.getString("psj_debtor_number");
            journalLine.psj_workcode = results.getString("psj_workcode");
            journalLine.psj_creditor_number = results.getString("psj_creditor_number");
            journalLine.psj_currency = results.getString("psj_currency");

            String exchangeRate = results.getString("psj_exchange_rate");
            if (StringUtils.isNotBlank(exchangeRate)) {
              journalLine.psj_exchange_rate = new BigDecimal(exchangeRate);
            }

            boolean isOutgoing = StringUtils.isNotBlank(journalLine.psj_debtor_number);
            log.debug("Invoice {} isOutgoing=" + isOutgoing, journalLine.psj_entry_number);

            if (StringUtils.isNotBlank(results.getString("psj_amount"))) {
              journalLine.psj_amount = new BigDecimal(results.getString("psj_amount"));
            }

            if (StringUtils.isNotBlank(results.getString("psj_amount_p_c_d"))) {
              journalLine.psj_amount_p_c_d = new BigDecimal(results.getString("psj_amount_p_c_d"));
            }
            if (StringUtils.isNotBlank(results.getString("psj_vat_amount"))) {
              journalLine.psj_vat_amount = new BigDecimal(results.getString("psj_vat_amount"));
            }

            journalLine.psj_due_date_invoice = results.getString("psj_due_date_invoice");
            journalLine.psj_due_date_payment = results.getString("psj_due_date_payment");

            try {
              journalLine.psj_vat_code = results.getInt("psj_vat_code");
            } catch (Exception e) {
              log.info("Could not convert psj_vat_code to int, string val was " + results
                  .getString("psj_vat_code"));
              logBus.log(Level.WARN, "Encountered an unknown psj_vat_code of " + results
                  .getString("psj_vat_code") + ".  "
                                     + "Syncing as NO GST, but should be manually checked.");
            }

            journalLine.setPsj_payment_ref(results.getString("psj_payment_ref"));
            journalLine.psj_cost_center = results.getString("psj_cost_center");
            journalLine.psj_loginid = results.getString("psj_loginid");
            journalLine.psj_case_id = results.getInt("psj_case_id");

            if (journalLine.psj_case_id != null && journalLine.psj_case_id > 0) {
              try {
                journalLine.teamLeader = getTeamLeader(conn, journalLine.psj_case_id);
              } catch (IOException e) {
                log.warn(e.getMessage(), e);
              }
            }

            journalLine.psj_cost_unit = results.getString("psj_cost_unit");

            // Get the quantity, and handle the possible exception of the field having a null or empty string value
            // in which case the default value of zero is used
            String quantity = results.getString("psj_quantity");
            if (quantity != null && quantity.length() > 0) {
              journalLine.psj_quantity = Double.parseDouble(quantity);
            }

            journalLine.psj_workcode_type = results.getString("psj_workcode_type");
            journalLine.psj_journal_type = results.getString("psj_journal_type");
            journalLine.psj_cost_category = results.getInt("psj_cost_category");

            journalLineList.add(journalLine);
          }

          return journalLineList;
        }
      };

      final List<JournalLine> journalLines =
          run.query(conn, "SELECT * FROM dbo.exact_p_s_journal WHERE PSJ_ENTRY_NUMBER=?", h2,
                    invoiceNum);
      return journalLines;
    } finally {
      DbUtils.close(conn);
    }
  }

  @Override
  public PatriciaInvoice getPatriciaInvoice(Integer invoiceNum, @Nullable LogBus logBus)
      throws IOException, SQLException {
    return new PatriciaInvoice(getJournalLines(invoiceNum, logBus));
  }

}
