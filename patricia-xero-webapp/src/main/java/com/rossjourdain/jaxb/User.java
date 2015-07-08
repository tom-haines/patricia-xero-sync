//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-b10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.07.08 at 09:52:02 AM CST 
//


package com.rossjourdain.jaxb;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for User complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="User">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="ValidationErrors" type="{}ArrayOfValidationError" minOccurs="0"/>
 *         &lt;element name="Warnings" type="{}ArrayOfWarning" minOccurs="0"/>
 *         &lt;element name="UserID" type="{}uniqueIdentifier" minOccurs="0"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UpdatedDateUTC" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="IsSubscriber" type="{}trueOrFalse" minOccurs="0"/>
 *         &lt;element name="OrganisationRole" type="{}organisationRole" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="status" type="{}entityValidationStatus" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "User", propOrder = {

})
public class User {

  @XmlElement(name = "ValidationErrors")
  protected ArrayOfValidationError validationErrors;
  @XmlElement(name = "Warnings")
  protected ArrayOfWarning warnings;
  @XmlElement(name = "UserID")
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  protected String userID;
  @XmlElement(name = "FirstName")
  protected String firstName;
  @XmlElement(name = "LastName")
  protected String lastName;
  @XmlElement(name = "UpdatedDateUTC", type = String.class)
  @XmlJavaTypeAdapter(Adapter1.class)
  @XmlSchemaType(name = "dateTime")
  protected Calendar updatedDateUTC;
  @XmlElement(name = "IsSubscriber")
  protected TrueOrFalse isSubscriber;
  @XmlElement(name = "OrganisationRole")
  protected OrganisationRole organisationRole;
  @XmlAttribute(name = "status")
  protected EntityValidationStatus status;

  /**
   * Gets the value of the validationErrors property.
   *
   * @return possible object is {@link ArrayOfValidationError }
   */
  public ArrayOfValidationError getValidationErrors() {
    return validationErrors;
  }

  /**
   * Sets the value of the validationErrors property.
   *
   * @param value allowed object is {@link ArrayOfValidationError }
   */
  public void setValidationErrors(ArrayOfValidationError value) {
    this.validationErrors = value;
  }

  /**
   * Gets the value of the warnings property.
   *
   * @return possible object is {@link ArrayOfWarning }
   */
  public ArrayOfWarning getWarnings() {
    return warnings;
  }

  /**
   * Sets the value of the warnings property.
   *
   * @param value allowed object is {@link ArrayOfWarning }
   */
  public void setWarnings(ArrayOfWarning value) {
    this.warnings = value;
  }

  /**
   * Gets the value of the userID property.
   *
   * @return possible object is {@link String }
   */
  public String getUserID() {
    return userID;
  }

  /**
   * Sets the value of the userID property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUserID(String value) {
    this.userID = value;
  }

  /**
   * Gets the value of the firstName property.
   *
   * @return possible object is {@link String }
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Sets the value of the firstName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setFirstName(String value) {
    this.firstName = value;
  }

  /**
   * Gets the value of the lastName property.
   *
   * @return possible object is {@link String }
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Sets the value of the lastName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setLastName(String value) {
    this.lastName = value;
  }

  /**
   * Gets the value of the updatedDateUTC property.
   *
   * @return possible object is {@link String }
   */
  public Calendar getUpdatedDateUTC() {
    return updatedDateUTC;
  }

  /**
   * Sets the value of the updatedDateUTC property.
   *
   * @param value allowed object is {@link String }
   */
  public void setUpdatedDateUTC(Calendar value) {
    this.updatedDateUTC = value;
  }

  /**
   * Gets the value of the isSubscriber property.
   *
   * @return possible object is {@link TrueOrFalse }
   */
  public TrueOrFalse getIsSubscriber() {
    return isSubscriber;
  }

  /**
   * Sets the value of the isSubscriber property.
   *
   * @param value allowed object is {@link TrueOrFalse }
   */
  public void setIsSubscriber(TrueOrFalse value) {
    this.isSubscriber = value;
  }

  /**
   * Gets the value of the organisationRole property.
   *
   * @return possible object is {@link OrganisationRole }
   */
  public OrganisationRole getOrganisationRole() {
    return organisationRole;
  }

  /**
   * Sets the value of the organisationRole property.
   *
   * @param value allowed object is {@link OrganisationRole }
   */
  public void setOrganisationRole(OrganisationRole value) {
    this.organisationRole = value;
  }

  /**
   * Gets the value of the status property.
   *
   * @return possible object is {@link EntityValidationStatus }
   */
  public EntityValidationStatus getStatus() {
    return status;
  }

  /**
   * Sets the value of the status property.
   *
   * @param value allowed object is {@link EntityValidationStatus }
   */
  public void setStatus(EntityValidationStatus value) {
    this.status = value;
  }

}
