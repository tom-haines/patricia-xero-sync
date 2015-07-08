package com.pi.xerosync.app;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.pi.xerosync.BaseConfig;
import com.pi.xerosync.service.LogBus;
import com.pi.xerosync.service.SyncManager;

import java.util.UUID;

public class ManualSync {

  // to limit sync to one invoice:
  //  System.setProperty("limit.to.invoice.no", "20470");
  // SELECT * FROM EXACT_P_S_JOURNAL WHERE PSJ_ENTRY_NUMBER=20517;

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new BaseConfig());
    SyncManager syncManager = injector.getInstance(SyncManager.class);
    final String runId = UUID.randomUUID().toString();
    syncManager.syncRecords(new LogBus(runId));
  }
}
