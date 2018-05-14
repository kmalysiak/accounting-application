package pl.coderstrust.service.pdfservice;

import org.springframework.context.annotation.Configuration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class PdfDateTimeProvider {

  private DateFormat dateFormat;

  public PdfDateTimeProvider() {
    dateFormat = new SimpleDateFormat(PdfConfiguration.DATE_FORMAT);
  }

  public String getDateTime() {
    return dateFormat.format(new Date());
  }
}
