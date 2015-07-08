package com.pi.xerosync.common;

import com.rossjourdain.jaxb.CurrencyCode;

import org.apache.commons.lang.StringUtils;

/**
 * User: thomas Date: 1/03/14
 */
public class CurrencyConverterImpl implements CurrencyConverter {

  @Override
  public CurrencyCode convertToCurrencyCode(String currencyCodeStr) {
    if (StringUtils.isBlank(currencyCodeStr)) {
      throw new IllegalStateException("Could not find currency code in header line");
    }
    currencyCodeStr = currencyCodeStr.toUpperCase();

    // correct currency code PNG in Patricia to official code PGK
    if ("PNG".equals(currencyCodeStr)) {
      currencyCodeStr = CurrencyCode.PGK.name();
    }

    switch (currencyCodeStr) {
      case "US":
        return CurrencyCode.USD;
      default:
        return CurrencyCode.valueOf(currencyCodeStr);
    }
  }
}
