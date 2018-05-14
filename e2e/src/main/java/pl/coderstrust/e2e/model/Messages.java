package pl.coderstrust.e2e.model;


public class Messages {

  public static final String CONTROLLER_INVOICE_ADDED = "Entry added under id : ";

  public static final String PRODUCTS_LIST_EMPTY = "Products list is empty.";
  public static final String PAYMENT_STATE_EMPTY = "Payment state is undefined.";

  public static final String COMPANY_NO_NAME = "Company name is empty.";
  public static final String COMPANY_NO_ADRESS = "Company address is empty.";
  public static final String COMPANY_NO_CITY = "Company city is empty.";
  public static final String COMPANY_NO_ZIPCODE = "Company zip code is empty.";
  public static final String COMPANY_NO_BACC = "Company bank account number is empty.";
  public static final String COMPANY_NO_NIP = "Company NIP number is empty.";

  public static final String DATE_EMPTY = "Date is empty.";
  public static final String DATE_TOO_EARLY = "Date is earlier then actual date.";
  public static final String PRODUCT_NO_NET_VALUE = "Product net value is empty.";

  public static final String PRODUCT_NO_NAME = "Product name is empty.";
  public static final String PRODUCT_NO_DESCRIPTION = "Product description is empty.";
  public static final String PRODUCT_WRONG_NET_VALUE = "Product "
      + "net value is negative or equal to zero.";
  public static final String PRODUCT_NO_VAT = "Product vat rate is empty";
  public static final String PRODUCT_INCORRECT_AMOUNT = "Product amount is negative or zero.";
  public static final String COMPANY_ID_NOT_MATCH =
      "Incorrect company ID not matching buyer and/or seller ID at the requested invoice.";
  public static final String COMPANY_NOT_EXIST = "Company with this id doesn't exist";
  public static final String INCORRECT_YEAR = "Wrong Year";
}
