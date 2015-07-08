package com.pi.xerosync.dbservice;

import com.pi.xerosync.common.PatriciaInvoice;
import com.pi.xerosync.service.LogBus;

import java.io.IOException;
import java.sql.SQLException;

import com.rossjourdain.jaxb.Contact;

/**
 * User: thomas Date: 17/02/14
 */
public interface PatriciaDbContactExtractor {
  public String getCountry(Integer actorId) throws SQLException, IOException;
  public Contact getContact(Integer actorId,LogBus logBus) throws IOException, SQLException;
  public boolean isLegalEntity(Integer actorId) throws SQLException, IOException;

}
