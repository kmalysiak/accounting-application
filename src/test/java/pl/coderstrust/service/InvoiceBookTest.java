package pl.coderstrust.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Invoice;

import java.time.LocalDate;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceBookTest {

  @Mock
  private Database database;

  @Mock
  private Invoice invoice;

  @InjectMocks
  private InvoiceService invoiceBook;

  @Test
  public void shouldAddInvoice() {
    //given
    when(database.addEntry(invoice)).thenReturn(1L);
    //when
    invoiceBook.addEntry(invoice);
    //then
    assertThat(invoiceBook.addEntry(invoice), is(equalTo(1L)));
  }

  @Test
  public void shouldRemoveInvoice() {
    //given
    doNothing().when(database).deleteEntry(1);
    //when
    invoiceBook.deleteEntry(1);
    //then
    verify(database).deleteEntry(1);
  }

  @Test
  public void shouldFindInvoice() {
    //given
    when(database.getEntryById(1)).thenReturn(invoice);
    //when
    invoiceBook.findEntry(1);
    //then
    verify(database).getEntryById(1);
  }

  @Test
  public void shouldUpdateInvoice() {
    //given
    doNothing().when(database).updateEntry(invoice);
    //when
    invoiceBook.updateEntry(invoice);
    //then
    verify(database).updateEntry(invoice);
  }

  @Test
  public void shouldGetInvoice() {
    //given
    when(database.getEntries()).thenReturn(Collections.singletonList(invoice));
    //when
    invoiceBook.getEntry();
    //then
    verify(database).getEntries();
  }

  @Test
  public void shouldCheckIdExist() {
    //given
    when(database.idExist(anyLong())).thenReturn(true);
    //when
    invoiceBook.idExist(1);
    //then
    verify(database).idExist(1);
  }

  @Test
  public void shouldGetInvoiceByDate() {
    //given
    LocalDate date = LocalDate.of(2018, 3, 15);
    Invoice invoiceDateTest = new Invoice();
    invoiceDateTest.setIssueDate(date);
    when(database.getEntries()).thenReturn(Collections.singletonList(invoiceDateTest));
    //when
    invoiceBook.getEntryByDate(date, date);
    //then
    assertThat(invoiceBook.getEntries().iterator().next().getIssueDate(), is(equalTo(date)));
  }
}