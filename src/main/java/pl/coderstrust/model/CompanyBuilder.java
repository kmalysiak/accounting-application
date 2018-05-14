package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.List;

public class CompanyBuilder {

  private Company company = new Company();

  public CompanyBuilder(String name) {
    company.setName(name);
  }

  public CompanyBuilder setId(long id) {
    company.setId(id);
    return this;
  }

  public CompanyBuilder setIssueDate(LocalDate date) {
    company.setIssueDate(date);
    return this;
  }

  public CompanyBuilder setAddress(String address) {
    company.setAddress(address);
    return this;
  }

  public CompanyBuilder setCity(String city) {
    company.setCity(city);
    return this;
  }

  public CompanyBuilder setZipCode(String zipCode) {
    company.setZipCode(zipCode);
    return this;
  }

  public CompanyBuilder setNip(String nip) {
    company.setNip(nip);
    return this;
  }

  public CompanyBuilder setTaxType(TaxType taxType) {
    company.setTaxType(taxType);
    return this;
  }

  public CompanyBuilder setBankAccoutNumber(String bankAccoutNumber) {
    company.setBankAccoutNumber(bankAccoutNumber);
    return this;
  }

  public CompanyBuilder setIsCarPersonalUsage(boolean isCarPersonalUsage) {
    company.setPersonalCarUsage(isCarPersonalUsage);
    return this;
  }

  public CompanyBuilder setPayments(List<Payment> payments) {
    company.setPayments(payments);
    return this;
  }

  public Company build() {
    return company;
  }
}

