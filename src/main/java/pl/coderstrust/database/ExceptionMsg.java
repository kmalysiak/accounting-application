package pl.coderstrust.database;

public class ExceptionMsg {

  public static final String INVOICE_NOT_EXIST =
      "Entry does not exist in database.";
  public static final String IO_ERROR_WHILE_DELETING =
      "Filesystem I/O error during deleting entry. Please try again later.";
  public static final String INVOICE_PROCESSING_INTERRUPT =
      "Entry processing interrupted. Please try again later.";
  public static final String IO_ERROR_WHILE_READING =
      "Filesystem I/O error reading entry. Please try again later.";
  public static final String IO_ERROR_WHILE_ADDING =
      "Filesystem I/O error adding entry. Please try again later.";
  public static final String INTERNAL_PROCESSING_ERROR =
      "Internal entry processing error. Please try again later";
  public static final String IO_ERROR_WHILE_INITIALIZING =
      "Filesystem I/O error while initializing. Please try again later";
}
