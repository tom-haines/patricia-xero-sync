package com.pi.xerosync.xeroservice;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import com.pi.xerosync.common.InvoiceDataExtractor;
import com.pi.xerosync.common.JournalLine;
import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.common.TransactionType;
import com.pi.xerosync.service.LogBus;
import com.rossjourdain.XeroClientException;
import com.rossjourdain.XeroClientUnexpectedException;
import com.rossjourdain.jaxb.ArrayOfContact;
import com.rossjourdain.jaxb.ArrayOfLineItem;
import com.rossjourdain.jaxb.ArrayOfTrackingCategory;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.CreditNoteType;
import com.rossjourdain.jaxb.CurrencyCode;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.InvoiceStatus;
import com.rossjourdain.jaxb.InvoiceType;
import com.rossjourdain.jaxb.Item;
import com.rossjourdain.jaxb.LineItem;
import com.rossjourdain.jaxb.TrackingCategory;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.qos.logback.classic.Level;

/**
 * User: thomas Date: 17/02/14
 */
@Singleton
public class XeroFromPatriciaTransformerImpl implements XeroFromPatriciaTransformer {

  private final Logger log = LoggerFactory.getLogger(XeroFromPatriciaTransformerImpl.class);
  @Inject
  InvoiceDataExtractor extractor;
  @Inject
  private XeroClient xeroClient;

  private Map<String, Item> itemMap = new HashMap<>();

  @Override
  public synchronized Optional<Contact> getContact(Integer customerNumber, @Nullable LogBus logBus)
      throws IOException {
    try {
      List<Contact> contactList = xeroClient.getContacts(customerNumber.toString());
      if (contactList != null && !contactList.isEmpty()) {
        if (contactList.size() > 1) {
          log.warn("Found multiple contacts for actor Id");
          if (logBus != null) {
            logBus.log(Level.WARN, "Found multiple contacts for actor Id");
          }
        }
        Contact contact = contactList.get(0);
        log.debug("Found customer " + contact.getContactID() + " " + contact.getName());
        if (logBus != null) {
          logBus.log(Level.DEBUG,
                     "Found customer " + contact.getContactID() + " " + contact.getName());
        }
        return Optional.of(contact);
      } else {
        return Optional.absent();
      }
    } catch (XeroClientUnexpectedException | XeroClientException e) {
      log.warn(e.getMessage(), e);
      if (logBus != null) {
        logBus.log(Level.WARN, e.getClass().getName() + e.getMessage());
      }
      throw new IOException(e.getMessage());
    }
  }

  @Override
  public synchronized void saveContact(Contact contact) throws IOException {
    if (contact == null) {
      log.warn("Contact was null, no action possible");
      return;
    }

    ArrayOfContact arrayOfContact = new ArrayOfContact();
    arrayOfContact.getContact().add(contact);
    try {
      xeroClient.postContacts(arrayOfContact);
    } catch (XeroClientException | XeroClientUnexpectedException e) {
      log.error(e.getMessage(), e);
      throw new IOException(e.getMessage());
    }
  }

