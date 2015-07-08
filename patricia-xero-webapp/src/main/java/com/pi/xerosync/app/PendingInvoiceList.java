package com.pi.xerosync.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.pi.xerosync.BaseConfig;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * User: Thomas Haines; Date: 19/02/14
 */
public class PendingInvoiceList {

  public static void main(String[] args) throws IOException, SQLException {
    Injector injector = Guice.createInjector(new BaseConfig());
    PatriciaExactPSJournalGateway gateway = injector.getInstance(PatriciaExactPSJournalGateway.class);
    System.out.println(StringUtils.join(gateway.getListOfInvoiceNumbersToSync(), ";"));
  }


}
