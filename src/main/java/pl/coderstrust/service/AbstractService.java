package pl.coderstrust.service;

import pl.coderstrust.database.Database;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.WithNameIdIssueDate;
import pl.coderstrust.service.pdfservice.PdfGenerator;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractService<T extends WithNameIdIssueDate> {

  protected static final LocalDate MIN_DATE = LocalDate.of(1500, 11, 12);
  protected static final LocalDate MAX_DATE = LocalDate.of(3000, 11, 12);

  Database<T> entriesDb;
  PdfGenerator pdfGenerator;

  public long addEntry(T entry) {
    setDefaultEntryNameIfEmpty(entry);
    return entriesDb.addEntry(entry);
  }

  public void deleteEntry(long id) {
    entriesDb.deleteEntry(id);
  }

  public T findEntry(long id) {
    return (T) entriesDb.getEntryById(id);
  }

  public List<T> getEntry() {
    return entriesDb.getEntries();
  }

  public boolean idExist(long id) {
    return entriesDb.idExist(id);
  }

  public void updateEntry(T entry) {
    setDefaultEntryNameIfEmpty(entry);
    entriesDb.updateEntry(entry);
  }

  public List<T> getEntryByDate(LocalDate beginDate, LocalDate endDate) {
    if (beginDate == null) {
      beginDate = MIN_DATE;
    }
    if (endDate == null) {
      endDate = MAX_DATE;
    }
    List<T> selectedEntries = new ArrayList<>();
    List<T> allEntries = entriesDb.getEntries();
    for (T entry : allEntries) {
      if (entry.getIssueDate().isBefore(endDate.plusDays(1)) && entry.getIssueDate()
          .isAfter(beginDate.minusDays(1))) {
        selectedEntries.add(entry);
      }
    }
    return selectedEntries;
  }

  public List<T> getEntries() {
    return entriesDb.getEntries();
  }

  protected void setDefaultEntryNameIfEmpty(T entry) {
    if (entry.getName() == null || entry.getName().trim().length() == 0) {
      entry.setName(String.format("%d / %s", entry.getId(), entry.getIssueDate()));
    }
  }

  protected ByteArrayInputStream getPdfReport(long id) {
    return pdfGenerator.invoiceToPdf((Invoice) findEntry(id));
  }
}