  @Override
  public Invoice createXeroInvoice(PatriciaInvoice patriciaInvoice, Contact contact,
                                   List<LineItem> lineItems,
                                   String country, @Nullable LogBus logBus)
      throws IOException, XeroClientException, XeroClientUnexpectedException {
    /*
      Account number logic(pre-added as items via import script)
      D - 201
      DS - 200
      O - 203
      S,T0 - 202
      T - 200
     */

    Invoice newInvoice = new Invoice();
    newInvoice.setStatus(InvoiceStatus.AUTHORISED);
    Contact myContact = new Contact();
    myContact.setContactID(contact.getContactID());
    newInvoice.setContact(myContact);

    final InvoiceType invoiceType = extractor.getInvoiceType(patriciaInvoice);
    switch (invoiceType) {
      case ACCPAY:
        newInvoice.setInvoiceNumber(extractor.getCreditorInvoiceNumber(patriciaInvoice));
        break;
      case ACCREC:
        newInvoice.setInvoiceNumber(extractor.getPatriciaInvoiceNumber(patriciaInvoice));
        break;
    }

    CurrencyCode currencyCode = extractor.getCurrencyCode(patriciaInvoice);
    newInvoice.setCurrencyCode(currencyCode);

    // Date
    newInvoice.setDate(extractor.getInvoiceCal(patriciaInvoice));

    if (StringUtils.isBlank(country)) {
      throw new IllegalStateException(
          "The country of the invoice issuer must be known to set the tax code.  Check exact table for invoice "
          + extractor.getPatriciaInvoiceNumber(patriciaInvoice));
    }

    Calendar cal = getDueDate(extractor.getInvoiceDate(patriciaInvoice),
                              extractor.getPatriciaDueDate(patriciaInvoice), country);
    newInvoice.setDueDate(cal);
    if (logBus != null) {
      logBus.log(Level.INFO, "Set invoice payment date to " + new LocalDate(cal.getTime()));
    }

    // use inclusive amounts
    newInvoice.setLineAmountTypes("Inclusive");

    ArrayOfLineItem arrayLineItems = new ArrayOfLineItem();
    newInvoice.setLineItems(arrayLineItems);
    arrayLineItems.getLineItem().addAll(lineItems);

    newInvoice.setTotal(getHeaderTotal(patriciaInvoice));

    final String caseRef = extractor.getCaseRefString(patriciaInvoice);
    if (extractor.isCreditor(patriciaInvoice)) {
      newInvoice.setType(InvoiceType.ACCPAY);
    } else {
      newInvoice.setType(InvoiceType.ACCREC);
      if (StringUtils.isNotBlank(caseRef)) {
        newInvoice.setReference(caseRef);
      }
    }

    return newInvoice;

  }

  @Override
  public CreditNote createXeroCreditNote(PatriciaInvoice patriciaInvoice, Contact contact,
                                         List<LineItem> lineItems,
                                         String country, @Nullable LogBus logBus)
      throws IOException, XeroClientException, XeroClientUnexpectedException {
    CreditNote creditNote = new CreditNote();
    creditNote.setStatus(InvoiceStatus.AUTHORISED);
    Contact myContact = new Contact();
    myContact.setContactID(contact.getContactID());
    creditNote.setContact(myContact);

    final InvoiceType invoiceType = extractor.getInvoiceType(patriciaInvoice);
    switch (invoiceType) {
      case ACCPAY:
        creditNote.setType(CreditNoteType.ACCPAYCREDIT);
        creditNote.setCreditNoteNumber(extractor.getCreditorInvoiceNumber(patriciaInvoice));
        break;
      case ACCREC:
        creditNote.setType(CreditNoteType.ACCRECCREDIT);
        creditNote.setCreditNoteNumber(extractor.getPatriciaInvoiceNumber(patriciaInvoice));
        break;
    }

    CurrencyCode currencyCode = extractor.getCurrencyCode(patriciaInvoice);
    creditNote.setCurrencyCode(currencyCode);

    // Date
    creditNote.setDate(extractor.getInvoiceCal(patriciaInvoice));

    if (StringUtils.isBlank(country)) {
      throw new IllegalStateException(
          "The country of the invoice issuer must be known to set the tax code.  Check exact table for invoice "
          + extractor.getPatriciaInvoiceNumber(patriciaInvoice));
    }

    ArrayOfLineItem arrayLineItems = new ArrayOfLineItem();
    creditNote.setLineItems(arrayLineItems);
    arrayLineItems.getLineItem().addAll(lineItems);

    // use inclusive amounts
    creditNote.setLineAmountTypes("Inclusive");
    creditNote.setReference(extractor.getCaseRefString(patriciaInvoice));
    creditNote.setTotal(getHeaderTotal(patriciaInvoice));

    return creditNote;
  }

  private synchronized Item getItem(String workcode) throws IOException {
    if (itemMap.containsKey(workcode)) {
      return itemMap.get(workcode);
    }
    try {
      Item item = xeroClient.getItem(workcode);
      if (item != null) {
        itemMap.put(workcode, item);
      }
      return item;
    } catch (XeroClientUnexpectedException e) {
      throw new IOException(e.getMessage());
    }
  }

