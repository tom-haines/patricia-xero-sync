package com.pi.xerosync.common;

import com.rossjourdain.jaxb.CurrencyCode;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User: thomas Date: 1/03/14
 */
public class CurrencyConverterImplTest {

  CurrencyConverterImpl converter;

  @BeforeMethod
  public void setUp() throws Exception {
    converter = new CurrencyConverterImpl();
  }

  @Test
  public void testStandard() throws Exception {
    Assert.assertEquals(converter.convertToCurrencyCode("USD"), CurrencyCode.USD);
    Assert.assertEquals(converter.convertToCurrencyCode("uSd"), CurrencyCode.USD);
    Assert.assertEquals(converter.convertToCurrencyCode("GBP"), CurrencyCode.GBP);
    Assert.assertEquals(converter.convertToCurrencyCode("AUD"), CurrencyCode.AUD);
  }

  @Test (expectedExceptions = IllegalStateException.class)
  public void testException() throws Exception {
    Assert.assertEquals(converter.convertToCurrencyCode(""), CurrencyCode.AUD);
  }


  @Test
  public void testUS() throws Exception {
    Assert.assertEquals(converter.convertToCurrencyCode("US"), CurrencyCode.USD);
  }

}
