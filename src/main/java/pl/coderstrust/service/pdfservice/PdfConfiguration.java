package pl.coderstrust.service.pdfservice;

import java.math.BigDecimal;

public class PdfConfiguration {

  public static final float DEFAULT_MARGIN_SIZE = 20;
  public static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
  public static final int ROUND_DIGITS_BIG_DECIMAL = 4;
  public static final int ROUND_MODE_BIG_DECIMAL = BigDecimal.ROUND_HALF_UP;
  public static final int COMPANIES_TABLE_COLUMNS_COUNT = 3;
  public static final int PRODUCTS_TABLE_COLUMNS_COUNT = 6;
  public static final float TABLE_SPACING = 15;
  protected static final String BASE_FONT_NAME = "DejaVuSans.ttf";
  protected static final int DEFAULT_FONT_SIZE = 12;
  protected static final int HEADER_DEFAULT_FONT_SIZE = 20;
}
