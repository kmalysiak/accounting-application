package pl.coderstrust.database.sql;

import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.TaxType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CompanyRowMapper implements RowMapper {

  private List<Payment> payments;

  CompanyRowMapper(List<Payment> payments) {
    this.payments = payments;
  }

  CompanyRowMapper() {
  }

  @Override
  public Company mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
    Company company = new Company();

    if (!resultSet.wasNull()) {
      company.setId(resultSet.getInt(TableAndColumnsNames.COMPANY_ID));
      company.setName(resultSet.getString(TableAndColumnsNames.COMPANY_NAME));
      company.setIssueDate(resultSet.getDate(TableAndColumnsNames.COMPANY_ISSUE_DATE)
          .toLocalDate());
      company.setAddress(resultSet.getString(TableAndColumnsNames.ADDRESS));
      company.setCity(resultSet.getString(TableAndColumnsNames.CITY));
      company.setZipCode(resultSet.getString(TableAndColumnsNames.ZIP_CODE));
      company.setNip(resultSet.getString(TableAndColumnsNames.NIP));
      company.setTaxType(TaxType.valueOf(resultSet.getString(TableAndColumnsNames.TAX_TYPE)));
      company.setBankAccoutNumber(resultSet.getString(TableAndColumnsNames.BANK_ACCOUNT_NUMBER));
      company.setPersonalCarUsage(resultSet.getBoolean(TableAndColumnsNames.CAR_USER));
      company.setPayments(payments);
    }
    return company;
  }
}