  private boolean isLocalClient(String country) throws IllegalStateException {
    final String localCountry = "AU";
    if (StringUtils.isBlank(localCountry)) {
      throw new IllegalStateException("localCountry must be defined in system");
    }
    if (StringUtils.isBlank(country)) {
      throw new IllegalStateException("country must be defined on invoice");
    }
    return StringUtils.equalsIgnoreCase(localCountry, country);
  }

  @Override
  public List<LineItem> extractXeroLineItems(PatriciaInvoice patriciaInvoice, String partyCountry,
                                             TransactionType transactionType,
                                             @Nullable LogBus logBus) throws IOException {

    String logIdentifier = extractor.getPatriciaInvoiceNumber(patriciaInvoice);
    List<LineItem> xeroLineItems = new ArrayList<>();

    final List<JournalLine> invoiceBodyLines = extractor.getInvoiceBodyLines(patriciaInvoice);
    for (JournalLine jLine : invoiceBodyLines) {
      LineItem lineItem = new LineItem();
      lineItem.setQuantity(BigDecimal.ONE);
      lineItem.setItemCode(jLine.psj_workcode);

      Item item = getItem(jLine.psj_workcode);
      if (item == null) {
        throw new IllegalStateException(
            "Error - workcode " + jLine.psj_workcode + " has not been added to xero");
      }

      if (StringUtils.isNotBlank(jLine.psj_comment)) {
        lineItem.setDescription(
            String.format("%s - %s", item.getDescription(), jLine.psj_comment.trim()));
      } else {
        lineItem.setDescription(item.getDescription());
      }

      // set the tracking categories
      final List<TrackingCategory> trackingCategories = extractor.getTrackingCategories(jLine);
      if (trackingCategories.size() > 0) {
        ArrayOfTrackingCategory trackArray = new ArrayOfTrackingCategory();
        trackArray.getTrackingCategory().addAll(trackingCategories);
        lineItem.setTracking(trackArray);
      }

      // Determine which column to use
      BigDecimal journalLineAmount = getLineAmount(patriciaInvoice, jLine, invoiceBodyLines.size());
      lineItem.setQuantity(BigDecimal.ONE);
      lineItem.setUnitAmount(journalLineAmount);

      boolean localClient = isLocalClient(partyCountry);

      // <TaxType>
      if (extractor.isCreditor(patriciaInvoice)) {
        // if supplier, if VAT amt is 0 then GST exempt item
        // if supplier, and VAT amt is 10, then it is GST standard
        if (jLine.psj_vat_amount.intValue() == getVatRate().intValue()) {
          lineItem.setTaxType("INPUT");
        } else {
          lineItem.setTaxType("EXEMPTEXPENSES");
        }

        lineItem.setAccountCode(item.getPurchaseDetails().getAccountCode());
      } else {
        // if VAT amt is 10, it is GST standard
        // if VAT amt is 0, and code is 4, it is export
        // if vat amt is 0, and code is 0, it is GST exempt item
        if (jLine.psj_vat_amount.intValue() == getVatRate().intValue()) {
          // GST standard
          lineItem.setTaxType("OUTPUT");
        } else {
          lineItem.setAccountCode(item.getSalesDetails().getAccountCode());

          if (jLine.psj_vat_code == 0) {
            if (localClient) {
              lineItem.setTaxType("EXEMPTOUTPUT");
            } else {
              lineItem.setTaxType("EXEMPTEXPORT");
            }
          } else if (jLine.psj_vat_code == 4) {
            lineItem.setTaxType("EXEMPTEXPORT");
          } else {
            throw new IllegalStateException("Unknown vat code of " + jLine.psj_vat_code);
          }
        }
      }

      // flip the value if it is a credit note
      if (transactionType == TransactionType.CREDIT_NOTE) {
        final BigDecimal unitAmount = lineItem.getUnitAmount();
        if (unitAmount != null) {
          final int signum = unitAmount.signum();
          if (signum < 0) {
            lineItem.setUnitAmount(unitAmount.abs());
          } else if (signum > 0) {
            lineItem.setUnitAmount(unitAmount.negate());
          }
          log.debug("Line item value changed from {} to {}", unitAmount.toString(),
                    lineItem.getUnitAmount().toString());
        }
      }

      xeroLineItems.add(lineItem);
    }

    // check for any rounding issues
    final BigDecimal patriciaTotal = getHeaderTotal(patriciaInvoice);
    final BigDecimal total = getXeroLineItemTotal(xeroLineItems);

    if (total.compareTo(patriciaTotal) != 0) {
      if (total.subtract(patriciaTotal).abs().compareTo(new BigDecimal(0.05)) < 0) {
        BigDecimal delta = total.subtract(patriciaTotal);
        if (xeroLineItems.size() > 0) {
          LineItem firstLineItem = xeroLineItems.get(0);
          BigDecimal preAdjust = firstLineItem.getUnitAmount();
          firstLineItem.setUnitAmount(firstLineItem.getUnitAmount().subtract(delta));
          String
              msg =
              String.format(
                  "Adjustment by delta=%s on invoice %s: preadjust lineItem1=%s; postAdjust=%s",
                  delta.toString(), logIdentifier, preAdjust.toString(),
                  firstLineItem.getUnitAmount().toString());
          log.info(msg);
          if (logBus != null) {
            logBus.log(Level.INFO, msg);
          }
        }
      } else {
        String
            msg =
            String.format("Non-match on invoice %s: total of lines=%s; total of header=%s",
                          logIdentifier,
                          total.toString(), patriciaTotal.toString());
        log.info(msg);
        if (logBus != null) {
          logBus.log(Level.WARN, msg);
        }
      }
    }

    return ImmutableList.copyOf(xeroLineItems);
  }

