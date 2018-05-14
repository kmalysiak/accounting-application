package pl.coderstrust.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.testhelpers.TestCasesGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser
public class InvoiceControllerMultiCompaniesIntegrationTests {

  private static final String GET_INVOICE_BY_DATE_METHOD = "getInvoiceByDatePerCompany";
  private static final String GET_INVOICE_BY_ID_METHOD = "getInvoiceByIdPerCompany";
  private static final String REMOVE_INVOICE_METHOD = "removeInvoicePerCompany";
  private static final String ADD_INVOICE_METHOD = "addInvoicePerCompany";
  private static final String GET_PDF_METHOD = "invoiceToPdf";

  private static final String ADD_COMPANY_METHOD = "addCompany";

  private static final String DEFAULT_PATH_INVOICE = "/v2/company/";
  private static final String DEFAULT_PATH_COMPANY = "/v2/company";
  private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final MediaType PDF_CONTENT_TYPE = MediaType.APPLICATION_PDF;
  private static final String INT_FROM_STRING_REGEX_PATTERN = "([0-9])+";
  private static final int DEFAULT_INVOICE_NUMBER = 1;
  private static final int DEFAULT_UPDATED_INVOICE_NUMBER = 2;
  private static final int DEFAULT_ENTRIES_COUNT = 1;

  private static String START_DATE = "2020-01-01";
  private static String END_DATE = "2060-01-01";
  private LocalDate startDate = LocalDate.parse(START_DATE);
  private LocalDate endDate = LocalDate.parse(END_DATE);

  private Pattern extractIntFromString = Pattern.compile(INT_FROM_STRING_REGEX_PATTERN);
  private Invoice invoice;
  private Invoice updatedInvoice;
  private long buyerId;
  private long sellerId;
  private long invoiceId;
  private long anotherCompanyId;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private TestCasesGenerator generator;

  @Before
  public void givenForTest() throws Exception {
    invoice = generator.getTestInvoice(DEFAULT_INVOICE_NUMBER, DEFAULT_ENTRIES_COUNT);
    updatedInvoice = generator
        .getTestInvoice(DEFAULT_UPDATED_INVOICE_NUMBER, DEFAULT_ENTRIES_COUNT);

    buyerId = registerInvoiceBuyerAtCompanyDb(invoice);
    sellerId = registerInvoiceSellerAtCompanyDb(invoice);
    invoiceId = addInvoiceToInvoiceDb(invoice);

    anotherCompanyId = addCompanyToCompanyDb(invoice.getSeller());
    updatedInvoice.setId(invoiceId);
    updatedInvoice.setBuyer(invoice.getBuyer());
    updatedInvoice.setSeller(invoice.getSeller());
  }

