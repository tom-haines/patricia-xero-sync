//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.08 at 09:52:02 AM CST 
//


package com.rossjourdain.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fixedTaxType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class. <p>
 * <pre>
 * &lt;simpleType name="fixedTaxType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INPUT"/>
 *     &lt;enumeration value="INPUT2"/>
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="OUTPUT"/>
 *     &lt;enumeration value="OUTPUT2"/>
 *     &lt;enumeration value="ZERORATED"/>
 *     &lt;enumeration value="EXEMPTINPUT"/>
 *     &lt;enumeration value="EXEMPTOUTPUT"/>
 *     &lt;enumeration value="RRINPUT"/>
 *     &lt;enumeration value="RROUTPUT"/>
 *     &lt;enumeration value="ZERORATEDINPUT"/>
 *     &lt;enumeration value="ZERORATEDOUTPUT"/>
 *     &lt;enumeration value="EXEMPTEXPORT"/>
 *     &lt;enumeration value="CAPEXINPUT"/>
 *     &lt;enumeration value="IMPORTDUTY"/>
 *     &lt;enumeration value="INPUTTAXED"/>
 *     &lt;enumeration value="SRINPUT"/>
 *     &lt;enumeration value="SROUTPUT"/>
 *     &lt;enumeration value="GSTONIMPORTS"/>
 *     &lt;enumeration value="GSTONCAPIMPORTS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "fixedTaxType")
@XmlEnum
public enum FixedTaxType {

  /**
   * Input
   */
  INPUT("INPUT"),

  /**
   * Input2
   */
  @XmlEnumValue("INPUT2")
  INPUT_2("INPUT2"),

  /**
   * None
   */
  NONE("NONE"),

  /**
   * Output
   */
  OUTPUT("OUTPUT"),

  /**
   * Output2
   */
  @XmlEnumValue("OUTPUT2")
  OUTPUT_2("OUTPUT2"),

  /**
   * Zero Rated
   */
  ZERORATED("ZERORATED"),

  /**
   * Exempt Input
   */
  EXEMPTINPUT("EXEMPTINPUT"),

  /**
   * Exempt Output
   */
  EXEMPTOUTPUT("EXEMPTOUTPUT"),

  /**
   * Reduced Rate Input
   */
  RRINPUT("RRINPUT"),

  /**
   * Reduced Rate Output
   */
  RROUTPUT("RROUTPUT"),

  /**
   * Zero Rated Input
   */
  ZERORATEDINPUT("ZERORATEDINPUT"),

  /**
   * Zero Rated Output
   */
  ZERORATEDOUTPUT("ZERORATEDOUTPUT"),

  /**
   * Exempt Export
   */
  EXEMPTEXPORT("EXEMPTEXPORT"),

  /**
   * Capital Input
   */
  CAPEXINPUT("CAPEXINPUT"),

  /**
   * Import Duty
   */
  IMPORTDUTY("IMPORTDUTY"),

  /**
   * Input Taxed
   */
  INPUTTAXED("INPUTTAXED"),

  /**
   * Special Rate Input
   */
  SRINPUT("SRINPUT"),

  /**
   * Special Rate Output
   */
  SROUTPUT("SROUTPUT"),

  /**
   * GST On Imports
   */
  GSTONIMPORTS("GSTONIMPORTS"),

  /**
   * GST On Capital Imports
   */
  GSTONCAPIMPORTS("GSTONCAPIMPORTS");
  private final String value;

  FixedTaxType(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static FixedTaxType fromValue(String v) {
    for (FixedTaxType c : FixedTaxType.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }

}
