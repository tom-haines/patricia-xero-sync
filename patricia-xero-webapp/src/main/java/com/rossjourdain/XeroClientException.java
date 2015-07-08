/*
 *  Copyright 2011 Ross Jourdain
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.rossjourdain;

import com.rossjourdain.jaxb.ApiException;
import com.rossjourdain.jaxb.ApiExceptionExtended;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.Payment;
import com.rossjourdain.jaxb.ResponseType;
import com.rossjourdain.jaxb.ValidationError;
import com.rossjourdain.jaxb.Warning;
import com.sun.xml.ws.streaming.DOMStreamReader;

import net.oauth.OAuthProblemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * @author ross
 */
@SuppressWarnings("serial")
public class XeroClientException extends Exception {

  private static final Logger log = LoggerFactory
      .getLogger(XeroClientException.class);

  private ApiExceptionExtended apiException;
  private List<ValidationError> validationErrors;
  private List<Warning> warnings;
  private Object modelObject;

  public XeroClientException(String message,
                             OAuthProblemException oAuthProblemException) {
    super(message, oAuthProblemException);

    String oAuthProblemExceptionString = XeroXmlManager.oAuthProblemExceptionToXml(oAuthProblemException);
    if (oAuthProblemExceptionString == null) {
      log.warn("oAuthProblemExceptionToString: " + oAuthProblemException.toString());
    } else {
      apiException = XeroXmlManager.xmlToException(oAuthProblemExceptionString);
      unmarshalAdditionalData();

      /* Add this back in if you need more details on the exception */
      log.warn("oAuthProblemExceptionString: " + oAuthProblemExceptionString);
    }
  }

  public ApiException getApiException() {
    return apiException;
  }

  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  public List<Warning> getWarnings() {
    return warnings;
  }

  public Object getModelObject() {
    return modelObject;
  }

  private void unmarshalAdditionalData() {

    try {

      Element e = (Element) apiException.getElements().getDataContractBase();
      String elementType = e.getAttribute("xsi:type");

      JAXBContext context = JAXBContext.newInstance(ResponseType.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();

      // unmarshaller.setEventHandler(new DefaultValidationEventHandler());

      JAXBElement<?> jaxbElement = null;

      if ("Invoice".equals(elementType)) {
        jaxbElement = unmarshaller.unmarshal(new DOMStreamReader(e),
                                             Invoice.class);
        modelObject = (Invoice) jaxbElement.getValue();
      } else if ("Payment".equals(elementType)) {
        jaxbElement = unmarshaller.unmarshal(new DOMStreamReader(e),
                                             Payment.class);
        modelObject = (Payment) jaxbElement.getValue();
      } else if ("Contact".equals(elementType)) {
        jaxbElement = unmarshaller.unmarshal(new DOMStreamReader(e),
                                             Contact.class);
        modelObject = (Contact) jaxbElement.getValue();
      } else if ("CreditNote".equals(elementType)) {
        jaxbElement = unmarshaller.unmarshal(new DOMStreamReader(e),
                                             CreditNote.class);
        modelObject = (CreditNote) jaxbElement.getValue();
      } else {
        throw new RuntimeException("Unrecognised type: " + elementType);
      }

      if (jaxbElement != null) {
        if (jaxbElement instanceof DataContractBase) {
          DataContractBase dataContractBase = (DataContractBase) jaxbElement
              .getValue();

          if (dataContractBase.getWarnings() != null
              && dataContractBase.getWarnings().getWarning() != null) {
            warnings = dataContractBase.getWarnings().getWarning();
          }
          if (dataContractBase.getValidationErrors() != null
              && dataContractBase.getValidationErrors().getValidationError() != null) {
            validationErrors = dataContractBase.getValidationErrors()
                .getValidationError();
          }
        } else {
          log.info("jaxbElement is of class type "
                   + jaxbElement.getClass().getName());
        }

      }

    } catch (JAXBException ex) {
      ex.printStackTrace();
    }

  }

  public void printDetails() {

    System.out.println("");
    System.out.println(this.getMessage());
    System.out.println("Message: " + apiException.getMessage());

    for (int i = 0; i < warnings.size(); i++) {
      Warning warning = warnings.get(i);
      System.out.println("Warning " + (i + 1) + ": " + warning.getMessage());
    }

    for (int i = 0; i < validationErrors.size(); i++) {
      ValidationError validationError = validationErrors.get(i);
      System.out.println("Validation Error " + (i + 1) + ": "
                         + validationError.getMessage());
    }

    System.out.println("Error " + apiException.getErrorNumber() + ": "
                       + apiException.getType());
    if (modelObject instanceof Invoice) {
      Invoice invoice = (Invoice) modelObject;
      System.out.println("Invoice ID: " + invoice.getInvoiceID());
      if (invoice.getDate() != null) {
        System.out.println("Invoice Date: " + invoice.getDate().getTime());
      }
    } else if (modelObject instanceof Payment) {
      Payment payment = (Payment) modelObject;
      System.out.println("Payment ID: " + payment.getPaymentID());
      if (payment.getDate() != null) {
        System.out.println("Payment Date: " + payment.getDate().getTime());
      }
    } else if (modelObject instanceof Contact) {
      Contact contact = (Contact) modelObject;
      System.out.println("Contact ID: " + contact.getContactID());
      System.out.println("Contact Name: " + contact.getName());
    } else {
      System.out.println("Unrecognised type: " + modelObject);
    }

  }
}
