package pl.coderstrust.service.taxservice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
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
import pl.coderstrust.model.Messages;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentType;
import pl.coderstrust.testhelpers.InvoicesWithSpecifiedData;
import pl.coderstrust.testhelpers.TestCasesGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WithMockUser
public class PaymentControllerIntegrationTest {

  private static final String GET_PAYMENTS = "getPayments";
  private static final String GET_PAYMENT = "getPaymentById";
  private static final String REMOVE_PAYMENT_METHOD = "removePayment";
  private static final String UPDATE_PAYMENT_METHOD = "updatePayment";
  private static final String ADD_PAYMENT_METHOD = "addPayment";
  private static final String DEFAULT_PATH = "/payment";
  private static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON_UTF8;
  private static final Payment pensionInsurance = new Payment(1, LocalDate.now().plusMonths(2),
      BigDecimal.valueOf(500),
      PaymentType.PENSION_INSURANCE);
  private static final Payment incomeTaxAdvance = new Payment(2, LocalDate.now().plusMonths(4),
      BigDecimal.valueOf(1000),
      PaymentType.INCOME_TAX_ADVANCE);
  private static final Payment healthInsurance = new Payment(3, LocalDate.now().plusMonths(6),
      BigDecimal.valueOf(300),
      PaymentType.HEALTH_INSURANCE);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private TestCasesGenerator generator;

