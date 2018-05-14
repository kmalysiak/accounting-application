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
public class Company {

  @JsonProperty("companyId")
  private long id;

  private String name;
  private LocalDate issueDate;
  private String address;
  private String city;
  private String zipCode;
  private String nip;
  private String bankAccoutNumber;
  private TaxType taxType;
  private boolean personalCarUsage;
  private List<Payment> payments;
}
