package com.pi.xerosync.xeroservice;

import com.rossjourdain.jaxb.LineItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * User: thomas Date: 18/02/14
 */
public class InvoiceLineItems {

  private BigDecimal total;
  private List<LineItem> xeroLineItemList = new ArrayList<>();

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public List<LineItem> getXeroLineItemList() {
    return xeroLineItemList;
  }

}
