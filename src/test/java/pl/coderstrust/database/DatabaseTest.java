package pl.coderstrust.database;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.testhelpers.TestCasesGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class DatabaseTest {

  protected static final int INVOICES_COUNT = 2;
  private static final int INVOICE_ENTRIES_COUNT = 1;
  @Rule
  public ExpectedException atDeletedInvoiceAccess = ExpectedException.none();
  protected TestCasesGenerator generator = new TestCasesGenerator();
  protected Invoice givenInvoice;
  protected Database givenDatabase;
  protected long[] invoiceIds = new long[INVOICES_COUNT];
  private ObjectMapperHelper mapper = new ObjectMapperHelper<Invoice>(Invoice.class);
  private String[] expected = new String[INVOICES_COUNT];
  private String[] output = new String[INVOICES_COUNT];

  public abstract Database getCleanDatabase();

  @Before
  public void defaultGiven() {
    givenDatabase = getCleanDatabase();
    for (int i = 0; i < INVOICES_COUNT; i++) {
      givenInvoice = generator.getTestInvoice(i, INVOICE_ENTRIES_COUNT);
      invoiceIds[i] = givenDatabase.addEntry(givenInvoice);
      givenInvoice.setId(invoiceIds[i]);
      expected[i] = mapper.toJson(givenInvoice);
    }
  }

  @Test
  public void shouldAddAndGetSingleInvoice() {
    //given
    givenDatabase = getCleanDatabase();
    long invoiceId = givenDatabase.addEntry(givenInvoice);

    //when
    String output = mapper.toJson(givenDatabase.getEntryById(invoiceId));
    String expected = mapper.toJson(givenInvoice);

    //then
    assertThat(output, is(equalTo(expected)));
  }

  @Test
  public void shouldDeleteSingleInvoiceById() throws Exception {
    //given
    givenDatabase = getCleanDatabase();
    long invoiceId = givenDatabase.addEntry(givenInvoice);

    //when
    givenDatabase.deleteEntry(invoiceId);

    //then
    atDeletedInvoiceAccess.expect(DbException.class);
    atDeletedInvoiceAccess.expectMessage(ExceptionMsg.INVOICE_NOT_EXIST);
    givenDatabase.getEntryById(invoiceId);
  }

  @Test
  public void shouldUpdateSingleInvoice() {
    //given
    givenDatabase = getCleanDatabase();
    long invoiceId = givenDatabase.addEntry(givenInvoice);

    //when
    givenInvoice = generator.getTestInvoice(INVOICE_ENTRIES_COUNT + 1, INVOICE_ENTRIES_COUNT);
    givenInvoice.setId(invoiceId);
    givenDatabase.updateEntry(givenInvoice);
    String expected = mapper.toJson(givenInvoice);
    String output = mapper.toJson(givenDatabase.getEntryById(invoiceId));

    //then
    assertThat(output, is(equalTo(expected)));
  }


  @Test
  public void shouldAddAndGetSeveralInvoices() {
    //when
    for (int i = 0; i < INVOICES_COUNT; i++) {
      output[i] = mapper.toJson(givenDatabase.getEntryById(invoiceIds[i]));
    }
    //then
    assertThat(output, is(equalTo(expected)));
  }

  @Test
  public void shouldDeleteSeveralInvoicesById() throws Exception {
    //when
    for (int i = 0; i < INVOICES_COUNT; i++) {
      givenDatabase.deleteEntry(invoiceIds[i]);
    }

    //then
    boolean[] output = new boolean[INVOICES_COUNT];
    boolean[] expected = new boolean[INVOICES_COUNT];
    Arrays.fill(expected, false);
    for (int i = 0; i < INVOICES_COUNT; i++) {
      output[i] = givenDatabase.idExist((invoiceIds[i]));
    }
    assertThat(output, is(equalTo(expected)));
  }

  @Test
  public void shouldUpdateSeveralInvoices() {
    //when
    try {
      for (int i = 0; i < INVOICES_COUNT; i++) {
        givenInvoice = generator.getTestInvoice(i + 1, INVOICE_ENTRIES_COUNT);
        givenInvoice.setId(invoiceIds[i]);
        expected[i] = mapper.toJson(givenInvoice);
        givenDatabase.updateEntry(givenInvoice);
      }

      //then
      for (int i = 0; i < INVOICES_COUNT; i++) {
        output[i] = mapper.toJson(givenDatabase.getEntryById(invoiceIds[i]));
      }
    } catch (Exception ex) {
      fail("Test failed due to object mapper exception during processing invoice to Json.");
      ex.printStackTrace();
    }
    assertThat(output, is(equalTo(expected)));
  }

  @Test
  public void shouldGetAllInvoices() {
    //then
    ArrayList<Invoice> allInvoices = new ArrayList<>(givenDatabase.getEntries());
    String[] output = new String[INVOICES_COUNT];

    for (int i = 0; i < INVOICES_COUNT; i++) {
      output[i] = mapper.toJson(allInvoices.get(i));
    }

    //expected
    assertThat(output, is(equalTo(expected)));
  }

  @Test
  public void shouldReturnTrueWhenInvoiceExist() {
    long invoiceId = invoiceIds[(new Random()).nextInt(invoiceIds.length)];
    assertThat(givenDatabase.idExist(invoiceId), is(true));
  }

  @Test
  public void shouldReturnFalseWhenInvoiceDoesNotExist() {
    assertThat(givenDatabase.idExist(INVOICES_COUNT + INVOICES_COUNT), is(false));
  }

  @Test
  public void shouldReturnFalseForRemovedInvoice() {
    givenDatabase.deleteEntry(INVOICES_COUNT - 1);
    assertThat(givenDatabase.idExist(INVOICES_COUNT - 1), is(false));
  }
}