/*
 * Copyright 2013 Practice Insight Pty Ltd, all rights reserved.
 */
package com.pi.xerosync.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.google.sitebricks.http.Post;
import com.google.sitebricks.rendering.Decorated;

import com.pi.xerosync.xeroservice.XeroFromPatriciaTransformer;
import com.rossjourdain.jaxb.Contact;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jiakuanwang
 */
@At("/customer")
@Decorated
public class Customer extends Template {

  private final Logger log = LoggerFactory.getLogger(Customer.class);

  @Inject
  private HttpServletRequest request;

  @Inject
  private XeroFromPatriciaTransformer xeroFromPatriciaTransformer;

  private String customerNumber;
  private String customerName;

  @Get
  public void showForm() {
    // Just show the page of adding customer
  }

  @Post
  public String addCustomer() {
    log.debug("Adding customer: customerNumber={}, customerName={}", customerNumber, customerName);
    try {
      if (StringUtils.isBlank(customerNumber) || StringUtils.isBlank(customerName)) {
        request.getSession()
            .setAttribute("message", "Both customer number and customer name cannot be empty");
        return "customer";
      }

      Integer customerNumberInt = -1;
      try {
        customerNumberInt = new Integer(StringUtils.trimToNull(customerNumber));
      } catch (Exception e) {
        log.warn("Could not parse customer number '{}'", customerNumber);
        request.getSession()
            .setAttribute("message", "Customer number must be present, and a valid number");
        return "customer";
      }

      final Optional<Contact> contactOptional = xeroFromPatriciaTransformer.getContact(customerNumberInt, null);
      if (contactOptional.isPresent()) {
        request.getSession()
            .setAttribute("message", "Contact with id of " + customerNumber + " already exists");
        return "customer";
      }

      Contact contact = new Contact();
      contact.setContactNumber(customerNumber);
      contact.setName(customerName);
      xeroFromPatriciaTransformer.saveContact(contact);

      request.getSession()
          .setAttribute("message",
                        "Contact creation successful (" + customerNumber + ") – created: "
                        + contact.getName()
                        + " As this was added manually, please set the country code and contact details via the xero UI.");
    } catch (IOException e) {
      request.getSession().setAttribute("message", "IOException: " + e.getMessage());
      log.error(e.getMessage(), e);
    } catch (Exception e) {
      request.getSession().setAttribute("message", "Error: " + e.getMessage());
      log.error(e.getMessage(), e);
    }
    return "customer";
  }

  @Override
  public String getCustomerLinkStyle() {
    return "active";
  }

  public boolean getHasMessage() {
    final String message = (String) request.getSession().getAttribute("message");
    return StringUtils.isNotBlank(message);
  }

  public String getMessage() {
    return (String) request.getSession().getAttribute("message");
  }

  public String getCustomerNumber() {
    return customerNumber;
  }

  public void setCustomerNumber(String customerNumber) {
    this.customerNumber = customerNumber;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }
}