  private BigDecimal getLineAmount(PatriciaInvoice patriciaInvoice, JournalLine jLine,
                                   int invoiceRowCount) {
    BigDecimal journalLineAmount = jLine.psj_amount;

    if (StringUtils.isNotBlank(jLine.psj_creditor_number)) {
      // if it is a creditor, and p_c_d amount exists,
      // then use p_c_d amount amount instead of line amount
      // example invoices H20517 or G20470 for examples
      if (jLine.psj_amount_p_c_d != null && invoiceRowCount == 1) {
        log.info("Overriding currency conversion {} due to known currency conversion issues",
                 jLine.psj_amount_p_c_d);
        return jLine.psj_amount_p_c_d.setScale(2, RoundingMode.HALF_UP);
      }
    }

    if (isCurrencyConversionNeeded(patriciaInvoice)) {
      final BigDecimal exchangeRate = jLine.psj_exchange_rate;
      if (exchangeRate.compareTo(BigDecimal.ONE) != 0) {
        BigDecimal rawAmount = journalLineAmount;
        journalLineAmount = journalLineAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
        final String
            logMsg =
            String.format(
                "total adjusted lineAmount rawAmount=%s; currency=%s; exchangeRate=%s; newAmount=%s",
                rawAmount.toString(), jLine.psj_currency,
                exchangeRate.toString(),
                journalLineAmount.toString());
        log.info(logMsg);
      }
    }

    journalLineAmount = journalLineAmount.setScale(2, RoundingMode.HALF_UP);
    return journalLineAmount;
  }

  @Override
  public boolean areTotalsEqual(PatriciaInvoice patriciaInvoice, List<LineItem> xeroLineItems,
                                LogBus logBus) {
    final BigDecimal headerTotal = getHeaderTotal(patriciaInvoice);
    final BigDecimal xeroLineItemTotal = getXeroLineItemTotal(xeroLineItems);
    boolean result = headerTotal.compareTo(xeroLineItemTotal) == 0;
    if (!result) {
      String
          msg =
          String.format("Header line total '%s' != '%s' invoiceLine total", headerTotal.toString(),
                        xeroLineItemTotal.toString());
      log.warn(msg);
      if (logBus != null) {
        logBus.log(Level.WARN, msg);
      }
    }
    return result;
  }

