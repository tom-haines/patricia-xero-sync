package com.pi.xerosync.common;

import com.pi.xerosync.PatriciaDbAbstractTest;
import com.pi.xerosync.dbconnect.DbTestUtils;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;

/**
 * User: thomas Date: 18/02/14
 */
public class InvoiceDataExtractorImplTest extends PatriciaDbAbstractTest {

  protected PatriciaExactPSJournalGateway invoiceGateway;
  protected InvoiceDataExtractor invoiceDataExtractor;

  @BeforeMethod
  public void setUp() throws IOException, SQLException {
    super.setup(new TestModule());
    invoiceGateway = injector.getInstance(PatriciaExactPSJournalGateway.class);
    invoiceDataExtractor = injector.getInstance(InvoiceDataExtractor.class);
  }

  @AfterMethod
  public void tearDown() {
    super.tearDown();
  }

  @Test
  public void testIsCreditor() throws Exception {
    String sampleInvoice =
        "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2008-08-11 16:00:23.0', null, 0, N'I', N'62', N'8', N'08', N'20008', null, N'080802', null, null, null, N'50112', null, null, null, N'USD', N'1.149425', null, N'1375.00', N'080802', null, N'0', N'0', null, N'MUM/ 0697', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 27734, null, null, null, null, null, null, null, null, null, null),('2008-08-11 16:00:23.0', null, 1, N'I', N'62', N'8', N'08', N'20008', N'P1010IN00', N'080802', null, null, null, N'50112', null, N'1580.46', null, N'USD', N'1.149425', null, N'1375.00', null, null, N'0', N'0', null, null, null, null, N'P', N'IN', N'1', null, null, N'AF', null, null, null, null, null, N'Automated Brick Laying Sys', null, null, null, null, null, null, null, null, 27734, 1110, null, N'D105', N'SRF', null, null, N'D', null, null, null);";
    DbTestUtils.executeSql(connectionManager.getDataSource(), sampleInvoice);
    PatriciaInvoice patriciaInvoice = new PatriciaInvoice(invoiceGateway.getJournalLines(20008, null));
    Assert.assertTrue(invoiceDataExtractor.isCreditor(patriciaInvoice));
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice), TransactionType.INVOICE);
  }

  @Test
  public void testGetCreditorInvoiceNumber() throws Exception {
    String sampleInvoice =
        "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2008-08-11 16:00:23.0', null, 0, N'I', N'62', N'8', N'08', N'20008', null, N'080802', null, null, null, N'50112', null, null, null, N'USD', N'1.149425', null, N'1375.00', N'080802', null, N'0', N'0', null, N' MUM/ 0697', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 27734, null, null, null, null, null, null, null, null, null, null),('2008-08-11 16:00:23.0', null, 1, N'I', N'62', N'8', N'08', N'20008', N'P1010IN00', N'080802', null, null, null, N'50112', null, N'1580.46', null, N'USD', N'1.149425', null, N'1375.00', null, null, N'0', N'0', null, null, null, null, N'P', N'IN', N'1', null, null, N'AF', null, null, null, null, null, N'Automated Brick Laying Sys', null, null, null, null, null, null, null, null, 27734, 1110, null, N'D105', N'SRF', null, null, N'D', null, null, null);";
    DbTestUtils.executeSql(connectionManager.getDataSource(), sampleInvoice);
    PatriciaInvoice patriciaInvoice = new PatriciaInvoice(invoiceGateway.getJournalLines(20008, null));
    final String invoiceRef = invoiceDataExtractor.getCreditorInvoiceNumber(patriciaInvoice);
    Assert.assertEquals(invoiceRef,"MUM/-0697");
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice), TransactionType.INVOICE);
  }

  @Test
  public void testGetCreditorInvoiceNumberOne() throws Exception {
    String sampleInvoice =
        "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2008-08-11 16:00:23.0', null, 0, N'I', N'62', N'8', N'08', N'20008', null, N'080802', null, null, null, N'50112', null, null, null, N'USD', N'1.149425', null, N'1375.00', N'080802', null, N'0', N'0', null, N'1', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 27734, null, null, null, null, null, null, null, null, null, null),('2008-08-11 16:00:23.0', null, 1, N'I', N'62', N'8', N'08', N'20008', N'P1010IN00', N'080802', null, null, null, N'50112', null, N'1580.46', null, N'USD', N'1.149425', null, N'1375.00', null, null, N'0', N'0', null, null, null, null, N'P', N'IN', N'1', null, null, N'AF', null, null, null, null, null, N'Automated Brick Laying Sys', null, null, null, null, null, null, null, null, 27734, 1110, null, N'D105', N'SRF', null, null, N'D', null, null, null);";
    DbTestUtils.executeSql(connectionManager.getDataSource(), sampleInvoice);
    PatriciaInvoice patriciaInvoice = new PatriciaInvoice(invoiceGateway.getJournalLines(20008, null));
    final String invoiceRef = invoiceDataExtractor.getCreditorInvoiceNumber(patriciaInvoice);
    Assert.assertEquals(invoiceRef,"20008");
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice), TransactionType.INVOICE);
  }


  @Test
  public void testEmptyCreditorInvoiceNumber() throws Exception {
    String sampleInvoice =
        "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2008-08-11 16:00:23.0', null, 0, N'I', N'62', N'8', N'08', N'20008', null, N'080802', null, null, null, N'50112', null, null, null, N'USD', N'1.149425', null, N'1375.00', N'080802', null, N'0', N'0', null, N' ', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 27734, null, null, null, null, null, null, null, null, null, null),('2008-08-11 16:00:23.0', null, 1, N'I', N'62', N'8', N'08', N'20008', N'P1010IN00', N'080802', null, null, null, N'50112', null, N'1580.46', null, N'USD', N'1.149425', null, N'1375.00', null, null, N'0', N'0', null, null, null, null, N'P', N'IN', N'1', null, null, N'AF', null, null, null, null, null, N'Automated Brick Laying Sys', null, null, null, null, null, null, null, null, 27734, 1110, null, N'D105', N'SRF', null, null, N'D', null, null, null);";
    DbTestUtils.executeSql(connectionManager.getDataSource(), sampleInvoice);
    PatriciaInvoice patriciaInvoice = new PatriciaInvoice(invoiceGateway.getJournalLines(20008, null));
    final String invoiceRef = invoiceDataExtractor.getCreditorInvoiceNumber(patriciaInvoice);
    Assert.assertEquals(invoiceRef,"20008");
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice), TransactionType.INVOICE);
  }

}
