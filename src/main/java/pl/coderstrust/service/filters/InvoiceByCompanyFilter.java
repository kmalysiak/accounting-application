package pl.coderstrust.service.filters;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceByCompanyFilter implements EntriesFilter<Invoice> {

  @Qualifier("filterWithCompanies")
  Database<Company> dbCompanies;

  private InvoiceByCompanyFilter(@Qualifier("companiesDatabase") Database<Company> dbCompanies) {
    this.dbCompanies = dbCompanies;
  }

  @Override
  public boolean hasField(Invoice entry, long companyId) {
    return hasBuyerOrSeller(entry, companyId);
  }

  @Override
  public boolean hasObject(Invoice entry, Object company) {
    return hasBuyerOrSeller(entry, (Company) company);
  }

  @Override
  public boolean hasObjectById(Invoice entry, long companyId) {
    return hasBuyerOrSeller(entry, dbCompanies.getEntryById(companyId));
  }

  @Override
  public List<Invoice> filterByField(List<Invoice> entries, long companyId) {
    return entries.stream()
        .filter(line -> hasBuyerOrSeller(line, companyId))
        .collect(Collectors.toList());
  }

  private boolean hasBuyerOrSeller(Invoice entry, long filterId) {
    return entry.getSeller().getId() == filterId
        || entry.getBuyer().getId() == filterId;
  }

  private boolean hasBuyerOrSeller(Invoice entry, Company company) {
    return entry.getSeller().equals(company)
        || entry.getBuyer().equals(company);
  }
}