  @Override
  public boolean isCurrencyConversionNeeded(PatriciaInvoice patriciaInvoice) {
    // If the exchange rate of the header row is 1, then currency conversion is NOT needed on rows
    // e.g. in tests, see 20103
    // If the exchange rate of the header row is !=1, then currency conversion IS needed on rows
    // e.g. in tests, see 300066, 300067, 900006

    final JournalLine headerLine = extractor.getHeaderLine(patriciaInvoice);
    final BigDecimal headLineExchangeRate = headerLine.psj_exchange_rate;
    if (headLineExchangeRate == null) {
      throw new IllegalStateException(
          "New condition needs investigation: the psj_exchange_rate rate column was null.  It should always have a value.");
    }

    if (headLineExchangeRate.compareTo(BigDecimal.ONE) > 0) {
      log.trace("headLineExchangeRate is greater than 1");
      return true;
    } else if (headLineExchangeRate.compareTo(BigDecimal.ONE) < 0) {
      log.trace("headLineExchangeRate is less than 1");
      return true;
    } else {
      log.trace("headLineExchangeRate is 1.0");
      return false;
    }
  }

  private boolean isOnePointZeroValue(BigDecimal bigInteger) {
    if (bigInteger.compareTo(BigDecimal.ONE) > 0) {
      log.trace("headLineExchangeRate is greater than 1");
      return false;
    } else if (bigInteger.compareTo(BigDecimal.ONE) < 0) {
      log.trace("headLineExchangeRate is less than 1");
      return false;
    } else {
      log.trace("headLineExchangeRate is 1.0");
      return true;
    }
  }


  @Override
  public BigDecimal getHeaderTotal(PatriciaInvoice patriciaInvoice) {
    final JournalLine headerLine = extractor.getHeaderLine(patriciaInvoice);
    BigDecimal headerTotal = headerLine.psj_amount_p_c_d;

    // only apply currency conversion to the total for outgoing invoices to minimise possible issue
    if (StringUtils.isBlank(headerLine.psj_creditor_number)) {
      final BigDecimal exchangeRate = headerLine.psj_exchange_rate;
      if (exchangeRate.compareTo(BigDecimal.ONE) != 0) {
        BigDecimal rawAmount = headerTotal;
        headerTotal = headerTotal.divide(exchangeRate, 2, RoundingMode.HALF_UP);
        final String
            logMsg =
            String.format(
                "total adjusted header rawAmount=%s; currency=%s; exchangeRate=%s; newAmount=%s",
                rawAmount.toString(), headerLine.psj_currency,
                exchangeRate.toString(),
                headerTotal.toString());
        log.info(logMsg);
      }
    }

    if (extractor.getTransactionType(patriciaInvoice) == TransactionType.CREDIT_NOTE) {
      BigDecimal rawAmount = headerTotal;
      final int signum = headerTotal.signum();
      headerTotal.setScale(2, RoundingMode.HALF_UP);
      if (signum < 0) {
        headerTotal = headerTotal.abs();
        log.debug("Credit note total changed from {} to {}", rawAmount.toString(),
                  headerTotal.toString());
      } else if (signum > 0) {
        headerTotal = headerTotal.negate();
        log.debug("Credit note total changed from {} to {}", rawAmount.toString(),
                  headerTotal.toString());
      }
    }

    return headerTotal;
  }

  @Override
  public BigDecimal getXeroLineItemTotal(List<LineItem> xeroLineItems) {
    BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    for (LineItem xeroLine : xeroLineItems) {
      total = total.add(xeroLine.getUnitAmount());
    }

    return total;
  }

  private Calendar getDueDate(LocalDate invoiceDate, LocalDate patriciaDueDate, String country) {
    final String localCountry = "AU";
    int plusDays = 60;
    if (StringUtils.equalsIgnoreCase(localCountry, country)) {
      plusDays = 30;
    }
    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(invoiceDate.plusDays(plusDays).toDate());
    return cal;
  }

  public BigDecimal getVatRate() {
    return new BigDecimal("10");
  }

}
