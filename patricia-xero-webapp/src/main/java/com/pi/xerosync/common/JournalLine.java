package com.pi.xerosync.common;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class JournalLine {

  public int psj_cost_category = 0;
  public String psj_workcode_type = "";
  public String psj_journal_type = "";
  public int psj_line_number = 0;
  public int psj_journal_number = 0;
  public String psj_entry_number = "";
  public String psj_description = "";
  public String psj_comment = "";

  public LocalDate psj_date = null;
  public String psj_general_ledger_account = "";
  public String psj_debtor_number = "";
  public String psj_workcode = "";
  public String psj_creditor_number = "";
  public BigDecimal psj_amount = BigDecimal.ZERO;
  public BigDecimal psj_amount_p_c_d = BigDecimal.ZERO;
  public BigDecimal psj_vat_amount = BigDecimal.ZERO;
  public String psj_currency = "";
  public BigDecimal psj_exchange_rate = BigDecimal.ONE;
  public String psj_due_date_invoice = "";
  public String psj_due_date_payment = "";
  public int psj_vat_code = 0;

  private String psj_payment_ref = "";

  public Integer psj_case_id = -1;
  public String teamLeader = null;

  public String psj_cost_center = "";
  public String psj_loginid = "";
  public String psj_cost_unit = "";
  public double psj_quantity = 0.0;
  public boolean gstLiable = false;

  /**
   * @return the psj_payment_ref
   */
  public String getPsj_payment_ref() {
    return psj_payment_ref;
  }

  /**
   * @param psj_payment_ref the psj_payment_ref to set
   */
  public void setPsj_payment_ref(String psj_payment_ref) {
    // MYOB can only handle a payment ref of 20 chars or less
    final int MAX_LENGTH = 20;
    if (psj_payment_ref != null && psj_payment_ref.length() > MAX_LENGTH) {
      this.psj_payment_ref = psj_payment_ref.substring(0, MAX_LENGTH);
    } else {
      this.psj_payment_ref = psj_payment_ref;
    }

  }
}
