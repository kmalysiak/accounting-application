package pl.coderstrust.e2e.testHelpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectMapperHelper {

  private ObjectMapper jsonMapper;

  public ObjectMapperHelper() {
    jsonMapper = new ObjectMapper();
    jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jsonMapper.registerModule(new JavaTimeModule());
  }

  public String toJson(Object value) {
    try {
      return jsonMapper.writeValueAsString(value);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Mapper failed at conversion from object to Json.", ex);
    }
  }

  public Invoice toInvoice(String json) {
    try {
      return jsonMapper.readValue(json, Invoice.class);
    } catch (IOException ex) {
      throw new RuntimeException("Mapper failed at conversion from Json to object.", ex);
    }
  }

  public List<Invoice> toInvoiceList(String json) {
    try {
      return jsonMapper.readValue(json,
          jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, Invoice.class));
    } catch (IOException ex) {
      throw new RuntimeException("Mapper failed at conversion from Json to list of object.", ex);
    }
  }
  public List<Company> toCompanyList(String json) {
    try {
      return jsonMapper.readValue(json,
          jsonMapper.getTypeFactory().constructCollectionType(ArrayList.class, Company.class));
    } catch (IOException ex) {
      throw new RuntimeException("Mapper failed at conversion from Json to list of object.", ex);
    }
  }
}
