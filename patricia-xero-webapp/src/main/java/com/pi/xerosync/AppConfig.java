package com.pi.xerosync;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.sitebricks.SitebricksModule;

import com.pi.xerosync.service.Home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig extends GuiceServletContextListener {

  private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

  @Override
  public Injector getInjector() {

    final SitebricksModule sitebricksModule = new SitebricksModule() {
      @Override
      protected void configureSitebricks() {
        scan(Home.class.getPackage());
      }
    };

    return Guice.createInjector(sitebricksModule, new BaseConfig());
  }
}
