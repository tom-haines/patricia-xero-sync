package com.pi.xerosync.dbservice;

import com.pi.xerosync.dbconnect.ConnectionManager;
import com.pi.xerosync.service.LogBus;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ch.qos.logback.classic.Level;
import com.rossjourdain.jaxb.Address;
import com.rossjourdain.jaxb.AddressType;
import com.rossjourdain.jaxb.ArrayOfAddress;
import com.rossjourdain.jaxb.Contact;
import com.rossjourdain.jaxb.CurrencyCode;

/**
 * User: thomas Date: 17/02/14
 */
@Singleton
public class PatriciaDbContactExtractorImpl implements PatriciaDbContactExtractor {

  private final Logger log = LoggerFactory.getLogger(PatriciaDbContactExtractorImpl.class);

  @Inject
  private ConnectionManager connectionManager;

  @Override
  public String getCountry(Integer actorId) throws SQLException, IOException {
    String country = null;
    PreparedStatement exactStmt = null;
    Connection conn = connectionManager.getDataSource().getConnection();
    try {
      exactStmt = conn.prepareStatement(
              "SELECT * FROM dbo.EXACT_ACTOR WHERE EX_ACTOR_ID=? AND EX_COUNTRY IS NOT NULL AND EX_COUNTRY!='' ORDER BY EX_SAVED_DATE DESC");

      exactStmt.setInt(1, actorId);
      ResultSet exactResult = exactStmt.executeQuery();
      if (!exactResult.next()) {
        throw new SQLException("Could not find exported actor record for actorId to determine country " + actorId);
      }
      country = exactResult.getString("EX_COUNTRY");

    } finally {
      DbUtils.close(exactStmt);
      DbUtils.close(conn);
    }
    return country;
  }

