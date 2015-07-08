package com.pi.xerosync;

import com.google.inject.AbstractModule;

import com.pi.xerosync.common.CurrencyConverter;
import com.pi.xerosync.common.CurrencyConverterImpl;
import com.pi.xerosync.common.InvoiceDataExtractor;
import com.pi.xerosync.common.InvoiceDataExtractorImpl;
import com.pi.xerosync.dbconnect.ConnectionManager;
import com.pi.xerosync.dbconnect.ConnectionManagerImpl;
import com.pi.xerosync.dbconnect.XeroCredentials;
import com.pi.xerosync.dbconnect.XeroCredentialsImpl;
import com.pi.xerosync.dbservice.PatriciaDbContactExtractor;
import com.pi.xerosync.dbservice.PatriciaDbContactExtractorImpl;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGateway;
import com.pi.xerosync.dbservice.PatriciaExactPSJournalGatewayImpl;
import com.pi.xerosync.service.SyncManager;
import com.pi.xerosync.service.SyncManagerImpl;
import com.pi.xerosync.xeroservice.XeroClient;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformer;
import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformerImpl;
import com.rossjourdain.XeroClientLive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard production guice bindings for non-web components
 */
public class BaseConfig extends AbstractModule {

  private static final Logger log = LoggerFactory.getLogger(BaseConfig.class);

  @Override
  protected void configure() {
    bind(ConnectionManager.class).to(ConnectionManagerImpl.class);
    bind(XeroCredentials.class).to(XeroCredentialsImpl.class);
    bind(CurrencyConverter.class).to(CurrencyConverterImpl.class);
    bind(InvoiceDataExtractor.class).to(InvoiceDataExtractorImpl.class);
    bind(PatriciaExactPSJournalGateway.class).to(PatriciaExactPSJournalGatewayImpl.class);
    bind(PatriciaDbContactExtractor.class).to(PatriciaDbContactExtractorImpl.class);
    bind(SyncManager.class).to(SyncManagerImpl.class);
    bind(XeroClient.class).to(XeroClientLive.class);
    bind(XeroFromPatriciaTransformer.class).to(XeroFromPatriciaTransformerImpl.class);
  }
}
