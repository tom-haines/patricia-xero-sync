package com.pi.xerosync.xeroservice;

import com.google.common.base.Optional;

import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.common.TransactionType;
import com.pi.xerosync.service.LogBus;
import com.rossjourdain.XeroClientException;
import com.rossjourdain.XeroClientUnexpectedException;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.LineItem;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * User: thomas Date: 17/02/14
 */
public interface XeroFromPatriciaTransformer {

  public Optional<Contact> getContact(Integer customerNumber, @Nullable LogBus logBus) throws IOException;

  public void saveContact(Contact contact) throws IOException;

  public Invoice createXeroInvoice(PatriciaInvoice patriciaInvoice, Contact contact, List<LineItem> lineItems,
                                   String country, @Nullable LogBus logBus)
      throws IOException, XeroClientException, XeroClientUnexpectedException;

  public CreditNote createXeroCreditNote(PatriciaInvoice patriciaInvoice, Contact contact, List<LineItem> lineItems, String country, @Nullable LogBus logBus)
      throws IOException, XeroClientException, XeroClientUnexpectedException;

  public List<LineItem> extractXeroLineItems(PatriciaInvoice patriciaInvoice, String partyCountry,
                                             TransactionType transactionType, @Nullable LogBus logBus) throws IOException;

  public boolean areTotalsEqual(PatriciaInvoice patriciaInvoice, List<LineItem> xeroLines, LogBus logBus);

  public boolean isCurrencyConversionNeeded(PatriciaInvoice patriciaInvoice);
  public BigDecimal getHeaderTotal(PatriciaInvoice patriciaInvoice);
  public BigDecimal getXeroLineItemTotal(List<LineItem> xeroLineItems);

}
