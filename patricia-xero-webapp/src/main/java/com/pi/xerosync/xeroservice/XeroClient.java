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
import com.rossjourdain.jaxb.TrackingCategory;

import net.oauth.OAuthMessage;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public interface XeroClient {

  public String getTrackingCategoryOutput() throws XeroClientException, XeroClientUnexpectedException;

  public List<TrackingCategory> getTrackingCategories() throws XeroClientException, XeroClientUnexpectedException;

  public OAuthMessage postInvoices(ArrayOfInvoice arrayOfInvoices, @Nullable LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException;

  public OAuthMessage postCreditNote(CreditNote creditNote, @Nullable LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException;

  public List<Invoice> getInvoices(String invoiceNumber) throws XeroClientException, XeroClientUnexpectedException;

  public boolean doesSalesInvoiceNumberExistInXero(String invoiceNumber)
      throws XeroClientException, XeroClientUnexpectedException;

  public List<Contact> getContacts(String customerNumber) throws XeroClientException, XeroClientUnexpectedException;

  public Item getItem(String itemNum) throws XeroClientUnexpectedException, IOException;

  public OAuthMessage postContacts(ArrayOfContact arrayOfContact) throws XeroClientException,
                                                                         XeroClientUnexpectedException;

}
