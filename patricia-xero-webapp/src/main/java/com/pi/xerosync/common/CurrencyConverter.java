package com.pi.xerosync.common;

import com.rossjourdain.jaxb.CurrencyCode;

/**
 * User: thomas Date: 1/03/14
 */
public interface CurrencyConverter {

  CurrencyCode convertToCurrencyCode(String currencyCodeStr);

}
