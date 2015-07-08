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

import com.google.inject.Singleton;

import com.pi.xerosync.dbconnect.XeroCredentials;
import com.pi.xerosync.service.LogBus;
import com.pi.xerosync.xeroservice.XeroClient;
import com.rossjourdain.jaxb.ArrayOfContact;
import com.rossjourdain.jaxb.ArrayOfInvoice;
import com.rossjourdain.jaxb.ArrayOfPayment;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CreditNote;
import com.rossjourdain.jaxb.Invoice;
import com.rossjourdain.jaxb.InvoiceType;
import com.rossjourdain.jaxb.Item;
import com.rossjourdain.jaxb.Report;
import com.rossjourdain.jaxb.ResponseType;
import com.rossjourdain.jaxb.TrackingCategory;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.ParameterStyle;
import net.oauth.client.OAuthClient;
import net.oauth.client.OAuthResponseMessage;
import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ch.qos.logback.classic.Level;

/**
 * @author ross
 */
@Singleton
public class XeroClientLive implements XeroClient {

  private static final Logger log = LoggerFactory.getLogger(XeroClientLive.class);

  @Inject
  private XeroCredentials xeroCredentials;

  private final String endpointUrl = "https://api.xero.com/api.xro/2.0/";

  private OAuthClient clientObj = null;
  private OAuthAccessor accessorObj = null;

  private synchronized OAuthClient getClient() {
    if (clientObj == null) {
      // set -Dpi.proxy=true to use a local proxy
      HttpHost proxy = null;
      String proxyStr = System.getProperty("pi.proxy", "false");
      if ("true".equalsIgnoreCase(proxyStr)) {
        proxy = new HttpHost("127.0.0.1", 8888, "http");
      }

      clientObj = new OAuthClient(new HttpClient4(proxy));
    }
    return clientObj;
  }

  public OAuthAccessor getAccessor() throws IOException {
    if (accessorObj == null) {
      accessorObj = buildAccessor();
    }
    return accessorObj;
  }

  public OAuthAccessor buildAccessor() throws IOException {
    OAuthConsumer consumer = new OAuthConsumer(null, xeroCredentials.getXeroConsumerKey(), null, null);
    consumer.setProperty(RSA_SHA1.PRIVATE_KEY, getPrivateKey());
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);

    OAuthAccessor accessor = new OAuthAccessor(consumer);
    accessor.accessToken = xeroCredentials.getXeroConsumerKey();
    accessor.tokenSecret = xeroCredentials.getXeroConsumerSecret();

