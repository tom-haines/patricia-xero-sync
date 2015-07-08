package com.pi.xerosync.dbservice;

import com.pi.xerosync.PatriciaDbAbstractTest;
import com.pi.xerosync.common.JournalLine;
import com.pi.xerosync.dbconnect.DbTestUtils;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public class PatriciaExactPSJournalGatewayImplTest extends PatriciaDbAbstractTest {

  protected PatriciaExactPSJournalGateway exactTableGateway;

  @BeforeMethod
  public void setUp() throws IOException, SQLException {
    super.setup(new TestModule());
    exactTableGateway = injector.getInstance(PatriciaExactPSJournalGateway.class);
  }

  @AfterMethod
  public void tearDown() {
    super.tearDown();
  }

  @Test
  public void testGetListOfEmptyInvoiceNumbersToSync() throws Exception {
    final List<Integer> toSyncList = exactTableGateway.getListOfInvoiceNumbersToSync();
    Assert.assertNotNull(toSyncList);
    Assert.assertEquals(toSyncList.size(), 0);
  }

  @Test
  public void testGetListOfInvoiceNumbersToSync() throws Exception {
    String
        insertTwoInvoices =
        "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2013-05-07 11:41:22.387', null, 0, N'V', N'52', N'5', N'13', N'9803606', N'P2329AU00', N'130507', N'', null, N'50737', N'', null, null, null, N'AUD', N'1.000000', null, N'2515.00', N'130507', N'130507', null, null, null, null, null, null, N'SRF', null, null, null, null, null, null, null, null, null, null, N'TROUGH MIRROR FOR SOLAR TR', null, null, N'', null, null, null, null, null, 170582, 2820, N'', null, null, null, null, null, null, null, null),('2013-05-07 11:41:22.387', null, 1, N'V', N'52', N'5', N'13', N'9803606', N'P2329AU00', N'130507', null, null, N'50737', N'', null, N'1655.00', null, N'AUD', N'1.000000', null, null, null, null, N'4', N'0', null, N'1', null, null, N'P', N'AU', N'1.00', null, null, N'AF', null, null, null, null, null, N'TROUGH MIRROR FOR SOLAR TR', N'74', null, N'', null, null, null, null, null, 170582, 2820, N'', N'T0', N'SRF', null, null, N'T', null, null, null),('2013-05-07 11:41:22.387', null, 2, N'V', N'52', N'5', N'13', N'9803606', N'P2329AU00', N'130507', null, null, N'50737', N'', null, N'490.00', null, N'AUD', N'1.000000', null, null, null, null, N'0', N'0', null, N'0', null, null, N'P', N'AU', N'1.00', null, null, N'AF', null, null, null, null, null, N'TROUGH MIRROR FOR SOLAR TR', N'74', null, N'', null, null, null, null, null, 170582, 2820, N'', N'O400', N'SRF', null, null, N'O', null, null, null),('2013-05-07 11:41:22.387', null, 3, N'V', N'52', N'5', N'13', N'9803606', N'P2329AU00', N'130507', null, null, N'50737', N'', null, N'370.00', null, N'AUD', N'1.000000', null, null, null, null, N'0', N'0', null, N'0', null, null, N'P', N'AU', N'1.00', null, null, N'AF', null, null, null, null, null, N'TROUGH MIRROR FOR SOLAR TR', N'74', null, N'', null, null, null, null, null, 170582, 2820, N'', N'O400', N'SRF', null, null, N'O', null, null, null),('2013-05-08 10:53:55.847', null, 0, N'V', N'52', N'5', N'13', N'9803607', N'P2328AU00', N'130508', N'', null, N'50660', N'', null, null, null, N'AUD', N'1.000000', null, N'1680.00', N'130508', N'130508', null, null, null, null, null, null, N'LJBG', null, null, null, null, null, null, null, null, null, null, N'Protection Against Passive', null, null, N'LW/FCR-QT FR2011/052', null, null, null, null, null, 170693, 2819, N'', null, null, null, null, null, null, null, null),('2013-05-08 10:53:55.847', null, 1, N'V', N'52', N'5', N'13', N'9803607', N'P2328AU00', N'130508', null, null, N'50660', N'', null, N'370.00', null, N'AUD', N'1.000000', null, null, null, null, N'0', N'0', null, N'0', null, null, N'P', N'AU', N'1.00', null, null, N'AF', null, null, null, null, null, N'Protection Against Passive', N'74', null, N'LW/FCR-QT FR2011/052', null, null, null, null, null, 170693, 2819, N'', N'O400', N'LJBG', null, null, N'O', null, null, null),('2013-05-08 10:53:55.847', null, 2, N'V', N'52', N'5', N'13', N'9803607', N'P2328AU00', N'130508', null, null, N'50660', N'', null, N'1310.00', null, N'AUD', N'1.000000', null, null, null, null, N'4', N'0', null, N'1', null, null, N'P', N'AU', N'2.00', null, null, N'AF', null, null, null, null, null, N'Protection Against Passive', N'74', null, N'LW/FCR-QT FR2011/052', null, null, null, null, null, 170693, 2819, N'', N'T0', N'LJBG', null, null, N'T', null, null, null);";
    DbTestUtils.executeSql(connectionManager.getDataSource(), insertTwoInvoices);
    final List<Integer> toSyncList = exactTableGateway.getListOfInvoiceNumbersToSync();
    Assert.assertNotNull(toSyncList);
    Assert.assertEquals(toSyncList.size(), 2);
    Assert.assertTrue(toSyncList.contains(new Integer(9803607)));
    Assert.assertTrue(toSyncList.contains(new Integer(9803606)));
  }

  @Test
  public void testGetJournalLines() throws Exception {
    final Integer patInvNo = 20315;
    // insert team leader info re 20315
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.TEAM_EFFORT(TEAM_ID, LOGIN_ID, ROLE_ID, IS_TEAM_LEADER, DOCKETING_DISTRIBUTION, DOCKETING_PROCESS) VALUES (20003, N'LJBG', 10, 1, null, null)");
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.WORK_GROUP(CASE_ID, WORK_GROUP_LABEL, TEAM_ID) VALUES (2504, null, 20003)");

    // insert header line of entry_number 20315
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2012-10-10 11:03:25.413', '2012-11-28 00:00:00.0', 0, N'I', N'62', N'10', N'12', N'20315', N'', N'121003', N'', null, N'', N'51019', null, N'', null, N'JPY', N'0.013358', null, N'477.10', N'121003', N'', N'0', N'0', null, N'G12FC-0147', null, null, N'', null, N'', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 147347, null, null, null, null, null, null, null, null, null, null)");

    // insert body line of entry_number 20315, using TMH as fee earner
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2012-10-10 11:03:25.413', '2012-11-28 00:00:00.0', 1, N'I', N'62', N'10', N'12', N'20315', N'P1311JP00', N'121003', null, null, N'', N'51019', null, N'6.37', null, N'JPY', N'0.013358', null, N'477.10', null, null, N'0', N'0', null, null, null, null, N'P', N'JP', N'1', null, null, N'AB', null, null, null, null, null, N'Medical Device', null, null, null, null, null, null, null, null, 147347, 2504, null, N'D105', N'TMH', null, null, N'D', null, null, null);");

    final List<JournalLine> journalLines = exactTableGateway.getJournalLines(patInvNo, null);
    Assert.assertNotNull(journalLines);
    Assert.assertEquals(journalLines.size(),2);
    JournalLine bodyLine = journalLines.get(1);
    Assert.assertNotNull(bodyLine);
    Assert.assertEquals(bodyLine.teamLeader,"LJBG");
    Assert.assertEquals(bodyLine.psj_loginid,"TMH");
  }



}
