package pl.coderstrust.service.pdfservice;

public class PdfServiceException extends RuntimeException {

  public PdfServiceException(String message) {
    super(message);
  }

  public PdfServiceException(String message, Exception previousException) {
    super(message, previousException);
  }

}
