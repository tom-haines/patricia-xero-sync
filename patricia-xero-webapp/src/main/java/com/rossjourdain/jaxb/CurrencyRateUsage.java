//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.08 at 09:52:02 AM CST 
//


package com.rossjourdain.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for currencyRateUsage.
 *
 * <p>The following schema fragment specifies the expected content contained within this class. <p>
 * <pre>
 * &lt;simpleType name="currencyRateUsage">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="DAILY"/>
 *     &lt;enumeration value="SINGLE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "currencyRateUsage")
@XmlEnum
public enum CurrencyRateUsage {

  /**
   * Daily Rate
   */
  DAILY,

  /**
   * Single Use Rate
   */
  SINGLE;

  public String value() {
    return name();
  }

  public static CurrencyRateUsage fromValue(String v) {
    return valueOf(v);
  }

}
