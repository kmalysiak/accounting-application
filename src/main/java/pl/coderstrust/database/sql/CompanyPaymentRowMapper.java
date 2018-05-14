package pl.coderstrust.database.sql;

import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyPaymentRowMapper implements RowMapper {

  @Override
  public Payment mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
    Payment payments = new Payment();

    if (!resultSet.wasNull()) {
      payments.setId(resultSet.getInt(TableAndColumnsNames.COMPANY_PAYMENTS_ID));
      payments.setIssueDate(resultSet.getDate(TableAndColumnsNames.COMPANY_ISSUE_DATE)
          .toLocalDate());
      payments.setAmount(resultSet.getBigDecimal(TableAndColumnsNames.AMOUNT));
      payments.setType(PaymentType.valueOf(resultSet.getString(TableAndColumnsNames.PAYMENT_TYPE)));
    }
    return payments;
  }
}