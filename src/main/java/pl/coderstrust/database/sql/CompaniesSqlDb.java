package pl.coderstrust.database.sql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Payment;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CompaniesSqlDb implements Database<Company> {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public long addEntry(Company entry) {

    long id = jdbcTemplate
        .queryForObject(SqlQueries.ADD_COMPANY_SQL, companyObject(entry), Long.class);

    for (int i = 0; i < entry.getPayments().size(); i++) {
      jdbcTemplate.update(SqlQueries.ADD_COMPANY_PAYMENTS_SQL,
          companyPaymentObject(entry.getPayments().get(i), id));
    }
    return id;
  }

  @Override
  public void deleteEntry(long id) {
    jdbcTemplate.update(SqlQueries.DELETE_COMPANY_BY_ID, id);
    jdbcTemplate.update(SqlQueries.DELETE_COMPANY_PAYMENTS_BY_COMPANY_ID, id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Company getEntryById(long id) {
    List<Payment> payment = jdbcTemplate
        .query(SqlQueries.GET_PAYMENTS_BY_COMPANY_ID, new Object[]{id},
            new CompanyPaymentRowMapper());

    return (Company) jdbcTemplate
        .queryForObject(SqlQueries.GET_COMPANY_PAYMENTS_BY_COMPANY_ID, new Object[]{id},
            new CompanyRowMapper(payment));
  }

  @Override
  public void updateEntry(Company entry) {
    long id = entry.getId();
    jdbcTemplate.update(SqlQueries.DELETE_COMPANY_PAYMENTS_BY_COMPANY_ID, id);

    for (int i = 0; i < entry.getPayments().size(); i++) {
      jdbcTemplate.update(SqlQueries.ADD_COMPANY_PAYMENTS_SQL,
          companyPaymentObject(entry.getPayments().get(i), id));
    }

    jdbcTemplate.update(SqlQueries.UPDATE_COMPANY + id, companyObject(entry));
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Company> getEntries() {
    //TODO Company returns null for Payments while getting company by date
    return jdbcTemplate
        .query(SqlQueries.GET_COMPANIES_IN_SPECIFIC_DATE_RANGE, new CompanyRowMapper());
  }

  @Override
  public boolean idExist(long idLong) {
    return jdbcTemplate.queryForObject(SqlQueries.CHECK_IS_THERE_AN_ID + idLong, Long.class)
        .equals(idLong);
  }

  private Object[] companyObject(Company entry) {
    Date date = Date.from(entry.getIssueDate().atStartOfDay()
        .atZone(ZoneId.systemDefault()).toInstant());
    return new Object[]{entry.getName(), date, entry.getAddress(), entry.getCity(),
        entry.getZipCode(), entry.getNip(), entry.getBankAccoutNumber(),
        entry.getTaxType().toString(), entry.isPersonalCarUsage()};
  }

  private Object[] companyPaymentObject(Payment entry, long id) {
    Date date = Date.from(entry.getIssueDate().atStartOfDay()
        .atZone(ZoneId.systemDefault()).toInstant());
    return new Object[]{date, entry.getAmount(),
        entry.getType().toString(), id};
  }
}