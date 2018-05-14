package pl.coderstrust.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    return mapper;
  }

  @Bean
  public Formatter<LocalDate> localDateFormatter() {
    return new Formatter<LocalDate>() {
      @Override
      public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
      }

      @Override
      public String print(LocalDate object, Locale locale) {
        return DateTimeFormatter.ISO_DATE.format(object);
      }
    };
  }
}