  @Before
  public void givenForTest() throws Exception {
    this.mockMvc
        .perform(post("/v2/company")
            .content(mapper.writeValueAsString(InvoicesWithSpecifiedData.getPolishCompanySeller()))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName("addCompany"))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(pensionInsurance))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(incomeTaxAdvance))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(healthInsurance))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldAddPayments() throws Exception {
    //given
    clearDefaultGiven();
    pensionInsurance.setId(0);
    incomeTaxAdvance.setId(0);
    healthInsurance.setId(0);
    //when
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(pensionInsurance))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(incomeTaxAdvance))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(status().isOk());
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(healthInsurance))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(status().isOk());
    //then
    pensionInsurance.setId(1);
    incomeTaxAdvance.setId(2);
    healthInsurance.setId(3);

    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "/1"))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(3)));
    assertThat(output,
        is(equalTo(Arrays.asList(pensionInsurance, incomeTaxAdvance, healthInsurance))));
  }

  @Test
  public void shouldReturnErrorCausedByEmptyField() throws Exception {
    Payment wrongPayment = new Payment(4, LocalDate.now(), BigDecimal.valueOf(100),
        PaymentType.PENSION_INSURANCE);
    wrongPayment.setAmount(null);
    //then
    this.mockMvc
        .perform(post(DEFAULT_PATH + "/1")
            .content(json(wrongPayment))
            .contentType(CONTENT_TYPE))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(handler().methodName(ADD_PAYMENT_METHOD))
        .andExpect(content().string("[\"Amount is empty.\"]"));
  }

  @Test
  public void shouldGetAllPayments() throws Exception {
    //when
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "/1"))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(3)));
    assertThat(output,
        is(equalTo(Arrays.asList(pensionInsurance, incomeTaxAdvance, healthInsurance))));
  }

  @Test
  public void shouldGetPaymentById() throws Exception {
    //when
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "/1/3"))
        .andExpect(handler().methodName(GET_PAYMENT))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    Payment payment = jsonToPayment(response);

    assertThat(payment,
        is(equalTo(healthInsurance)));
  }

  @Test
  public void shouldGetPaymentsFromStartDate() throws Exception {
    //when
    String response = this.mockMvc
        .perform(
            get(DEFAULT_PATH + "/1"
                + "?startDate=" + LocalDate.now().plusMonths(3).toString()))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(2)));
    assertThat(output,
        is(equalTo(Arrays.asList(incomeTaxAdvance, healthInsurance))));
  }

  @Test
  public void shouldGetPaymentsTillEndDate() throws Exception {
    //when
    String response = this.mockMvc
        .perform(
            get(DEFAULT_PATH + "/1"
                + "?endDate=" + LocalDate.now().plusMonths(5).toString()))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(2)));
    assertThat(output,
        is(equalTo(Arrays.asList(pensionInsurance, incomeTaxAdvance))));
  }

  @Test
  public void shouldGetPaymentsBeetwenDates() throws Exception {
    //when
    String response = this.mockMvc
        .perform(
            get(DEFAULT_PATH + "/1"
                + "?startDate=" + LocalDate.now().plusMonths(3).toString()
                + "&endDate=" + LocalDate.now().plusMonths(5).toString()))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(1)));
    assertThat(output.get(0),
        is(equalTo(incomeTaxAdvance)));
  }

  @Test
  public void shouldGetPaymentsWithSpecifiedType() throws Exception {
    //when
    String response = this.mockMvc
        .perform(
            get(DEFAULT_PATH + "/1"
                + "?type=" + PaymentType.INCOME_TAX_ADVANCE))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(1)));
    assertThat(output.get(0),
        is(equalTo(incomeTaxAdvance)));
  }

  @Test
  public void shouldReturnErrorCausedByWrongCompanyId() throws Exception {
    //then
    this.mockMvc
        .perform(get(DEFAULT_PATH + "/2"))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(Messages.COMPANY_NOT_EXIST));
  }

  @Test
  public void shouldUpdatePayment() throws Exception {
    //Given
    Payment paymentToUpdate = incomeTaxAdvance;
    paymentToUpdate.setAmount(BigDecimal.valueOf(3000));
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/1/2")
            .content(json(paymentToUpdate))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(UPDATE_PAYMENT_METHOD))
        .andExpect(status().isOk());

    //then
    String response = this.mockMvc
        .perform(get(DEFAULT_PATH + "/1/2"))
        .andExpect(handler().methodName(GET_PAYMENT))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    Payment paymentAfterUpdate = jsonToPayment(response);

    assertThat(paymentAfterUpdate,
        is(equalTo(paymentToUpdate)));
  }

  @Test
  public void shouldReturnErrorCausedByCompanyNotExist() throws Exception {
    //Given
    Payment paymentToUpdate = incomeTaxAdvance;
    paymentToUpdate.setAmount(BigDecimal.valueOf(3000));
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/-100/2")
            .content(json(paymentToUpdate))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(UPDATE_PAYMENT_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByPaymentNotExist() throws Exception {
    //Given
    Payment paymentToUpdate = incomeTaxAdvance;
    paymentToUpdate.setAmount(BigDecimal.valueOf(3000));
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/1/-100")
            .content(json(paymentToUpdate))
            .contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(UPDATE_PAYMENT_METHOD))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByWrongPaymentId() throws Exception {
    //then
    this.mockMvc
        .perform(get(DEFAULT_PATH + "/1/9"))
        .andExpect(handler().methodName(GET_PAYMENT))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Payment under id 9 doesn't exist "
            + "in company Ferdynand Kiepski i Syn Sp.zoo payments list."));
  }

  @Test
  public void shouldDeletePayment() throws Exception {
    //when
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/1/1"))
        .andExpect(handler().methodName(REMOVE_PAYMENT_METHOD))
        .andExpect(status().isOk());
    //then
    String response = this.mockMvc
        .perform(
            get(DEFAULT_PATH + "/1"))
        .andExpect(content().contentType(CONTENT_TYPE))
        .andExpect(handler().methodName(GET_PAYMENTS))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    //then
    List<Payment> output = getPaymentsFromResponse(response);
    assertThat(output.size(),
        is(equalTo(2)));
    assertFalse(output.contains(pensionInsurance));
  }

  @Test
  public void shouldReturnErrorCausedByCompanyNotExistDeleteMethod() throws Exception {
    //when
    this.mockMvc
        .perform(put(DEFAULT_PATH + "/-100/2"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnErrorCausedByPaymentNotExistDeleteMethod() throws Exception {
    //when
    this.mockMvc
        .perform(delete(DEFAULT_PATH + "/1/-100"))
        .andExpect(status().isBadRequest());
  }


  private String json(Payment payment) throws Exception {
    return mapper.writeValueAsString(payment);
  }

  private Payment jsonToPayment(String jsonInvoice) throws Exception {
    return mapper.readValue(jsonInvoice, Payment.class);
  }

  private List<Payment> getPaymentsFromResponse(String response) throws Exception {
    return mapper.readValue(
        response,
        mapper.getTypeFactory().constructCollectionType(List.class, Payment.class));
  }

  private void clearDefaultGiven() throws Exception {
    for (int i = 1; i <= 3; i++) {
      this.mockMvc
          .perform(delete(DEFAULT_PATH + "/1/" + String.valueOf(i)))
          .andExpect(status().isOk());
    }
  }
}







