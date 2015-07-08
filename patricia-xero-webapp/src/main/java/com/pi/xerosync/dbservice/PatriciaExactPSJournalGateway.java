package com.pi.xerosync.dbservice;

import com.pi.xerosync.common.JournalLine;
import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.service.LogBus;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * For all functions that relate to interacting with EXACT_P_S_JOURNAL table
 */
public interface PatriciaExactPSJournalGateway {
  public List<Integer> getListOfInvoiceNumbersToSync() throws IOException, SQLException;
  public Integer getRemainingCount() throws IOException, SQLException;

  /**
   * Use #gatPatriciaInvoice
   */
  @Deprecated
  public List<JournalLine> getJournalLines(Integer invoiceNum, @Nullable final LogBus logBus) throws IOException, SQLException;

  public PatriciaInvoice getPatriciaInvoice(Integer invoiceNum, @Nullable final LogBus logBus) throws IOException, SQLException;

  public void markInvoiceCompleted(String invoiceNumber, LogBus logBus) throws IOException, SQLException;
}
