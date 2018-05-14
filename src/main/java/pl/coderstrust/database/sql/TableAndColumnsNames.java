package pl.coderstrust.database.sql;

class TableAndColumnsNames {

  //Tables names
  static final String TABLE_COMPANY = "company";
  static final String TABLE_COMPANY_PAYMENTS = "company_payments";

  //Table company
  static final String COMPANY_NAME = "company_name";
  static final String COMPANY_ISSUE_DATE = "issue_date";
  static final String ADDRESS = "address";
  static final String CITY = "city";
  static final String ZIP_CODE = "zip_code";
  static final String NIP = "nip";
  static final String BANK_ACCOUNT_NUMBER = "bank_account_number";
  static final String TAX_TYPE = "tax_type";
  static final String CAR_USER = "car_user";
  static final String COMPANY_ID = "id";

  //Table company_payments
  static final String COMPANY_PAYMENTS_ISSUE_DATE = "issue_date";
  static final String AMOUNT = "amount";
  static final String PAYMENT_TYPE = "payment_type";
  static final String COMPANY_ID_FOR_PAYMENTS = "id_company";
  static final String COMPANY_PAYMENTS_ID = "id";
}