  @Override
  public Contact getContact(Integer actorId, LogBus logBus) throws IOException, SQLException {
    Contact contact = new Contact();
    contact.setContactNumber(actorId.toString());
    PreparedStatement exactStmt = null;
    PreparedStatement patNamesStmt = null;
    Connection patriciaCon = connectionManager.getDataSource().getConnection();
    try {
      exactStmt = patriciaCon
          .prepareStatement("SELECT * FROM dbo.EXACT_ACTOR WHERE EX_ACTOR_ID=? ORDER BY EX_SAVED_DATE DESC");
      exactStmt.setInt(1, actorId);
      ResultSet exactResult = exactStmt.executeQuery();
      if (!exactResult.next()) {
        throw new SQLException("Could not find exported actor record for actorId in EXACT_ACTOR table " + actorId);
      }
      String country = StringUtils.trimToEmpty(exactResult.getString("EX_COUNTRY"));
      String address1 = StringUtils.trimToNull(exactResult.getString("EX_ADRESS_1"));
      String address2 = StringUtils.trimToNull(exactResult.getString("EX_ADRESS_2"));
      String address3 = StringUtils.trimToNull(exactResult.getString("EX_ADRESS_3"));
      String exCity = StringUtils.trimToNull(exactResult.getString("EX_CITY"));

      String email = exactResult.getString("EX_E_MAIL_ADRESS");
      if (StringUtils.isNotBlank(email) && EmailValidator.getInstance().isValid(email)) {
        contact.setEmailAddress(email);
      } else if (StringUtils.isNotBlank(email)) {
        log.info("Email not valid: " + email);
        if (email.contains(";")) {
          String[] emails = email.split(";[ ]?");
          StringBuilder strB = new StringBuilder();
          for (String emailAddr : emails) {
            if (strB.length() == 0) {
              strB.append(emailAddr.trim());
            } else {
              String emailMod = emailAddr.trim().replace("@", "!!");
              strB.insert(0, emailMod + "&&");
            }

            // switch the first '@' to the end

            if (EmailValidator.getInstance().isValid(strB.toString())) {
              log.info("Set email to modified version to work around xero restriction: " + strB.toString());
              contact.setEmailAddress(strB.toString());
            } else {
              log.info("Modified version of email not valid syntax: " + strB.toString());
              logBus.log(Level.INFO, "Email not valid: " + email);
            }
          }
        }
      }

      if (StringUtils.isBlank(country)) {
        log.warn("Country was blank for actor ID {}", actorId);
        logBus.log(Level.WARN, "Country was blank for actor ID " + actorId + " using AU as default");
        country = "AU";
      }

      // set default tax codes based on contact location
      switch (country) {
        case "AU":
          contact.setAccountsPayableTaxType("INPUT");
          contact.setAccountsReceivableTaxType("OUTPUT");
          contact.setDefaultCurrency(CurrencyCode.AUD);
          break;
        default:
          contact.setAccountsPayableTaxType("GSTONIMPORTS");
          contact.setAccountsReceivableTaxType("EXEMPTEXPORT");
          break;
      }

      Address address = new Address();
      address.setAddressLine1(address1);
      address.setAddressLine2(address2);
      address.setAddressLine3(address3);
      address.setCity(exCity);

      address.setCountry(country);
      // use mailing address
      address.setAddressType(AddressType.POBOX);

      boolean isLegalEntity = isLegalEntity(actorId);

      ArrayOfAddress aAddresses = new ArrayOfAddress();
      aAddresses.getAddress().add(address);
      contact.setAddresses(aAddresses);

      patNamesStmt = patriciaCon
          .prepareStatement("SELECT * FROM dbo.PAT_NAMES_ENTITY WHERE NAME_ID=? ORDER BY ENTITY_ID_CREATED_DATE DESC");
      patNamesStmt.setInt(1, actorId);
      ResultSet patResult = patNamesStmt.executeQuery();
      if (!patResult.next()) {
        throw new SQLException("Could not find exported actor record for in PAT_NAMES_ENTITY for actorId " + actorId);
      }
      log.debug(patResult.getString("ENTITY_ID_CREATED_DATE"));

      if (isLegalEntity) {
        String officialName = patResult.getString("OFFICIAL_NAME_NEW");
        if (StringUtils.isBlank(officialName)) {
          // see if we can find a recent official name
          PreparedStatement findOfficialStmt = patriciaCon
              .prepareStatement(
                  "SELECT OFFICIAL_NAME_NEW FROM dbo.PAT_NAMES_ENTITY WHERE NAME_ID=? AND OFFICIAL_NAME_NEW IS NOT NULL ORDER BY ENTITY_ID_CREATED_DATE DESC");
          findOfficialStmt.setInt(1, actorId);
          ResultSet offResult = findOfficialStmt.executeQuery();
          if (offResult.next()) {
            officialName = offResult.getString(1);
          }
          offResult.close();
          DbUtils.close(findOfficialStmt);
        }
        if (StringUtils.isBlank(officialName)) {
          throw new SQLException("The official name must be set for patricia actor ID " + actorId);
        }

        contact.setName(officialName);
      } else {
        String name1 = patResult.getString("NAME1");
        String name3 = patResult.getString("NAME3");
        if (StringUtils.isBlank(name1) && StringUtils.isBlank(name3)) {
          throw new SQLException("The first and last name must be set for an individual see patricia actor ID " + actorId);
        }

        contact.setName(name1.toUpperCase() + ", " + name3);
        contact.setFirstName(name3);
        contact.setLastName(name1);
      }

    } finally {
      DbUtils.close(exactStmt);
      DbUtils.close(patNamesStmt);
      DbUtils.close(patriciaCon);
    }

    return contact;  }

  @Override
  public boolean isLegalEntity(Integer actorId) throws SQLException, IOException {
    boolean legalEntity = true;
    PreparedStatement findLegalStatus = null;
    ResultSet results = null;
    Connection conn = connectionManager.getDataSource().getConnection();
    try {
      // search the database to find this record, and check the legal entity field
      findLegalStatus = conn.prepareStatement("SELECT IS_LEGAL_ENTITY FROM dbo.PAT_NAMES WHERE NAME_ID=?");
      findLegalStatus.setInt(1, actorId);
      results = findLegalStatus.executeQuery();
      if (results.next()) {
        legalEntity = (results.getInt(1) == 1);
      }
    } finally {
      DbUtils.close(results);
      DbUtils.close(findLegalStatus);
      DbUtils.close(conn);
    }
    return legalEntity;
  }
}
