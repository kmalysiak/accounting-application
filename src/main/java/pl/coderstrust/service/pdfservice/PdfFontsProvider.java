package pl.coderstrust.service.pdfservice;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PdfFontsProvider {

  private final Logger logger = LoggerFactory.getLogger(PdfFontsProvider.class);
  private Font headerPropertyFont;
  private Font headerValueFont;
  private Font propertyFont;
  private Font valueFont;

  public PdfFontsProvider() {
    BaseFont baseFont;
    try {
      baseFont = BaseFont
          .createFont(PdfConfiguration.BASE_FONT_NAME, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    } catch (DocumentException ex) {
      logger.warn(
          " from PdfFontsProvider in PdfFontsProvider "
              + ExceptionMessage.PDF_BASE_FONT_INSTANTIATION_INTERRUPT, ex);
      throw new PdfServiceException(ExceptionMessage.PDF_BASE_FONT_INSTANTIATION_INTERRUPT, ex);

    } catch (IOException ex) {
      logger.warn(
          " from PdfFontsProvider in PdfFontsProvider " + ExceptionMessage.NO_FONT_FILE, ex);
      throw new PdfServiceException(ExceptionMessage.NO_FONT_FILE, ex);
    }

    propertyFont = new Font(baseFont, PdfConfiguration.DEFAULT_FONT_SIZE, Font.BOLD,
        BaseColor.BLACK);
    valueFont = new Font(baseFont, PdfConfiguration.DEFAULT_FONT_SIZE);
    headerPropertyFont = new Font(baseFont, PdfConfiguration.HEADER_DEFAULT_FONT_SIZE, Font.BOLD,
        BaseColor.BLACK);
    headerValueFont = new Font(baseFont, PdfConfiguration.HEADER_DEFAULT_FONT_SIZE, Font.NORMAL,
        BaseColor.BLACK);
  }

  public Font getHeaderPropertyFont() {
    return headerPropertyFont;
  }

  public Font getHeaderValueFont() {
    return headerValueFont;
  }

  public Font getPropertyFont() {
    return propertyFont;
  }

  public Font getValueFont() {
    return valueFont;
  }
}
