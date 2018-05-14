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
import pl.coderstrust.model.Company;

import java.time.LocalDate;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class CompanyServiceTest {

  @Mock
  private Database database;

  @Mock
  private Company company;

  @InjectMocks
  private CompanyService companySerivce;

  @Test
  public void shouldAddCompany() {
    //given
    when(database.addEntry(company)).thenReturn(1L);
    //when
    companySerivce.addEntry(company);
    //then
    assertThat(companySerivce.addEntry(company), is(equalTo(1L)));
  }

  @Test
  public void shouldRemoveCompany() {
    //given
    doNothing().when(database).deleteEntry(1);
    //when
    companySerivce.deleteEntry(1);
    //then
    verify(database).deleteEntry(1);
  }

  @Test
  public void shouldFindCompany() {
    //given
    when(database.getEntryById(1)).thenReturn(company);
    //when
    companySerivce.findEntry(1);
    //then
    verify(database).getEntryById(1);
  }

  @Test
  public void shouldUpdateCompany() {
    //given
    doNothing().when(database).updateEntry(company);
    //when
    companySerivce.updateEntry(company);
    //then
    verify(database).updateEntry(company);
  }

  @Test
  public void shouldGetCompany() {
    //given
    when(database.getEntries()).thenReturn(Collections.singletonList(company));
    //when
    companySerivce.getEntry();
    //then
    verify(database).getEntries();
  }

  @Test
  public void shouldCheckIdExist() {
    //given
    when(database.idExist(anyLong())).thenReturn(true);
    //when
    companySerivce.idExist(1);
    //then
    verify(database).idExist(1);
  }

  @Test
  public void shouldGetCompanyByDate() {
    //given
    LocalDate date = LocalDate.of(2018, 3, 15);
    Company companyDateTest = new Company();
    companyDateTest.setIssueDate(date);
    when(database.getEntries()).thenReturn(Collections.singletonList(companyDateTest));
    //when
    companySerivce.getEntryByDate(date, date);
    //then
    assertThat(companySerivce.getEntries().iterator().next().getIssueDate(), is(equalTo(date)));
  }
}

