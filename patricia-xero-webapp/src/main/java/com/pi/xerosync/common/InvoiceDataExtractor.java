package com.pi.xerosync.common;

import com.rossjourdain.jaxb.CurrencyCode;
import com.rossjourdain.jaxb.InvoiceType;
import com.rossjourdain.jaxb.TrackingCategory;

import org.jetbrains.annotations.Nullable;
import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public interface InvoiceDataExtractor {

  public final String PRACTICE_CENTRE_TRACK_NAME = "PracticeCentre";
  public final String FEE_EARNER_TRACK_NAME = "FeeEarner";

  public TransactionType getTransactionType(PatriciaInvoice patriciaInvoice);

  public InvoiceType getInvoiceType(PatriciaInvoice patriciaInvoice);

  public boolean isCreditor(PatriciaInvoice patriciaInvoice);

  public Integer getActorId(PatriciaInvoice patriciaInvoice);

  public String getPatriciaInvoiceNumber(PatriciaInvoice patriciaInvoice);

  public CurrencyCode getCurrencyCode(PatriciaInvoice patriciaInvoice);

  public String getCaseRefString(PatriciaInvoice patriciaInvoice);

  public String getCreditorInvoiceNumber(PatriciaInvoice patriciaInvoice);

  public LocalDate getPatriciaDueDate(PatriciaInvoice patriciaInvoice);

  public Calendar getInvoiceCal(PatriciaInvoice patriciaInvoice);

  public LocalDate getInvoiceDate(PatriciaInvoice patriciaInvoice);

  public JournalLine getHeaderLine(PatriciaInvoice patriciaInvoice);

  public List<JournalLine> getInvoiceBodyLines(PatriciaInvoice patriciaInvoice);

  public List<TrackingCategory> getTrackingCategories(JournalLine journalLine);

}
