package com.pi.xerosync.common;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public class PatriciaInvoice {

  private final List<JournalLine> journalLines;

  public PatriciaInvoice(List<JournalLine> journalLines) {
    this.journalLines = ImmutableList.copyOf(journalLines);
  }

  public List<JournalLine> getJournalLines() {
    return journalLines;
  }
}
