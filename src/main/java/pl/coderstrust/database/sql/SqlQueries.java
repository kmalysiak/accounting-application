package pl.coderstrust.database.sql;

class SqlQueries {

  static final String ADD_COMPANY_SQL =
      "INSERT INTO " + TableAndColumnsNames.TABLE_COMPANY
          + "(" + TableAndColumnsNames.COMPANY_NAME
          + "," + TableAndColumnsNames.COMPANY_ISSUE_DATE
          + "," + TableAndColumnsNames.ADDRESS
          + "," + TableAndColumnsNames.CITY
          + "," + TableAndColumnsNames.ZIP_CODE
          + "," + TableAndColumnsNames.NIP
          + "," + TableAndColumnsNames.BANK_ACCOUNT_NUMBER
          + "," + TableAndColumnsNames.TAX_TYPE
          + "," + TableAndColumnsNames.CAR_USER
          + ") VALUES (?,?,?,?,?,?,?,?,?) RETURNING "
          + TableAndColumnsNames.COMPANY_ID;

  static final String ADD_COMPANY_PAYMENTS_SQL =
      "INSERT INTO " + TableAndColumnsNames.TABLE_COMPANY_PAYMENTS
          + "(" + TableAndColumnsNames.COMPANY_PAYMENTS_ISSUE_DATE
          + "," + TableAndColumnsNames.AMOUNT
          + "," + TableAndColumnsNames.PAYMENT_TYPE
          + "," + TableAndColumnsNames.COMPANY_ID_FOR_PAYMENTS
          + ") VALUES (?,?,?,?)";

  static final String DELETE_COMPANY_BY_ID =
      "DELETE FROM " + TableAndColumnsNames.TABLE_COMPANY
          + " WHERE " + TableAndColumnsNames.COMPANY_ID + "=?";

  static final String DELETE_COMPANY_PAYMENTS_BY_COMPANY_ID =
      "DELETE FROM " + TableAndColumnsNames.TABLE_COMPANY_PAYMENTS
          + " WHERE " + TableAndColumnsNames.COMPANY_ID_FOR_PAYMENTS + "=?";

  static final String GET_COMPANY_PAYMENTS_BY_COMPANY_ID =
      " SELECT * FROM " + TableAndColumnsNames.TABLE_COMPANY
          + " WHERE " + TableAndColumnsNames.COMPANY_ID + "=?";

  static final String GET_PAYMENTS_BY_COMPANY_ID =
      "SELECT * FROM " + TableAndColumnsNames.TABLE_COMPANY_PAYMENTS
          + " WHERE " + TableAndColumnsNames.COMPANY_ID_FOR_PAYMENTS + "=?";

  static final String UPDATE_COMPANY =
      "UPDATE " + TableAndColumnsNames.TABLE_COMPANY + " set "
          + TableAndColumnsNames.COMPANY_NAME + "=?,"
          + TableAndColumnsNames.COMPANY_ISSUE_DATE + "=?,"
          + TableAndColumnsNames.ADDRESS + "=?,"
          + TableAndColumnsNames.CITY + "=?,"
          + TableAndColumnsNames.ZIP_CODE + "=?,"
          + TableAndColumnsNames.NIP + "=?,"
          + TableAndColumnsNames.BANK_ACCOUNT_NUMBER + "=?,"
          + TableAndColumnsNames.TAX_TYPE + "=?,"
          + TableAndColumnsNames.CAR_USER + "=? WHERE "
          + TableAndColumnsNames.COMPANY_ID + "=";

  static final String GET_COMPANIES_IN_SPECIFIC_DATE_RANGE = "SELECT * FROM "
      + TableAndColumnsNames.TABLE_COMPANY;

  static final String CHECK_IS_THERE_AN_ID = "SELECT "
      + TableAndColumnsNames.COMPANY_ID + " FROM "
      + TableAndColumnsNames.TABLE_COMPANY + " WHERE "
      + TableAndColumnsNames.COMPANY_ID + "=";
}