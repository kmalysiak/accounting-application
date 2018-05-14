//package pl.coderstrust.database.sql;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CreateSqlTablesIfNotExists implements CommandLineRunner {
//
//  @Autowired
//  JdbcTemplate jdbcTemplate;
//
//  @Override
//  public void run(String... strings) throws Exception {
//    jdbcTemplate.update(getCompanyTable());
//    jdbcTemplate.update(getCompanyPaymentTable());
//  }
//
//  private String getCompanyTable() {
//    return "CREATE TABLE IF NOT EXISTS "
//        + TableAndColumnsNames.TABLE_COMPANY + "("
//        + TableAndColumnsNames.COMPANY_NAME + "      varchar(256),"
//        + TableAndColumnsNames.COMPANY_ISSUE_DATE + "        date,"
//        + TableAndColumnsNames.ADDRESS + "           varchar(256),"
//        + TableAndColumnsNames.CITY + "              varchar(256),"
//        + TableAndColumnsNames.ZIP_CODE + "          varchar(256),"
//        + TableAndColumnsNames.NIP + "               varchar(256),"
//        + TableAndColumnsNames.BANK_ACCOUNT_NUMBER + " varchar(256),"
//        + TableAndColumnsNames.TAX_TYPE + "          varchar(256),"
//        + TableAndColumnsNames.CAR_USER + "          boolean,"
//        + TableAndColumnsNames.COMPANY_ID + "               serial not null "
//        + "constraint company_id_pk primary key);"
//        + "create unique index if not exists company_id_index on company (id);";
//  }
//
//  private String getCompanyPaymentTable() {
//    return "CREATE TABLE IF NOT EXISTS "
//        + TableAndColumnsNames.TABLE_COMPANY_PAYMENTS + "("
//        + TableAndColumnsNames.COMPANY_PAYMENTS_ISSUE_DATE + " date,"
//        + TableAndColumnsNames.AMOUNT + "                      numeric,"
//        + TableAndColumnsNames.PAYMENT_TYPE + "                varchar(256),"
//        + TableAndColumnsNames.COMPANY_ID_FOR_PAYMENTS + "     integer,"
//        + TableAndColumnsNames.COMPANY_PAYMENTS_ID + "         serial not null "
//        + "constraint company_payments_id_pk primary key);"
//        + "create unique index if not exists company_payments_id_index on company (id);";
//  }
//}