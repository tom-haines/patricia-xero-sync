package com.pi.xerosync.service;

import com.google.common.base.Optional;

import com.pi.xerosync.common.InvoiceDataExtractor;
import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.common.TransactionType;
import com.pi.xerosync.dbservice.PatriciaDbContactExtractor;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;
import com.pi.xerosync.xeroservice.XeroClient;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformer;
import com.rossjourdain.XeroClientException;
import com.rossjourdain.XeroClientUnexpectedException;
import com.rossjourdain.jaxb.Address;
import com.rossjourdain.jaxb.ArrayOfInvoice;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.LineItem;
import com.rossjourdain.jaxb.TrackingCategory;
import com.rossjourdain.jaxb.TrackingCategoryOption;

import net.oauth.OAuthMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.qos.logback.classic.Level;

/**
 * User: thomas Date: 17/02/14
 */
@Singleton
public class SyncManagerImpl implements SyncManager {

  private static final Logger log = LoggerFactory.getLogger(SyncManagerImpl.class);
  @Inject
  PatriciaDbContactExtractor patriciaDbContactExtractor;
  @Inject
  InvoiceDataExtractor extractor;
  @Inject
  XeroClient xeroClient;
  @Inject
  private PatriciaExactPSJournalGateway patriciaExactPSJournalGateway;
  @Inject
  private XeroFromPatriciaTransformer xeroFromPatriciaTransformer;

  @Override
  public synchronized void syncRecords(final LogBus logBus) {
    log.info("Starting sync records method");
    try {
      logBus.log(Level.INFO, "Loading invoices to be synchronized...");
      List<Integer>
          pendingInvoiceList =
          patriciaExactPSJournalGateway.getListOfInvoiceNumbersToSync();

      // loop through each invoice
      for (Integer invoiceNum : pendingInvoiceList) {
        PatriciaInvoice
            patriciaInvoice =
            patriciaExactPSJournalGateway.getPatriciaInvoice(invoiceNum, logBus);

        final TransactionType transactionType = extractor.getTransactionType(patriciaInvoice);

        if (transactionType == TransactionType.ZERO_BALANCE) {
          log.debug(String.format(
              "Invoice num '%s' being marked as processed as total of invoice is zero balance.",
              invoiceNum));
          patriciaExactPSJournalGateway
              .markInvoiceCompleted(extractor.getPatriciaInvoiceNumber(patriciaInvoice), logBus);
          logBus.log(Level.INFO, String
              .format("Invoice num '%s' skipped as total of invoice is zero balance.", invoiceNum));
          log.info(String.format(
              "Invoice num '%s' marked processed as total of invoice is zero balance.",
              invoiceNum));
          continue;
        }

        // make sure actor is OK
        if (createCheckContact(patriciaInvoice, logBus)) {
          continue;
        }

        // check for duplicate
        if (!extractor.isCreditor(patriciaInvoice) && isDuplicateSalesInvoiceNumber(patriciaInvoice,
                                                                                    logBus)) {
          continue;
        }

        Contact contact = getContact(patriciaInvoice, logBus);
        if (contact == null) {
          continue;
        }

        Thread.sleep(750);

        boolean processSuccess = false;

        final String country = getCountry(contact, logBus);
        List<LineItem>
            xeroItemList =
            getXeroLineItems(patriciaInvoice, contact, country, transactionType, logBus);
        if (xeroItemList == null || xeroItemList.isEmpty()) {
          continue;
        }

        if (trackingCategoriesMissing(patriciaInvoice, xeroItemList, logBus)) {
          continue;
        }

        switch (transactionType) {
          case INVOICE:
            processSuccess =
                processInvoice(patriciaInvoice, contact, country, xeroItemList, logBus);
            break;
          case CREDIT_NOTE:
            processSuccess =
                processCreditNote(patriciaInvoice, contact, country, xeroItemList, logBus);
            break;
        }

        if (processSuccess) {
          patriciaExactPSJournalGateway
              .markInvoiceCompleted(extractor.getPatriciaInvoiceNumber(patriciaInvoice), logBus);
        }
      } // end invoice loop
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      logBus.log(Level.ERROR, e.getMessage());
      logBus.log(Level.ERROR, ExceptionUtils.getFullStackTrace(e));
    }
    log.info("Synchronize process run completed.");
    logBus.log(Level.INFO, "Synchronize process run completed.");
  }

