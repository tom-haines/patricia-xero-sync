package com.rossjourdain;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import com.pi.xerosync.dbconnect.XeroCredentials;
import com.pi.xerosync.dbconnect.XeroCredentialsImpl;
import com.pi.xerosync.xeroservice.XeroClient;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.TrackingCategory;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public class XeroClientLiveTest {

  protected static Injector injector;

  @BeforeClass
  protected static void setup() throws IOException, SQLException {
    injector = Guice.createInjector(new XeroModule());
  }

  @AfterClass
  public static void tearDown() {
    injector = null;
  }

  @Test(enabled = false)
  public void getSampleInvoice() throws XeroClientException, XeroClientUnexpectedException {
    XeroClientLive xeroClient = (XeroClientLive) injector.getInstance(XeroClient.class);
    final List<Invoice> invoices = xeroClient.getInvoices("9804251");
    Assert.assertNotNull(invoices);
    Assert.assertEquals(invoices.size(), 1);
    Invoice invoice = invoices.get(0);
    Assert.assertNotNull(invoice);
  }

  @Test(enabled = false)
  public void testFindSalesOnly() throws XeroClientException, XeroClientUnexpectedException {
    // confirmed via integration test on 25-Feb-2014
    XeroClientLive xeroClient = (XeroClientLive) injector.getInstance(XeroClient.class);
    final boolean findValidSales = xeroClient.doesSalesInvoiceNumberExistInXero("9803969");
    Assert.assertTrue(findValidSales);
    final boolean findValidPurchaseNum = xeroClient.doesSalesInvoiceNumberExistInXero("251100345");
    Assert.assertFalse(findValidPurchaseNum);
  }

  @Test(enabled = false)
  public void getTrackingCategoryOutput() throws XeroClientException, XeroClientUnexpectedException {
    XeroClientLive xeroClient = (XeroClientLive) injector.getInstance(XeroClient.class);
    String output = xeroClient.getTrackingCategoryOutput();
    Assert.assertNotNull(output);
    System.out.println(output);

    final List<TrackingCategory> trackingCategories = xeroClient.getTrackingCategories();
    Assert.assertNotNull(trackingCategories);

  }

  public static class XeroModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(XeroClient.class).to(XeroClientLive.class);
      bind(XeroCredentials.class).to(XeroCredentialsImpl.class);
    }
  }

}
