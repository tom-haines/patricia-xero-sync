package com.pi.xerosync.type;

import com.pi.xerosync.PatriciaDbAbstractTest;
import com.pi.xerosync.common.InvoiceDataExtractor;
import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.common.TransactionType;
import com.pi.xerosync.dbconnect.DbTestUtils;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;
import com.pi.xerosync.xeroservice.XeroClient;
import com.pi.xerosync.xeroservice.XeroClientDummy;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformer;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformerImpl;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.LineItem;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * User: Thomas Haines; Date: 19/02/14
 */
public class CreditNoteTest extends PatriciaDbAbstractTest {

  protected PatriciaExactPSJournalGateway invoiceGateway;
  protected InvoiceDataExtractor invoiceDataExtractor;
  protected XeroFromPatriciaTransformer xeroFromPatriciaTransformer;

  @BeforeMethod
  public void setUp() throws IOException, SQLException {
    super.setup(new GatewayModule());
    invoiceGateway = injector.getInstance(PatriciaExactPSJournalGateway.class);
    invoiceDataExtractor = injector.getInstance(InvoiceDataExtractor.class);
    xeroFromPatriciaTransformer = injector.getInstance(XeroFromPatriciaTransformer.class);
  }

  @AfterMethod
  public void tearDown() {
    super.tearDown();
  }