  private boolean trackingCategoriesMissing(PatriciaInvoice patriciaInvoice,
                                            List<LineItem> xeroItemList, LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException {
    final List<TrackingCategory> trackingCategories = xeroClient.getTrackingCategories();

    for (LineItem lineItem : xeroItemList) {
      if (lineItem.getTracking() != null && lineItem.getTracking().getTrackingCategory() != null) {
        for (TrackingCategory track : lineItem.getTracking().getTrackingCategory()) {
          String trackCategoryId = null;
          String trackCategoryOptionId = null;

          for (TrackingCategory foundCategory : trackingCategories) {
            if (foundCategory.getName() != null && foundCategory.getName()
                .equals(track.getName())) {
              trackCategoryId = foundCategory.getTrackingCategoryID();
              track.setTrackingCategoryID(trackCategoryId);

              if (foundCategory.getOptions() != null
                  && foundCategory.getOptions().getOption() != null) {
                for (TrackingCategoryOption foundOption : foundCategory.getOptions().getOption()) {
                  if (foundOption.getName() != null && foundOption.getName()
                      .equals(track.getOption())) {
                    trackCategoryOptionId = foundOption.getTrackingOptionID();
                  }
                }
              }
            }
          }

          if (trackCategoryId == null) {
            String
                msg =
                String.format(
                    "There is no such tracking category name '%s' in xero.  Please add via xero GUI - skipping %s",
                    track.getName(), extractor.getPatriciaInvoiceNumber(patriciaInvoice));
            log.error(msg);
            logBus.log(Level.ERROR, msg);
            return true;
          }

          if (trackCategoryOptionId == null) {
            String
                msg =
                String.format(
                    "There is no such tracking category option '%s' in '%s' in xero.  Please add via xero GUI - skipping %s",
                    track.getOption(), track.getName(),
                    extractor.getPatriciaInvoiceNumber(patriciaInvoice));
            log.error(msg);
            logBus.log(Level.ERROR, msg);
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean processCreditNote(PatriciaInvoice patriciaInvoice, Contact contact,
                                    String country,
                                    List<LineItem> xeroItemList, LogBus logBus)
      throws IOException, InterruptedException, XeroClientException, XeroClientUnexpectedException {

    // add invoice to xero
    log.info(
        "Adding credit note to xero for " + extractor.getPatriciaInvoiceNumber(patriciaInvoice));
    logBus.log(Level.INFO, "Adding credit note to xero for " + extractor
        .getPatriciaInvoiceNumber(patriciaInvoice));

    CreditNote creditNote =
        xeroFromPatriciaTransformer
            .createXeroCreditNote(patriciaInvoice, contact, xeroItemList, country, logBus);

    // save creditNote to xero
    log.debug("Date: " + new LocalDate(creditNote.getDate().getTime()).toString());

    OAuthMessage message = xeroClient.postCreditNote(creditNote, logBus);
    log.debug("message={}", message);

    return true;
  }

  private boolean processInvoice(PatriciaInvoice patriciaInvoice, Contact contact, String country,
                                 List<LineItem> xeroItemList, LogBus logBus)
      throws IOException, InterruptedException, XeroClientException, XeroClientUnexpectedException {

    // add invoice to xero
    log.info("Adding invoice to xero for " + extractor.getPatriciaInvoiceNumber(patriciaInvoice));
    logBus.log(Level.INFO,
               "Adding invoice to xero for " + extractor.getPatriciaInvoiceNumber(patriciaInvoice));

    Invoice newInvoice = xeroFromPatriciaTransformer
        .createXeroInvoice(patriciaInvoice, contact, xeroItemList, country, logBus);

    // save invoice to xero
    log.debug("Date: " + new LocalDate(newInvoice.getDate().getTime()).toString());
    log.debug("Due Date: " + new LocalDate(newInvoice.getDueDate().getTime()).toString());
    log.debug("Amount due: " + newInvoice.getTotal());

    ArrayOfInvoice arrayOfInvoices = new ArrayOfInvoice();
    arrayOfInvoices.getInvoice().add(newInvoice);
    OAuthMessage message = xeroClient.postInvoices(arrayOfInvoices, logBus);
    log.debug("message={}", message);

    return true;
  }

  private String getCountry(Contact contact, LogBus logBus) {
    // get customer country as double check
    String country = null;
    if (contact.getAddresses() != null && contact.getAddresses().getAddress() != null
        && !contact.getAddresses().getAddress().isEmpty()) {
      for (Address address : contact.getAddresses().getAddress()) {
        if (StringUtils.isBlank(country) && address != null && StringUtils
            .isNotBlank(address.getCountry())) {
          country = address.getCountry();
        }
      }
    }

    if (StringUtils.isBlank(country)) {
      logBus.log(Level.DEBUG,
                 "Country code not found from xero record, assuming AU for tax codes – set 2-letter ISO country code in addresses via xero interface to correct this");
      country = "AU";
    }
    return country;
  }

  private List<LineItem> getXeroLineItems(PatriciaInvoice patriciaInvoice, Contact contact,
                                          String country,
                                          TransactionType transactionType, LogBus logBus)
      throws IOException {
    final List<LineItem> xeroItemList =
        xeroFromPatriciaTransformer
            .extractXeroLineItems(patriciaInvoice, country, transactionType, logBus);

    if (!xeroFromPatriciaTransformer.areTotalsEqual(patriciaInvoice, xeroItemList, logBus)) {
      log.error("Total of invoice does not match line items – needs investigation re invoice no {}",
                extractor.getPatriciaInvoiceNumber(patriciaInvoice));
      logBus.log(Level.ERROR,
                 "Total of invoice does not match line items – needs investigation re invoice no "
                 + extractor
                     .getPatriciaInvoiceNumber(patriciaInvoice));
      return null;
    }
    return xeroItemList;
  }

  /**
   * @return true if an error occurred
   */
  private boolean createCheckContact(PatriciaInvoice patriciaInvoice, LogBus logBus) {
    // check actor id
    Integer actorId = extractor.getActorId(patriciaInvoice);
    if (actorId == null) {
      final String invoiceNumber = extractor.getPatriciaInvoiceNumber(patriciaInvoice);
      log.error(
          "Error processing invoice number, could not find debtor or creditor number for Patricia invoice number {}",
          invoiceNumber);
      logBus.log(Level.ERROR,
                 "Error processing invoice number, could not find debtor or creditor number for Patricia invoice number "
                 + invoiceNumber);
      return true;
    }

    boolean contactCheckPassed = checkContact(extractor.getActorId(patriciaInvoice), logBus);
    if (!contactCheckPassed) {
      log.info("Contact check did not pass, skipping invoice");
      logBus.log(Level.INFO, "Contact check did not pass, skipping invoice");
      return false;
    }

    return false;
  }

  private boolean isDuplicateSalesInvoiceNumber(PatriciaInvoice patriciaInvoice, LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException, IOException, SQLException {
    // check if invoice already added to xero
    if (xeroClient
        .doesSalesInvoiceNumberExistInXero(extractor.getPatriciaInvoiceNumber(patriciaInvoice))) {
      String
          msg =
          "Skipping invoice " + extractor.getPatriciaInvoiceNumber(patriciaInvoice)
          + " as it is already added to xero";
      log.info(msg);
      logBus.log(Level.INFO, msg);
      patriciaExactPSJournalGateway
          .markInvoiceCompleted(extractor.getPatriciaInvoiceNumber(patriciaInvoice), logBus);
      return true;
    }
    return false;
  }

  private Contact getContact(PatriciaInvoice patriciaInvoice, LogBus logBus) throws IOException {
    Integer actorId = extractor.getActorId(patriciaInvoice);
    log.debug("Fetching contact that was saved");
    Optional<Contact> contactOpt = xeroFromPatriciaTransformer.getContact(actorId, logBus);
    if (!contactOpt.isPresent()) {
      String msg = "Error adding actor id " + actorId.toString()
                   + " : the name could not be found after adding to xero";
      log.error(msg);
      logBus.log(Level.ERROR, msg);
      return null;
    }
    Contact contact = contactOpt.get();

    log.debug("Customer " + contact.getContactID() + " obtained -- " + contact.getName());
    logBus.log(Level.DEBUG,
               "Customer " + contact.getContactID() + " obtained -- " + contact.getName());

    return contact;
  }

  private boolean checkContact(Integer actorId, LogBus logBus) {
    // check if (debtor or creditor number) has been added to xero
    try {
      Optional<Contact> contactOpt = xeroFromPatriciaTransformer.getContact(actorId, logBus);
      if (!contactOpt.isPresent()) {
        // contact not in xero –– add to xero
        log.info("Actor id " + actorId + " not found in xero, adding");
        logBus.log(Level.INFO, "Actor id " + actorId + " not found in xero, adding");

        Contact contact = patriciaDbContactExtractor.getContact(actorId, logBus);
        if (contact == null) {

        }

        xeroFromPatriciaTransformer.saveContact(contact);
        logBus.log(Level.INFO,
                   "Contact creation successful (" + actorId + ") – created: " + contact.getName());
      }
    } catch (IOException ex) {
      log.warn(ex.getMessage(), ex);
      logBus.log(Level.WARN, "IOException: " + ex.getMessage());
      return false;
    } catch (Exception ex) {
      log.warn(ex.getMessage(), ex);
      logBus.log(Level.WARN, "Exception: " + ex.getMessage());
      return false;
    }

    return true;
  }
}
