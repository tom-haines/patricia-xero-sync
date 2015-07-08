package com.pi.xerosync;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import com.pi.xerosync.common.CurrencyConverter;
import com.pi.xerosync.common.CurrencyConverterImpl;
import com.pi.xerosync.common.InvoiceDataExtractor;
import com.pi.xerosync.common.InvoiceDataExtractorImpl;
import com.pi.xerosync.dbconnect.ConnectionManager;
import com.pi.xerosync.dbconnect.ConnectionManagerInMemory;
import com.pi.xerosync.dbconnect.DbTestUtils;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGatewayImpl;
import com.pi.xerosync.xeroservice.XeroClient;
import com.pi.xerosync.xeroservice.XeroClientDummy;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformer;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformerImpl;

import java.io.IOException;
import java.sql.SQLException;

/**
 * User: thomas Date: 18/02/14
 */
public abstract class PatriciaDbAbstractTest {

  protected Injector injector;
  protected ConnectionManager connectionManager;

  protected void setup(Module module) throws IOException, SQLException {
    injector = Guice.createInjector(module);
    connectionManager = injector.getInstance(ConnectionManager.class);
    DbTestUtils.dropCreateDatabase(connectionManager.getDataSource());
  }

  public void tearDown() {
    connectionManager = null;
    injector = null;
  }

  public static class TestModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(ConnectionManager.class).to(ConnectionManagerInMemory.class);
      bind(PatriciaExactPSJournalGateway.class).to(PatriciaExactPSJournalGatewayImpl.class);
      bind(CurrencyConverter.class).to(CurrencyConverterImpl.class);
      bind(InvoiceDataExtractor.class).to(InvoiceDataExtractorImpl.class);
    }
  }

  public static class GatewayModule extends TestModule {

    @Override
    protected void configure() {
      super.configure();
      bind(XeroClient.class).to(XeroClientDummy.class);
      bind(XeroFromPatriciaTransformer.class).to(XeroFromPatriciaTransformerImpl.class);
    }

  }


}
