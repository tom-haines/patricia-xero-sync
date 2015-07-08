package com.pi.xerosync.xeroservice;

import com.pi.xerosync.service.LogBus;
import com.rossjourdain.XeroClientException;
import com.rossjourdain.XeroClientUnexpectedException;
import com.rossjourdain.jaxb.ArrayOfContact;
import com.rossjourdain.jaxb.ArrayOfInvoice;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.Item;
import com.rossjourdain.jaxb.ItemPriceDetails;
import com.rossjourdain.jaxb.TrackingCategory;

import net.oauth.OAuthMessage;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public class XeroClientDummy implements XeroClient {

  public final static String DUMMY_ACCOUNT_CODE = "123";

  @Override
  public String getTrackingCategoryOutput() throws XeroClientException, XeroClientUnexpectedException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public List<TrackingCategory> getTrackingCategories() throws XeroClientException, XeroClientUnexpectedException {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public OAuthMessage postInvoices(ArrayOfInvoice arrayOfInvoices, @Nullable LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException {
    return null;
  }

  @Override
  public OAuthMessage postCreditNote(CreditNote creditNote, @Nullable LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException {
    return null;
  }

  @Override
  public List<Invoice> getInvoices(String invoiceNumber) throws XeroClientException, XeroClientUnexpectedException {
    return new ArrayList<>();
  }

  @Override
  public boolean doesSalesInvoiceNumberExistInXero(String invoiceNumber)
      throws XeroClientException, XeroClientUnexpectedException {
    return false;
  }

  @Override
  public List<Contact> getContacts(String customerNumber) throws XeroClientException, XeroClientUnexpectedException {
    return new ArrayList<>();
  }

  @Override
  public Item getItem(String itemNum) throws XeroClientUnexpectedException, IOException {
    final Item item = new Item();
    // ItemPriceDetails
    ItemPriceDetails details = new ItemPriceDetails();
    details.setAccountCode(DUMMY_ACCOUNT_CODE);
    item.setSalesDetails(details);

    ItemPriceDetails purchaseDetails = new ItemPriceDetails();
    purchaseDetails.setAccountCode(DUMMY_ACCOUNT_CODE);
    item.setPurchaseDetails(purchaseDetails);

    return item;
  }

  @Override
  public OAuthMessage postContacts(ArrayOfContact arrayOfContact) throws XeroClientException, XeroClientUnexpectedException {
    return null;
  }
}