  /**
   * This is an example credit note provided by HMP given to debtor
   */
  @Test
  public void testCreditNoteDebtor() throws Exception {
    // import sample credit note 900007
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-18 16:06:08.64', '1905-03-14 00:00:00.0', 0, N'V', N'72', N'2', N'14', N'900007', N'PN20050AU00', N'140218', N'', null, N'36562', N'', null, null, null, N'AUD', N'1.000000', null, N'-2125.75', N'140218', N'140304', null, null, null, null, null, null, N'LMM', null, null, null, null, null, null, null, null, null, null, N'Application for amendment ', null, null, N'', null, null, null, null, null, 289643, 56330, N'', null, null, null, null, null, null, null, null);");
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-18 16:06:08.64', '1905-03-14 00:00:00.0', 1, N'V', N'72', N'2', N'14', N'900007', N'PN20050AU00', N'140218', null, null, N'36562', N'', null, N'-2125.75', null, N'AUD', N'1.000000', null, null, null, null, N'2', N'10.00', null, N'1', null, null, N'PN', N'AU', N'-1.00', null, null, N'OO', null, null, null, null, null, N'Application for amendment ', N'110', null, N'', null, null, null, null, null, 289643, 56330, N'', N'C0020', N'LMM', null, null, N'T', null, null, null);");
    PatriciaInvoice patriciaInvoice = invoiceGateway.getPatriciaInvoice(900007, null);
    Assert.assertFalse(invoiceDataExtractor.isCreditor(patriciaInvoice));
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice),
                        TransactionType.CREDIT_NOTE);
    List<LineItem>
        xeroItemList =
        xeroFromPatriciaTransformer
            .extractXeroLineItems(patriciaInvoice, "AU", TransactionType.CREDIT_NOTE, null);
    Assert.assertNotNull(xeroItemList);
    Contact contact = new Contact();
    final
    CreditNote xeroCreditNote =
        xeroFromPatriciaTransformer
            .createXeroCreditNote(patriciaInvoice, contact, xeroItemList, "AU", null);
    Assert.assertNotNull(xeroCreditNote);
  }

  /**
   * This is an example credit note provided by HMP given to debtor (multi-line)
   */
  @Test
  public void testCreditNoteDebtorMultiLine() throws Exception {
    // import sample credit note 900008
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-19 09:24:49.55', '1905-03-14 00:00:00.0', 0, N'V', N'72', N'2', N'14', N'900008', N'D20217AU00', N'140219', N'', null, N'457', N'', null, null, null, N'AUD', N'1.000000', null, N'-974.50', N'140219', N'140305', null, null, null, null, null, null, N'LMM', null, null, null, null, null, null, null, null, null, null, N'Multi-Compartment Pouch', null, N'2004-00', N'', null, null, null, null, null, 289777, 51880, N'', null, null, null, null, null, null, null, null);");
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-19 09:24:49.55', '1905-03-14 00:00:00.0', 1, N'V', N'72', N'2', N'14', N'900008', N'D20217AU00', N'140219', null, null, N'457', N'', null, N'-654.50', null, N'AUD', N'1.000000', null, null, null, null, N'2', N'10.00', null, N'1', null, null, N'D', N'AU', N'-1.00', null, null, N'AA', null, null, null, null, null, N'Multi-Compartment Pouch', N'1118', N'2004-00', N'', null, null, null, null, null, 289777, 51880, N'', N'13010', N'MS', null, N'51880', N'R', null, null, null);");
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-19 09:24:49.55', '1905-03-14 00:00:00.0', 2, N'V', N'72', N'2', N'14', N'900008', N'D20217AU00', N'140219', null, null, N'457', N'', null, N'-320.00', null, N'AUD', N'1.000000', null, null, null, null, N'0', N'0', null, N'0', null, null, N'D', N'AU', N'-1.00', null, null, N'AA', null, null, null, null, null, N'Multi-Compartment Pouch', N'1118', N'2004-00', N'', null, null, null, null, null, 289777, 51880, N'', N'13000', N'MS', null, N'51880', N'B', null, null, null);");
    PatriciaInvoice patriciaInvoice = invoiceGateway.getPatriciaInvoice(900008, null);
    Assert.assertFalse(invoiceDataExtractor.isCreditor(patriciaInvoice));
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice),
                        TransactionType.CREDIT_NOTE);
    Contact contact = new Contact();
    List<LineItem>
        xeroItemList =
        xeroFromPatriciaTransformer
            .extractXeroLineItems(patriciaInvoice, "AU", TransactionType.CREDIT_NOTE, null);
    final
    CreditNote xeroCreditNote =
        xeroFromPatriciaTransformer
            .createXeroCreditNote(patriciaInvoice, contact, xeroItemList, "AU", null);
    Assert.assertNotNull(xeroCreditNote);
  }

  /**
   * This is an example credit note provided by HMP (as opposed to a write-off)
   */
  @Test
  public void testCreditNoteCreditor() throws Exception {
    // import sample credit note 20086
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-17 09:58:59.23', '1905-03-14 00:00:00.0', 0, N'I', N'72', N'2', N'14', N'20086', N'', N'140211', N'', null, N'', N'6642', null, N'', null, N'EUR', N'1.000000', null, N'-2155.50', N'140211', N'140511', N'0', N'0', null, N'87508', null, null, N'', null, N'', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 288792, null, null, null, null, null, null, null, N'European attorney''s charge (Inv. no. 87508)', null, null)");
    DbTestUtils.executeSql(connectionManager.getDataSource(),
                           "INSERT INTO dbo.EXACT_P_S_JOURNAL(PSJ_SAVED_DATE, PSJ_EXPORTED_DATE, PSJ_LINE_NUMBER, PSJ_JOURNAL_TYPE, PSJ_JOURNAL_NUMBER, PSJ_PERIOD, PSJ_FINANCIAL_YEAR, PSJ_ENTRY_NUMBER, PSJ_DESCRIPTION, PSJ_DATE, PSJ_GENERAL_LEDGER_ACCOUNT, PSJ_OFFSET_ACCOUNT, PSJ_DEBTOR_NUMBER, PSJ_CREDITOR_NUMBER, PSJ_OUTSTANDING_INVOICE_NUM, PSJ_AMOUNT, PSJ_JOURNALIZE_FC, PSJ_CURRENCY, PSJ_EXCHANGE_RATE, PSJ_PAYMENT_CHARGE_DISCOUNT, PSJ_AMOUNT_P_C_D, PSJ_DUE_DATE_INVOICE, PSJ_DUE_DATE_PAYMENT, PSJ_VAT_CODE, PSJ_VAT_AMOUNT, PSJ_WEEK_NUMBER, PSJ_PAYMENT_REF, PSJ_PAYMENT_METHOD, PSJ_AMOUNT_BLOCKED_ACCOUNT, PSJ_COST_CENTER, PSJ_COST_UNIT, PSJ_QUANTITY, PSJ_WRITE_OFF_CODE, PSJ_REVERSAL_ENTRY, PSJ_PROJECT_CODE, PSJ_AMOUNT_REVENUE, PSJ_AMOUNT_EXPENSES, PSJ_NUMBER_OF_REVENUES, PSJ_NUMBER_OF_EXPENSES, PSJ_COST_CENTER_2, PSJ_PROJECT_DESCRIPTION, PSJ_COST_CATEGORY, PSJ_JOURNAL_LABEL, PSJ_CATEGORY_TYPE, PSJ_CASE_COUNTRY_CODE, PSJ_CASE_TYPE, PSJ_SAVED_DATE_STR, PSJ_DUE_DATE_INVOICE_STR, PSJ_DUE_DATE_PAYMENT_STR, PSJ_TRANS_ID, PSJ_CASE_ID, PSJ_CHECKSUM, PSJ_WORKCODE, PSJ_LOGINID, PSJ_DEP_COSTCENTER, PSJ_OLD_CASE_ID, PSJ_WORKCODE_TYPE, PSJ_COMMENT, PSJ_LOGINID_REG, PSJ_KID_CODE) VALUES ('2014-02-17 09:58:59.23', '1905-03-14 00:00:00.0', 1, N'I', N'72', N'2', N'14', N'20086', N'P21141EP00', N'140211', null, null, N'', N'6642', null, N'-2155.50', null, N'EUR', N'1.512662', null, N'-2155.50', null, null, N'0', N'0', null, null, null, null, N'P', N'EP', N'1', null, null, N'AF', null, null, null, null, null, N'Method of Blasting', null, null, null, null, null, null, null, null, 288792, 56204, null, N'X41', N'RRE', null, null, N'D', N'European attorney''s charge (Inv. no. 87508)', null, null)");
    PatriciaInvoice
        patriciaInvoice = invoiceGateway.getPatriciaInvoice(20086, null);
    Assert.assertTrue(invoiceDataExtractor.isCreditor(patriciaInvoice));
    Assert.assertEquals(invoiceDataExtractor.getTransactionType(patriciaInvoice),
                        TransactionType.CREDIT_NOTE);
    List<LineItem>
        xeroItemList =
        xeroFromPatriciaTransformer
            .extractXeroLineItems(patriciaInvoice, "AU", TransactionType.CREDIT_NOTE, null);
    Contact contact = new Contact();
    final
    CreditNote xeroCreditNote =
        xeroFromPatriciaTransformer
            .createXeroCreditNote(patriciaInvoice, contact, xeroItemList, "AU", null);
    Assert.assertNotNull(xeroCreditNote);
  }

  class GatewayModule extends TestModule {

    @Override
    protected void configure() {
      super.configure();
      bind(XeroClient.class).to(XeroClientDummy.class);
      bind(XeroFromPatriciaTransformer.class).to(XeroFromPatriciaTransformerImpl.class);
    }

  }


}
