package pl.coderstrust.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Payment implements WithValidation {

  private long id;
  private LocalDate issueDate;
  private BigDecimal amount;
  private PaymentType type;

  public Payment(long id, LocalDate issueDate, BigDecimal amount,
      PaymentType type) {
    this.id = id;
    this.issueDate = issueDate;
    this.amount = amount;
    this.type = type;
  }

  public Payment() {
  }

  @Override
  public List<String> validate() {
    List<String> errors = new ArrayList<>();
    if (issueDate == null) {
      errors.add("Date is empty.");
    }
    if (amount == null) {
      errors.add("Amount is empty.");
    }
    if (type == null) {
      errors.add("Payment type is empty.");
    }
    return errors;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public LocalDate getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public PaymentType getType() {
    return type;
  }

  public void setType(PaymentType type) {
    this.type = type;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object object) {
    return EqualsBuilder.reflectionEquals(this, object);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, true);
  }

}


