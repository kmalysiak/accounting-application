package pl.coderstrust.e2e.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Invoice {

  List<InvoiceEntry> products;
  @JsonProperty("invoiceId")
  private long id;
  private String name;
  private Company buyer;
  private Company seller;
  private LocalDate issueDate;
  private LocalDate paymentDate;
  private PaymentState paymentState;
}