  @Test
  public void shouldAddInvoiceWhenBuyerMatchesCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(post(addInvoiceUrl(buyerId))
            .content(mapper.writeValueAsString(invoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isOk());
  }

  private String addInvoiceUrl(long companyId) {
    return DEFAULT_PATH_INVOICE + String.valueOf(companyId) + "/invoice";
  }

  private long registerInvoiceBuyerAtCompanyDb(Invoice invoice) throws Exception {
    long buyerId = addCompanyToCompanyDb(invoice.getBuyer());
    this.invoice.getBuyer().setId(buyerId);
    return buyerId;
  }

  private long registerInvoiceSellerAtCompanyDb(Invoice invoice) throws Exception {
    long sellerId = addCompanyToCompanyDb(invoice.getSeller());
    this.invoice.getSeller().setId(sellerId);
    return sellerId;
  }


  @Test
  public void shouldAddInvoiceWhenSellerMatchesCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(post(addInvoiceUrl(sellerId))
            .content(mapper.writeValueAsString(invoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldReturnErrorWhileAddWhenCompaniesNotMatchCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(post(addInvoiceUrl(anotherCompanyId))
            .content(mapper.writeValueAsString(invoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldGetEntryByIdWhenSellerMatchesCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(get(getInvoiceUrl(invoice.getId(), sellerId)))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_INVOICE_BY_ID_METHOD))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(invoice)));
  }

  private String getInvoiceUrl(long invoiceId, long companyId) {
    return DEFAULT_PATH_INVOICE + String.valueOf(companyId) + "/invoice/" + String
        .valueOf(invoiceId);
  }

  private String getPdfUrl(long invoiceId, long companyId) {
    return DEFAULT_PATH_INVOICE + String.valueOf(companyId) + "/invoice/" + String
        .valueOf(invoiceId) + "/pdf";
  }

  @Test
  public void shouldGetEntryByIdWhenBuyerMatchesCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(get(getInvoiceUrl(invoice.getId(), buyerId)))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_INVOICE_BY_ID_METHOD))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(invoice)));
  }

  @Test
  public void shouldReturnErrorWhileGetEntryByIdWhenBuyerOrSellerIdNotMatchIdAtInvoice()
      throws Exception {
    //then
    this.mockMvc
        .perform(get(getInvoiceUrl(invoiceId, anotherCompanyId)))
        .andExpect(handler().methodName(GET_INVOICE_BY_ID_METHOD))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldGetCorrectEntriesByDateWhenBuyerMatchesCompanyId() throws Exception {
    //given
    LocalDate invoiceDate = startDate;
    while (invoiceDate.isBefore(endDate)) {
      invoice.setIssueDate(invoiceDate);
      invoiceDate = invoiceDate.plusYears(1);
      addInvoiceToInvoiceDb(invoice);
    }

    //when
    String response = this.mockMvc
        .perform(get(getInvoiceByDateUrl(buyerId)))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    List<Invoice> invoiceList = getInvoicesFromResponse(response);

    //then
    for (Invoice inv : invoiceList) {
      assertTrue((inv.getIssueDate().isBefore(endDate.plusDays(1))
          && (inv.getIssueDate().isAfter(startDate.minusDays(1)))));
    }
  }

  private String getInvoiceByDateUrl(long companyId) {
    return DEFAULT_PATH_INVOICE + String.valueOf(companyId) + "/invoice"
        + "?startDate=" + START_DATE + "&endDate=" + END_DATE;
  }

  @Test
  public void shouldGetEntryByDateWhenSellerMatchesCompanyId() throws Exception {
    //given
    LocalDate invoiceDate = startDate;
    while (invoiceDate.isBefore(endDate)) {
      invoice.setIssueDate(invoiceDate);
      invoiceDate = invoiceDate.plusYears(1);
      addInvoiceToInvoiceDb(invoice);
    }

    //when
    String response = this.mockMvc
        .perform(get(getInvoiceByDateUrl(sellerId)))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    List<Invoice> invoiceList = getInvoicesFromResponse(response);

    //then
    for (Invoice inv : invoiceList) {
      assertTrue((inv.getIssueDate().isBefore(endDate.plusDays(1))
          && (inv.getIssueDate().isAfter(startDate.minusDays(1)))));
    }
  }


  @Test
  public void shouldGetErrorWhileEntryByDateWhenBuyerAndSellerNotMatchesCompanyId()
      throws Exception {
    //given
    LocalDate invoiceDate = startDate;

    while (invoiceDate.isBefore(endDate)) {
      invoice.setIssueDate(invoiceDate);
      invoiceDate = invoiceDate.plusYears(1);
      addInvoiceToInvoiceDb(invoice);
    }

    //when
    String response = this.mockMvc
        .perform(get(getInvoiceByDateUrl(sellerId + 1)))
        .andExpect(content().contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_INVOICE_BY_DATE_METHOD))
        .andReturn().getResponse().getContentAsString();

    //than
    assertThat(response, is(equalTo("[]")));
  }

  @Test
  public void shouldUpdateInvoiceWhenBuyerMatchesId() throws Exception {
    //then
    this.mockMvc
        .perform(put(getInvoiceUpdateUrl(invoiceId, buyerId))
            .content(mapper.writeValueAsString(updatedInvoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldUpdateInvoiceWhenSellerMatchesId() throws Exception {
    //then
    this.mockMvc
        .perform(put(getInvoiceUpdateUrl(invoiceId, sellerId))
            .content(mapper.writeValueAsString(updatedInvoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldNotUpdateInvoiceWhenSellerBuyerNotMatchId() throws Exception {
    //then
    this.mockMvc
        .perform(put(getInvoiceUpdateUrl(invoiceId, anotherCompanyId))
            .content(mapper.writeValueAsString(updatedInvoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  private String getInvoiceUpdateUrl(long invoiceId, long companyId) {
    return DEFAULT_PATH_INVOICE + String.valueOf(companyId) + "/invoice/" + String
        .valueOf(invoiceId);
  }

  @Test
  public void shouldDeleteInvoiceWhenBuyerMatchesId() throws Exception {
    //then
    this.mockMvc
        .perform(delete(getInvoiceDeleteUrl(invoiceId, buyerId)))
        .andExpect(handler().methodName(REMOVE_INVOICE_METHOD))
        .andExpect(status().isOk());
  }

  private String getInvoiceDeleteUrl(long invoiceId, long companyId) {
    return DEFAULT_PATH_INVOICE + String.valueOf(companyId) + "/invoice/" + String
        .valueOf(invoiceId);
  }

  @Test
  public void shouldNotDeleteInvoiceWhenSellerBuyerNotMatchesId() throws Exception {
    //then
    this.mockMvc
        .perform(delete(getInvoiceDeleteUrl(invoiceId, anotherCompanyId)))
        .andExpect(handler().methodName(REMOVE_INVOICE_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldDeleteInvoiceWhenSellerMatchesId() throws Exception {
    //then
    this.mockMvc
        .perform(delete(getInvoiceDeleteUrl(invoiceId, sellerId)))
        .andExpect(handler().methodName(REMOVE_INVOICE_METHOD))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldGetEntryPdfWhenSellerMatchesCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(get(getPdfUrl(invoice.getId(), sellerId)))
        .andExpect(content().contentType(PDF_CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PDF_METHOD))
        .andExpect(status().isOk());
  }

  private long addCompanyToCompanyDb(Company company) throws Exception {
    String serviceResponse = this.mockMvc
        .perform(post(DEFAULT_PATH_COMPANY)
            .content(mapper.writeValueAsString(company))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_COMPANY_METHOD))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getEntryIdFromServiceResponse(serviceResponse);
  }

  private long addInvoiceToInvoiceDb(Invoice invoice) throws Exception {
    String serviceResponse = this.mockMvc
        .perform(post(addInvoiceUrl(invoice.getBuyer().getId()))
            .content(mapper.writeValueAsString(invoice))
            .contentType(JSON_CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_INVOICE_METHOD))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    return getEntryIdFromServiceResponse(serviceResponse);
  }

  private long getEntryIdFromServiceResponse(String response) {
    Matcher matcher = extractIntFromString.matcher(response);
    matcher.find();
    return Long.parseLong(matcher.group(0));
  }


  private List<Invoice> getInvoicesFromResponse(String response) throws Exception {
    return mapper.readValue(
        response,
        mapper.getTypeFactory().constructCollectionType(List.class, Invoice.class));
  }
}