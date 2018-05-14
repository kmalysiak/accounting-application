package pl.coderstrust.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Product;
import pl.coderstrust.testhelpers.InvoicesWithSpecifiedData;
import pl.coderstrust.testhelpers.TestCasesGenerator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser
public class InvoiceControllerIntegrationTest {

  private static final String GET_INVOICE_BY_DATE_METHOD = "getInvoiceByDate";
  private static final String GET_INVOICE_BY_ID_METHOD = "getInvoiceById";
  private static final String REMOVE_INVOICE_METHOD = "removeInvoice";
  private static final String ADD_INVOICE_METHOD = "addInvoice";
  private static final String GET_PDF_METHOD = "invoiceToPdf";
  private static final String DEFAULT_PATH = "/v1/invoice";
  private static final MediaType CONTENT_TYPE_JSON = MediaType.APPLICATION_JSON_UTF8;
  private static final MediaType CONTENT_TYPE_PDF = MediaType.APPLICATION_PDF;


  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private TestCasesGenerator generator;

  @Test
  public void shouldAddInvoices() throws Exception {
    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(generator.getTestInvoice(1, 1)))
            .contentType(CONTENT_TYPE_JSON))
        // .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(InvoicesWithSpecifiedData.getInvoiceWithPolishData()))
            .contentType(CONTENT_TYPE_JSON))
        //   .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isOk());
    //then
    this.mockMvc
        .perform(get(DEFAULT_PATH))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        // .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.[0].invoiceId ", is(1)))
        .andExpect(jsonPath("$.[0].name", is("idVisible_1")))
        .andExpect(jsonPath("$.[0].buyer.companyId", is(0)))
        .andExpect(jsonPath("$.[0].buyer.name", is("buyer_name_1")))
        .andExpect(jsonPath("$.[0].buyer.address", is("buyer_address_1")))
        .andExpect(jsonPath("$.[0].buyer.city ", is("buyer_city_1")))
        .andExpect(jsonPath("$.[0].buyer.zipCode ", is("buyer_zipCode_1")))
        .andExpect(jsonPath("$.[0].buyer.nip ", is("buyer_nip_1")))
        .andExpect(jsonPath("$.[0].buyer.bankAccoutNumber ",
            is("buyer_bankAccoutNumber_1")))
        .andExpect(jsonPath("$.[0].seller.companyId", is(0)))
        .andExpect(jsonPath("$.[0].seller.name", is("seller_name_1")))
        .andExpect(jsonPath("$.[0].seller.address", is("seller_address_1")))
        .andExpect(jsonPath("$.[0].seller.city ", is("seller_city_1")))
        .andExpect(jsonPath("$.[0].seller.zipCode ", is("seller_zipCode_1")))
        .andExpect(jsonPath("$.[0].seller.nip ", is("seller_nip_1")))
        .andExpect(jsonPath("$.[0].seller.bankAccoutNumber ",
            is("seller_bankAccoutNumber_1")))
        .andExpect(jsonPath("$.[0].issueDate ", is("2019-03-01")))
        .andExpect(jsonPath("$.[0].paymentDate ", is("2019-03-16")))
        .andExpect(jsonPath("$.[0].products.[0].product.name", is("name_1_1")))
        .andExpect(jsonPath("$.[0].products.[0].product.description",
            is("name_1_1_description_1")))
        .andExpect(jsonPath("$.[0].products.[0].product.netValue", is(1.0)))
        .andExpect(jsonPath("$.[0].products.[0].product.vatRate", is("VAT_23")))
        .andExpect(jsonPath("$.[0].products.[0].amount", is(1)))
        .andExpect(jsonPath("$.[0].paymentState", is("NOT_PAID")))
        .andExpect(jsonPath("$.[1].invoiceId ", is(2)))
        .andExpect(jsonPath("$.[1].name", is("1 / 2025-12-24")))
        .andExpect(jsonPath("$.[0].buyer.companyId", is(0)))
        .andExpect(jsonPath("$.[1].buyer.name", is("P.H. Marian Paździoch")))
        .andExpect(jsonPath("$.[1].buyer.address", is("Bazarowa 3/6")))
        .andExpect(jsonPath("$.[1].buyer.city ", is("Wrocław")))
        .andExpect(jsonPath("$.[1].buyer.zipCode ", is("00-999")))
        .andExpect(jsonPath("$.[1].buyer.nip ", is("123-456-32-18")))
        .andExpect(jsonPath("$.[1].buyer.bankAccoutNumber ",
            is("99 1010 2222 3333 4444 5555 6666")))
        .andExpect(jsonPath("$.[1].seller.companyId", is(1)))
        .andExpect(jsonPath("$.[1].seller.name",
            is("Ferdynand Kiepski i Syn Sp.zoo")))
        .andExpect(jsonPath("$.[1].seller.address", is("ćwiartki 3/4")))
        .andExpect(jsonPath("$.[1].seller.city ", is("Wrocław")))
        .andExpect(jsonPath("$.[1].seller.zipCode ", is("00-909")))
        .andExpect(jsonPath("$.[1].seller.nip ", is("123-456-32-22")))
        .andExpect(jsonPath("$.[1].seller.bankAccoutNumber ",
            is("11 1010 2222 3333 4444 5555 6655")))
        .andExpect(jsonPath("$.[1].issueDate ", is("2025-12-24")))
        .andExpect(jsonPath("$.[1].paymentDate ", is("2025-12-31")))
        .andExpect(jsonPath("$.[1].products.[0].product.name",
            is("Mocny Full")))
        .andExpect(jsonPath("$.[1].products.[0].product.description",
            is("Piwo Jasne")))
        .andExpect(jsonPath("$.[1].products.[0].product.netValue", is(1.99)))
        .andExpect(jsonPath("$.[1].products.[0].product.vatRate", is("VAT_23")))
        .andExpect(jsonPath("$.[1].products.[0].amount", is(100)))
        .andExpect(jsonPath("$.[1].paymentState", is("NOT_PAID")));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyProductsField() throws Exception {
    //then
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(generator.getTestInvoice(2, 0)))
            .contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(content().string("[\"Products list is empty.\"]"));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyProductFields() throws Exception {
    //given
    Invoice givenInvoice = generator.getTestInvoice(2, 1);
    Product product = new Product();
    InvoiceEntry invoiceEntry = new InvoiceEntry();
    invoiceEntry.setProduct(product);
    givenInvoice.setProducts(Arrays.asList(invoiceEntry));
    //then
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(givenInvoice))
            .contentType(CONTENT_TYPE_JSON))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(content().string("[\"Product amount is negative or zero.\""
            + ",\"Product name is empty.\",\"Product description is empty.\","
            + "\"Product vat rate is empty\",\"Product net value is empty.\"]"));
  }

  @Test
  public void shouldGetAllInvoicesIssuedAfterStartDate() throws Exception {
    //given
    for (int i = 1; i <= 50; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.of(2020, 1, 1).plusMonths(i));
      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE_JSON))

          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "?startDate=2021-01-01"))
        .andExpect(content().contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andExpect(jsonPath(("$.[0].issueDate"), is("2021-01-01")))
        .andReturn()
        .getResponse()
        .getContentAsString();
    List<Invoice> invoiceList = getInvoicesFromResponse(response);
    //then
    for (Invoice invoice : invoiceList) {
      assertTrue(invoice.getIssueDate().isAfter(
          LocalDate.of(2020, 12, 31)));
    }
  }

  @Test
  public void shouldGetAllInvoicesIssuedBeforeEndDate() throws Exception {
    //given
    for (int i = 1; i <= 50; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.of(2020, 1, 1).plusMonths(i));
      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE_JSON))

          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "?endDate=2023-01-02"))
        .andExpect(content().contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andExpect(jsonPath(("$.[0].issueDate"), is("2020-02-01")))
        .andReturn()
        .getResponse()
        .getContentAsString();
    List<Invoice> invoiceList = getInvoicesFromResponse(response);
    //then
    for (Invoice invoice : invoiceList) {
      assertTrue(invoice.getIssueDate().isBefore(LocalDate.of(2023, 1, 2)));
    }
  }

  @Test
  public void shouldGetAllInvoicesIssuedBetweenSpecifiedDates() throws Exception {
    //given
    for (int i = 1; i <= 50; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      invoice.setIssueDate(LocalDate.of(2020, 1, 1).plusMonths(i));
      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE_JSON))

          .andExpect(status().isOk());
    }
    //when
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "?startDate=2021-01-01&endDate=2023-01-02"))
        .andExpect(content().contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andExpect(jsonPath(("$.[0].issueDate"), is("2021-01-01")))
        .andReturn()
        .getResponse()
        .getContentAsString();
    List<Invoice> invoiceList = getInvoicesFromResponse(response);
    //then
    for (Invoice invoice : invoiceList) {
      assertTrue((invoice.getIssueDate().isBefore(
          LocalDate.of(2023, 1, 2))
          && (invoice.getIssueDate().isAfter(
          LocalDate.of(2020, 12, 31)))));
    }
  }

  @Test
  public void shouldGetInvoiceSpecifiedById() throws Exception {
    //given
    Invoice testInvoice = generator.getTestInvoice(1, 3);
    Invoice testInvoice2 = generator.getTestInvoice(2, 5);
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(testInvoice))
            .contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(testInvoice2))
            .contentType(CONTENT_TYPE_JSON))
        .andExpect(status().isOk());
    //when
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "/2"))
        .andExpect(handler().methodName(GET_INVOICE_BY_ID_METHOD))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    Invoice returnedInvoice = jsonToInvoice(response);
    assertTrue(returnedInvoice.equals(testInvoice2));
  }

  @Test
  public void shouldReturnNotFoundError() throws Exception {
    //when
    this.mockMvc
        .perform(get(DEFAULT_PATH + "/4"))
        .andExpect(handler().methodName(GET_INVOICE_BY_ID_METHOD))
        .andExpect(status().isNotFound());
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/4"))
        .andExpect(handler().methodName(REMOVE_INVOICE_METHOD))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldRemoveSelectedInvoices() throws Exception {
    //given
    for (int i = 1; i <= 50; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE_JSON))
          .andExpect(status().isOk());
    }
    //when
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/10"))
        .andExpect(handler().methodName(REMOVE_INVOICE_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/25"))
        .andExpect(handler().methodName(REMOVE_INVOICE_METHOD))
        .andExpect(status().isOk());
    //then
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH))
        .andExpect(content().contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    List<Invoice> invoiceList = getInvoicesFromResponse(response);
    assertThat(invoiceList.size(), is(48));
    assertFalse(invoiceList.contains(generator.getTestInvoice(10, 5)));
    assertFalse(invoiceList.contains(generator.getTestInvoice(25, 5)));
  }

  @Test
  public void shouldUpdateInvoice() throws Exception {
    //given
    for (int i = 1; i <= 5; i++) {
      Invoice invoice = generator.getTestInvoice(i, 5);
      this.mockMvc
          .perform(post(DEFAULT_PATH)
              .content(json(invoice))
              .contentType(CONTENT_TYPE_JSON))
          .andExpect(status().isOk());
    }
    Invoice invoiceToUpdate = InvoicesWithSpecifiedData.getInvoiceWithPolishData();
    invoiceToUpdate.setId(3);
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/3")
            .content(json(InvoicesWithSpecifiedData.getInvoiceWithPolishData()))
            .contentType(CONTENT_TYPE_JSON))
        .andExpect(status().isOk());
    //then
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "/3"))
        .andExpect(content().contentType(CONTENT_TYPE_JSON))
        .andExpect(handler().methodName(GET_INVOICE_BY_ID_METHOD))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    invoiceToUpdate.setName("3 / 2025-12-24");
    Invoice returnedInvoice = jsonToInvoice(response);
    assertTrue(returnedInvoice.equals(invoiceToUpdate));
  }

  @Test
  public void shouldReturnPdfForExistingInvoice() throws Exception {
    Invoice invoice = generator.getTestInvoice(1, 5);
    this.mockMvc
        .perform(post(DEFAULT_PATH)
            .content(json(invoice))
            .contentType(CONTENT_TYPE_JSON))
        .andExpect(status().isOk());

    this.mockMvc
        .perform(get(DEFAULT_PATH + "/1/pdf"))
        .andExpect(content().contentType(CONTENT_TYPE_PDF))
        .andExpect(handler().methodName(GET_PDF_METHOD))
        .andExpect(status().isOk());
  }

  private String json(Invoice invoice) throws Exception {
    return mapper.writeValueAsString(invoice);
  }

  private Invoice jsonToInvoice(String jsonInvoice) throws Exception {
    return mapper.readValue(jsonInvoice, Invoice.class);
  }

  private List<Invoice> getInvoicesFromResponse(String response) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    return mapper.readValue(
        response,
        mapper.getTypeFactory().constructCollectionType(List.class, Invoice.class));
  }
}