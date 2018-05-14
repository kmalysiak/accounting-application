package pl.coderstrust.e2e.model;

public class DbException extends RuntimeException {

  public DbException(String message) {
    super(message);
  }

  public DbException(String message, Exception previousException) {
    super(message, previousException);
  }

}
