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
 * <p>Java class for invoiceStatus.
 *
 * <p>The following schema fragment specifies the expected content contained within this class. <p>
 * <pre>
 * &lt;simpleType name="invoiceStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *     &lt;enumeration value="DRAFT"/>
 *     &lt;enumeration value="SUBMITTED"/>
 *     &lt;enumeration value="DELETED"/>
 *     &lt;enumeration value="AUTHORISED"/>
 *     &lt;enumeration value="PAID"/>
 *     &lt;enumeration value="VOIDED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "invoiceStatus")
@XmlEnum
public enum InvoiceStatus {

  /**
   * Draft
   */
  DRAFT,

  /**
   * Submitted
   */
  SUBMITTED,

  /**
   * Deleted
   */
  DELETED,

  /**
   * Authorised
   */
  AUTHORISED,

  /**
   * Paid
   */
  PAID,

  /**
   * Voided
   */
  VOIDED;

  public String value() {
    return name();
  }

  public static InvoiceStatus fromValue(String v) {
    return valueOf(v);
  }

}
