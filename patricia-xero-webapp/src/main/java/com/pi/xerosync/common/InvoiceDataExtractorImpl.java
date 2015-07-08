/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.common;

import com.google.common.base.Optional;

import com.rossjourdain.jaxb.CurrencyCode;
import com.rossjourdain.jaxb.InvoiceType;
import com.rossjourdain.jaxb.TrackingCategory;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class InvoiceDataExtractorImpl implements InvoiceDataExtractor {

  private static final Logger log = LoggerFactory.getLogger(InvoiceDataExtractorImpl.class);

  @Inject
  CurrencyConverter converter;

  @Override
  public TransactionType getTransactionType(PatriciaInvoice patriciaInvoice) {
    final JournalLine headerLine = getHeaderLine(patriciaInvoice);
    final BigDecimal total = headerLine.psj_amount_p_c_d;
    final int compareResult = total.compareTo(BigDecimal.ZERO);
    if (compareResult == 0) {
      return TransactionType.ZERO_BALANCE;
    }
    return compareResult < 0 ? TransactionType.CREDIT_NOTE : TransactionType.INVOICE;
  }

  @Override
  public InvoiceType getInvoiceType(PatriciaInvoice patriciaInvoice) {
    return isCreditor(patriciaInvoice) ? InvoiceType.ACCPAY : InvoiceType.ACCREC;
  }

  @Override
  public boolean isCreditor(PatriciaInvoice patriciaInvoice) {
    final List<JournalLine> journalLines = patriciaInvoice.getJournalLines();
    if (journalLines == null || journalLines.isEmpty()) {
      throw new IllegalArgumentException("No lines in invoice");
    }
    for (JournalLine line : journalLines) {
      if (StringUtils.isNotBlank(line.psj_creditor_number)) {
        return true;
      }
    }
    return false;
  }

  public Integer getActorId(PatriciaInvoice patriciaInvoice) {
    for (JournalLine line : patriciaInvoice.getJournalLines()) {
      if (StringUtils.isNotBlank(line.psj_debtor_number)) {
        return Integer.parseInt(line.psj_debtor_number);
      }
      if (StringUtils.isNotBlank(line.psj_creditor_number)) {
        return Integer.parseInt(line.psj_creditor_number);
      }
    }
    return null;
  }

  public LocalDate getPatriciaDueDate(PatriciaInvoice patriciaInvoice) {
    for (JournalLine line : patriciaInvoice.getJournalLines()) {
      if (StringUtils.isNotBlank(line.psj_due_date_payment)) {
        try {
          return DateTimeFormat.forPattern("yyMMdd").parseLocalDate(line.psj_due_date_payment);
        } catch (Exception e) {
          log.warn("Could not parse Patricia date {}", line.psj_due_date_payment);
        }
      }
    }
    return null;
  }

  public String getPatriciaInvoiceNumber(PatriciaInvoice patriciaInvoice) {
    for (JournalLine line : patriciaInvoice.getJournalLines()) {
      if (StringUtils.isNotBlank(line.psj_entry_number)) {
        return line.psj_entry_number;
      }
    }
    return "UNKNOWN_NUMBER";
  }

  public Calendar getInvoiceCal(PatriciaInvoice patriciaInvoice) throws IllegalStateException {
    JournalLine headerLine = getHeaderLine(patriciaInvoice);

    if (headerLine.psj_date == null) {
      throw new IllegalStateException("Could not find invoice date in header line");
    }

    Calendar cal = GregorianCalendar.getInstance();
    cal.setTime(headerLine.psj_date.toDate());
    return cal;
  }

  public CurrencyCode getCurrencyCode(PatriciaInvoice patriciaInvoice)
      throws IllegalStateException {
    String currencyCode = getHeaderLine(patriciaInvoice).psj_currency;

    return converter.convertToCurrencyCode(currencyCode);
  }

  public JournalLine getHeaderLine(PatriciaInvoice patriciaInvoice) throws IllegalStateException {
    for (JournalLine line : patriciaInvoice.getJournalLines()) {
      if (line.psj_line_number == 0) {
        return line;
      }
    }
    throw new IllegalStateException("Could not find journal line number zero");
  }

  public List<JournalLine> getInvoiceBodyLines(PatriciaInvoice patriciaInvoice)
      throws IllegalStateException {
    List<JournalLine> lineList = new ArrayList<>();
    for (JournalLine line : patriciaInvoice.getJournalLines()) {
      if (line.psj_line_number > 0) {
        lineList.add(line);
      }
    }

    // check for special condition that we can correct
    Optional<List<JournalLine>> lineListOverrideOpt =
        checkForCurrencyErrorOverride(patriciaInvoice, lineList);
    if (lineListOverrideOpt.isPresent() && lineListOverrideOpt.get().size() == 1) {
      return lineListOverrideOpt.get();
    }

    return lineList;
  }

  private Optional<List<JournalLine>> checkForCurrencyErrorOverride(PatriciaInvoice patriciaInvoice,
                                                                    List<JournalLine> invoiceBodyLines) {
    // if multiple lines
    if (invoiceBodyLines.size() < 2) {
      return Optional.absent();
    }

    final JournalLine firstRow = invoiceBodyLines.get(0);

    final String workCode = firstRow.psj_workcode;
    if (StringUtils.isBlank(workCode)) {
      return Optional.absent();
    }

    final BigDecimal firstRowPcd = firstRow.psj_amount_p_c_d;
    if (firstRowPcd == null) {
      return Optional.absent();
    }
    if (firstRowPcd.compareTo(firstRow.psj_amount) == 0) {
      return Optional.absent();
    }

    for (JournalLine line : invoiceBodyLines) {
      // creditor invoice
      if (StringUtils.isBlank(line.psj_creditor_number)) {
        return Optional.absent();
      }

      // all the same workcode
      if (!workCode.equals(line.psj_workcode)) {
        return Optional.absent();
      }

      // all not AUD
      if ("AUD".equalsIgnoreCase(line.psj_currency)) {
        return Optional.absent();
      }

      // all exchange rate 1.000
      final BigDecimal headLineExchangeRate = line.psj_exchange_rate;
      // if currency null, don't apply
      if (headLineExchangeRate == null) {
        return Optional.absent();
      }

      // if currency not 1.000 do not apply
      if (headLineExchangeRate.compareTo(BigDecimal.ONE) != 0) {
        return Optional.absent();
      }
    }

    // if we have reached this point, applying fix is OK – just use first row and allow

    return Optional.of(invoiceBodyLines.subList(0, 1));
  }

  @Override
  public List<TrackingCategory> getTrackingCategories(JournalLine journalLine) {
    List<TrackingCategory> trackList = new ArrayList<>();

    if (StringUtils.isNotBlank(journalLine.psj_loginid)) {
      TrackingCategory trackingCategory = new TrackingCategory();
      trackingCategory.setName(FEE_EARNER_TRACK_NAME);
      trackingCategory.setOption(journalLine.psj_loginid.trim().toUpperCase());
      trackList.add(trackingCategory);
    }

    if (StringUtils.isNotBlank(journalLine.psj_cost_center)) {
      TrackingCategory trackingCategory = new TrackingCategory();
      trackingCategory.setName(PRACTICE_CENTRE_TRACK_NAME);
      trackingCategory.setOption(journalLine.psj_cost_center.trim().toUpperCase());
      trackList.add(trackingCategory);
    }

    return trackList;
  }

  public LocalDate getInvoiceDate(PatriciaInvoice patriciaInvoice) throws IllegalStateException {
    JournalLine headerLine = getHeaderLine(patriciaInvoice);

    if (headerLine.psj_date == null) {
      throw new IllegalStateException("Could not find invoice date in header line");
    }
    return headerLine.psj_date;
  }

  public String getCaseRefString(PatriciaInvoice patriciaInvoice) {
    Set<String> caseRefSet = new HashSet<>();

    final List<JournalLine> invoiceBodyLines = getInvoiceBodyLines(patriciaInvoice);
    for (JournalLine invoiceLine : invoiceBodyLines) {
      if (StringUtils.isNotBlank(invoiceLine.psj_description)) {
        caseRefSet.add(invoiceLine.psj_description.trim());
      }
    }

    final JournalLine headerLine = getHeaderLine(patriciaInvoice);
    if (StringUtils.isNotBlank(headerLine.psj_description)) {
      caseRefSet.add(headerLine.psj_description.trim());
    }

    if (caseRefSet.isEmpty()) {
      return StringUtils.EMPTY;
    }

    return StringUtils.join(caseRefSet, ",");
  }

  @Override
  public String getCreditorInvoiceNumber(PatriciaInvoice patriciaInvoice) {
    String paymentRef = StringUtils.trimToNull(getHeaderLine(patriciaInvoice).getPsj_payment_ref());
    if ("1".equals(paymentRef)) {
      paymentRef = null;
    } else if ("0".equals(paymentRef)) {
      paymentRef = null;
    }
    if(paymentRef != null) {
      paymentRef = paymentRef.replaceAll("\\s+", "-");
    }

    String refToUseInXero = getPatriciaInvoiceNumber(patriciaInvoice);
    if (StringUtils.isNotBlank(paymentRef)) {
      // refToUseInXero = String.format("%s-REF%s", paymentRef, refToUseInXero);
      refToUseInXero = paymentRef;
    }

    log.info("refToUseInXero='{}'", refToUseInXero);
    return refToUseInXero;
  }

}
