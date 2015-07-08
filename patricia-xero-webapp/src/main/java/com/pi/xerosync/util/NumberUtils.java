package com.pi.xerosync.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class NumberUtils {

  public static double roundToCurrency(double amount) {

    BigDecimal d = new BigDecimal(amount);
    d = d.setScale(2, BigDecimal.ROUND_HALF_UP);
    double money = d.doubleValue();

    return money;
  }

  public static String roundToCurrencyString(double amount) {
    NumberFormat myFormatter = NumberFormat.getNumberInstance();
    myFormatter.setMinimumFractionDigits(2);
    myFormatter.setMaximumFractionDigits(2);
    myFormatter.setGroupingUsed(false);
    return myFormatter.format(amount);
  }

}
