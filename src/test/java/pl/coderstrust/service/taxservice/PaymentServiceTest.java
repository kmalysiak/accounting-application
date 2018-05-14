package pl.coderstrust.service.taxservice;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentType;
import pl.coderstrust.service.CompanyService;
import pl.coderstrust.testhelpers.InvoicesWithSpecifiedData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

  private Payment healthInsurance;
  private Payment pensionInsurance;
  private Payment incomeTaxAdvance;

  @Mock
  private CompanyService companyService;

  @InjectMocks
  private PaymentService paymentService;

  @Before
  public void givenForAllTests() {
    when(companyService.findEntry(1))
        .thenReturn(InvoicesWithSpecifiedData.getPolishCompanySeller());
    healthInsurance = new Payment(-1, LocalDate.now().plusMonths(1),
        BigDecimal.valueOf(300), PaymentType.HEALTH_INSURANCE);
    pensionInsurance = new Payment(-1, LocalDate.now().plusMonths(2),
        BigDecimal.valueOf(500), PaymentType.PENSION_INSURANCE);
    incomeTaxAdvance = new Payment(-1, LocalDate.now().plusMonths(3),
        BigDecimal.valueOf(1000), PaymentType.INCOME_TAX_ADVANCE);
    paymentService.addPayment(1, healthInsurance);
    paymentService.addPayment(1, pensionInsurance);
    paymentService.addPayment(1, incomeTaxAdvance);
  }

  @Test
  public void shouldAddPayments() {
    //given
    paymentService.deletePayment(1, 1);
    paymentService.deletePayment(1, 2);
    paymentService.deletePayment(1, 3);
    //when
    paymentService.addPayment(1, healthInsurance);
    paymentService.addPayment(1, pensionInsurance);
    paymentService.addPayment(1, incomeTaxAdvance);
    //then
    assertThat(paymentService.getPayments(1).size(), is(3));
    assertTrue(paymentService.getPayments(1).contains(pensionInsurance));
    assertTrue(paymentService.getPayments(1).contains(healthInsurance));
    assertTrue(paymentService.getPayments(1).contains(incomeTaxAdvance));
    assertTrue(paymentService.idExist(1, 1));
    assertTrue(paymentService.idExist(1, 2));
    assertTrue(paymentService.idExist(1, 3));
  }

  @Test
  public void shouldReturnPaymentsList() {
    //when
    assertThat(paymentService.getPayments(1).size(), is(3));
    assertThat(paymentService.getPayments(1).get(2), is(incomeTaxAdvance));
  }

  @Test
  public void shouldReturnPaymentsBetweenDates() {
    //when
    List<Payment> output = paymentService.getPaymentsByDate(1,
        LocalDate.now(), LocalDate.now().plusDays(70));
    //then
    assertThat(output, is(Arrays.asList(healthInsurance, pensionInsurance)));
  }

  @Test
  public void shouldReturnPaymentsBetweenDatesAndSpecifiedByType() {
    //when
    List<Payment> output = paymentService.getPaymentsByTypeAndDate(1,
        LocalDate.now(), LocalDate.now().plusMonths(4), PaymentType.INCOME_TAX_ADVANCE);
    //then
    assertThat(output, is(Arrays.asList(incomeTaxAdvance)));
  }

  @Test
  public void shouldUpdateExistingPayment() {
    Payment updatedPayment = incomeTaxAdvance;
    updatedPayment.setAmount(BigDecimal.TEN);
    //when
    paymentService.updatePayment(1, updatedPayment);
    //then
    assertThat(paymentService.getPayments(1).get(2).getAmount(), is(BigDecimal.TEN));
  }

  @Test
  public void shouldDeletePayment() {
    //when
    paymentService.deletePayment(1, 1);
    //then
    assertFalse(paymentService.getPayments(1).contains(healthInsurance));
    assertFalse(paymentService.idExist(1, 1));
    assertTrue(paymentService.getPayments(1).contains(incomeTaxAdvance));
    assertTrue(paymentService.getPayments(1).contains(pensionInsurance));
  }

  @Test
  public void shouldCheckIfIdExist() {
    //then
    assertTrue(paymentService.idExist(1, 1));
    assertTrue(paymentService.idExist(1, 2));
    assertTrue(paymentService.idExist(1, 3));
    assertFalse(paymentService.idExist(1, 4));
  }


}