    return accessor;
  }

  private String getPrivateKey() throws IOException {
    String privateKeyPath = xeroCredentials.getPrivateKeyPath();

    File file = new File(privateKeyPath);
    if (!file.exists()) {
      String error = String.format("File doesn't exist: %s", file.getAbsolutePath());
      log.error(error);
      throw new IllegalStateException(error);
    }

    BufferedReader reader = new BufferedReader(new FileReader(file));
    try {
      StringBuilder stringBuilder = new StringBuilder();
      String line = null;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line).append("\n");
      }
      return stringBuilder.toString();
    } finally {
      reader.close();
    }
  }

  public Item getItem(String itemNum) throws XeroClientUnexpectedException, IOException {
    List<Item> items = new ArrayList<>();
    try {
      String url = endpointUrl + "Items/" + itemNum;
      OAuthMessage oResponse = getClient().invoke(getAccessor(), OAuthMessage.GET, url, null);
      ResponseType response = XeroXmlManager.xmlToResponse(oResponse.getBodyAsStream());
      items.addAll(response.getItems().getItem());
      log.debug("Response id {}", response.getId());
      if (items.size() > 1) {
        throw new XeroClientUnexpectedException("More than one item found for itemcode " + itemNum);
      }
      return items.get(0);
    } catch (OAuthProblemException ex) {
      if (ex.getHttpStatusCode() == 404) {
        return null;
      } else {
        throw new XeroClientUnexpectedException(ex.getMessage(), ex);
      }
    } catch (OAuthException | URISyntaxException e) {
      throw new XeroClientUnexpectedException(e.getMessage(), e);
    }
  }

  public String getTrackingCategoryOutput()
      throws XeroClientException, XeroClientUnexpectedException {
    try {
      String url = endpointUrl + "TrackingCategories";
      OAuthMessage oResponse = getClient().invoke(getAccessor(), OAuthMessage.GET, url, null);
      final InputStream stream = oResponse.getBodyAsStream();
      StringWriter strWriter = new StringWriter();
      IOUtils.copy(stream,strWriter,"UTF-8");
      final String output = strWriter.toString();
      strWriter.close();
      return output;
    } catch (OAuthProblemException ex) {
      if (ex.getHttpStatusCode() == 404) {
        log.debug("Server responded with 'not found' for contact search");
        return null;
      } else {
        log.error(ex.getMessage(), ex);
        throw new XeroClientException("Error getting contacts", ex);
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
  }

  public List<TrackingCategory> getTrackingCategories() throws XeroClientException, XeroClientUnexpectedException {
    List<TrackingCategory> contact = new ArrayList<>();
    try {
      String url = endpointUrl + "TrackingCategories";
      OAuthMessage oResponse = getClient().invoke(getAccessor(), OAuthMessage.GET, url, null);
      ResponseType response = XeroXmlManager.xmlToResponse(oResponse.getBodyAsStream());
      contact.addAll(response.getTrackingCategories().getTrackingCategory());
      log.debug("Response id {}", response.getId());
    } catch (OAuthProblemException ex) {
      if (ex.getHttpStatusCode() == 404) {
        log.debug("Server responded with 'not found' for tracking search");
        return contact;
      } else {
        log.error(ex.getMessage(), ex);
        throw new XeroClientException("Error getting tracking categories", ex);
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
    return contact;
  }


  public List<Contact> getContacts(String customerNumber) throws XeroClientException, XeroClientUnexpectedException {
    List<Contact> contact = new ArrayList<>();
    try {
      String url = endpointUrl + "Contacts/" + customerNumber;
      OAuthMessage oResponse = getClient().invoke(getAccessor(), OAuthMessage.GET, url, null);
      ResponseType response = XeroXmlManager.xmlToResponse(oResponse.getBodyAsStream());
      contact.addAll(response.getContacts().getContact());
      log.debug("Response id {}", response.getId());
    } catch (OAuthProblemException ex) {
      if (ex.getHttpStatusCode() == 404) {
        log.debug("Server responded with 'not found' for contact search");
        return contact;
      } else {
        log.error(ex.getMessage(), ex);
        throw new XeroClientException("Error getting contacts", ex);
      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
    return contact;
  }

  public List<Invoice> getInvoices(String invoiceNumber) throws XeroClientException, XeroClientUnexpectedException {
    List<Invoice> arrayOfInvoices = new ArrayList<>();
    try {
      OAuthMessage response = getClient().invoke(getAccessor(), OAuthMessage.GET, endpointUrl + "Invoices/" + invoiceNumber,
                                                 null);
      arrayOfInvoices.addAll(XeroXmlManager.xmlToInvoices(response.getBodyAsStream()).getInvoice());
    } catch (OAuthProblemException ex) {
      if (ex.getHttpStatusCode() == 404) {
        return arrayOfInvoices;
      } else {
        throw new XeroClientException("Error getting invoice", ex);
      }
    } catch (Exception ex) {
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
    return arrayOfInvoices;
  }

  @Override
  public boolean doesSalesInvoiceNumberExistInXero(String invoiceNumber) throws XeroClientException, XeroClientUnexpectedException {
    List<Invoice> invoiceListArray = getInvoices(invoiceNumber);
    boolean foundInvoice = false;
    if (invoiceListArray != null) {
      for (Invoice invoice : invoiceListArray) {
        if (invoice.getType()== InvoiceType.ACCREC) {
          log.info("Found existing ACCREC sales invoice in xero");
          foundInvoice = true;
        }
      }
    }
    return foundInvoice;
  }

  public ArrayOfInvoice getInvoices() throws XeroClientException, XeroClientUnexpectedException {
    ArrayOfInvoice arrayOfInvoices;
    try {
      OAuthMessage response = getClient().invoke(getAccessor(), OAuthMessage.GET, endpointUrl + "Invoices", null);
      arrayOfInvoices = XeroXmlManager.xmlToInvoices(response.getBodyAsStream());
    } catch (OAuthProblemException ex) {
      throw new XeroClientException("Error getting invoices", ex);
    } catch (Exception ex) {
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
    return arrayOfInvoices;
  }

  public Report getReport(String reportUrl) throws XeroClientException, XeroClientUnexpectedException {
    Report report = null;
    try {
      OAuthMessage response = getClient().invoke(getAccessor(), OAuthMessage.GET, endpointUrl + "Reports" + reportUrl, null);
      ResponseType responseType = XeroXmlManager.xmlToResponse(response.getBodyAsStream());
      if (responseType != null && responseType.getReports() != null && responseType.getReports().getReport() != null
          && responseType.getReports().getReport().size() > 0) {
        report = responseType.getReports().getReport().get(0);
      }
    } catch (OAuthProblemException ex) {
      throw new XeroClientException("Error getting invoices", ex);
    } catch (Exception ex) {
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
    return report;
  }

  public OAuthMessage postContacts(ArrayOfContact arrayOfContact) throws XeroClientException,
                                                                         XeroClientUnexpectedException {
    try {
      String contactsString = XeroXmlManager.contactsToXml(arrayOfContact);
      return getClient().invoke(getAccessor(), OAuthMessage.POST, endpointUrl + "Contacts",
                                OAuth.newList("xml", contactsString));
    } catch (OAuthProblemException ex) {
      throw new XeroClientException("Error posting contacts", ex);
    } catch (Exception ex) {
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
  }


  @Override
  public OAuthMessage postCreditNote(CreditNote creditNote, @Nullable LogBus logBus)
      throws XeroClientException, XeroClientUnexpectedException {
      try {
        String creditNoteStr = XeroXmlManager.creditNoteToXml(creditNote);
        return getClient().invoke(getAccessor(), OAuthMessage.POST, endpointUrl + "CreditNotes", OAuth.newList("xml",creditNoteStr));
      } catch (OAuthProblemException ex) {
        log.error(ex.toString(), ex);
        if (logBus != null) {
          logBus.log(Level.ERROR, ex.toString());
        }
        throw new XeroClientException("Error posting credit note", ex);
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        if (logBus != null) {
          logBus.log(Level.ERROR, ex.toString());
        }
        throw new XeroClientUnexpectedException(ex.getMessage(), ex);
      }
  }

  public OAuthMessage postInvoices(ArrayOfInvoice arrayOfInvoices, @Nullable LogBus logBus) throws XeroClientException,
                                                                                                   XeroClientUnexpectedException {
    try {
      String invString = XeroXmlManager.invoicesToXml(arrayOfInvoices);
      return getClient().invoke(getAccessor(), OAuthMessage.POST, endpointUrl + "Invoices", OAuth.newList("xml", invString));
    } catch (OAuthProblemException ex) {
      log.error(ex.toString(), ex);
      if (logBus != null) {
        logBus.log(Level.ERROR, ex.toString());
      }
      throw new XeroClientException("Error posting invoices", ex);
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      if (logBus != null) {
        logBus.log(Level.ERROR, ex.toString());
      }
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
  }


  public OAuthMessage postPayments(ArrayOfPayment arrayOfPayment) throws XeroClientException,
                                                                         XeroClientUnexpectedException {
    try {
      String paymentsString = XeroXmlManager.paymentsToXml(arrayOfPayment);
      return getClient().invoke(getAccessor(), OAuthMessage.POST, endpointUrl + "Payments",
                                OAuth.newList("xml", paymentsString));
    } catch (OAuthProblemException ex) {
      throw new XeroClientException("Error posting payments", ex);
    } catch (Exception ex) {
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    }
  }

  public File getInvoiceAsPdf(String invoiceId) throws XeroClientException, XeroClientUnexpectedException {

    File file = null;
    InputStream in = null;
    FileOutputStream out = null;

    try {
      OAuthMessage request = getAccessor().newRequestMessage(OAuthMessage.GET, endpointUrl + "Invoices" + "/" + invoiceId,
                                                             null);
      request.getHeaders().add(new OAuth.Parameter("Accept", "application/pdf"));
      OAuthResponseMessage response = getClient().access(request, ParameterStyle.BODY);

      file = new File("Invoice-" + invoiceId + ".pdf");

      if (response != null && response.getHttpResponse() != null
          && (response.getHttpResponse().getStatusCode() / 2) != 2) {
        in = response.getBodyAsStream();
        out = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = in.read(buffer)) != -1) {
          out.write(buffer, 0, bytesRead);
        }
      } else {
        throw response.toOAuthProblemException();
      }

    } catch (OAuthProblemException ex) {
      throw new XeroClientException("Error getting PDF of invoice " + invoiceId, ex);
    } catch (Exception ex) {
      throw new XeroClientUnexpectedException(ex.getMessage(), ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
      }
      try {
        if (out != null) {
          out.flush();
          out.close();
        }
      } catch (IOException ex) {
      }
    }
    return file;
  }

}
