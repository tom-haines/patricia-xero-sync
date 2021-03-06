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
 * <p>Java class for invoiceType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class. <p>
 * <pre>
 * &lt;simpleType name="invoiceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="ACCPAY"/>
 *     &lt;enumeration value="ACCREC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "invoiceType")
@XmlEnum
public enum InvoiceType {

  /**
   * Accounts Payable
   */
  ACCPAY,

  /**
   * Accounts Receivable
   */
  ACCREC;

  public String value() {
    return name();
  }

  public static InvoiceType fromValue(String v) {
    return valueOf(v);
  }

}
