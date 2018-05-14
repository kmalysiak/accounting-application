package pl.coderstrust.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pl.coderstrust.model.Messages;
import pl.coderstrust.model.WithNameIdIssueDate;
import pl.coderstrust.model.WithValidation;
import pl.coderstrust.service.filters.EntriesFilter;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractController<T extends WithNameIdIssueDate & WithValidation> {

  protected AbstractService<T> service;
  protected EntriesFilter<T> filter;


  public ResponseEntity addEntry(T entry, Long filterId) {
    List<String> entryState = entry.validate();

    if (filterId != null) {
      if (!filter.hasObjectById(entry, filterId.longValue())) {
        entryState.add(Messages.COMPANY_ID_NOT_MATCH);
      }
    }
    if (entryState.isEmpty()) {
      long id = service.addEntry(entry);
      return ResponseEntity.ok(Messages.CONTROLLER_ENTRY_ADDED + id);
    }
    return ResponseEntity.badRequest().body(entryState);
  }

  public ResponseEntity getEntryById(Long entryId, Long filterKey) {
    if (!service.idExist(entryId)) {
      return ResponseEntity.notFound().build();
    }

    if (filterKey != null) {
      if (!filter.hasField(service.findEntry(entryId), filterKey)) {
        return ResponseEntity.notFound().build();
      }
    }
    return ResponseEntity.ok(service.findEntry(entryId));
  }


  public ResponseEntity getEntryByDate(LocalDate startDate, LocalDate endDate, Long filterKey) {
    if (startDate == null && endDate == null) {
      if (filterKey != null) {
        return ResponseEntity
            .ok(filter.filterByField(service.getEntry(), filterKey));
      }
      return ResponseEntity.ok(service.getEntry());
    }

    if (filterKey != null) {
      return ResponseEntity.ok(filter.filterByField(service.getEntryByDate(startDate,
          endDate), filterKey));
    }

    return ResponseEntity.ok(service.getEntryByDate(startDate,
        endDate));
  }


  public ResponseEntity updateEntry(Long entryId, T entry, Long filterKey) {
    List<String> entryState = entry.validate();

    if (filterKey != null) {
      if (!filter.hasObjectById(entry, filterKey)) {
        entryState.add(Messages.COMPANY_ID_NOT_MATCH);
      }
    }

    if (!entryState.isEmpty()) {
      return ResponseEntity.badRequest().body(entryState);
    }
    entry.setId(entryId);
    service.updateEntry(entry);
    return ResponseEntity.ok().build();
  }

  public ResponseEntity removeEntry(Long entryId, Long filterKey) {

    List<String> entryState = new ArrayList<>();
    if (!service.idExist(entryId)) {
      return ResponseEntity.notFound().build();
    }

    if (filterKey != null) {
      if (!filter.hasField(service.findEntry(entryId), filterKey)) {
        entryState.add(Messages.COMPANY_ID_NOT_MATCH);
        return ResponseEntity.badRequest().body(entryState);
      }
    }
    service.deleteEntry(entryId);
    return ResponseEntity.ok().build();
  }

  public ResponseEntity getPdfFromEntry(Long entryId, Long filterKey) {

    if (!service.idExist(entryId)) {
      return ResponseEntity.notFound().build();
    }

    if (filterKey != null) {
      if (!filter.hasField(service.findEntry(entryId), filterKey)) {
        return ResponseEntity.notFound().build();
      }
    }

    ByteArrayInputStream pdfContent = service.getPdfReport(entryId);
    HttpHeaders headers = new HttpHeaders();
    String fileName = "invoice_" + Long.toString(entryId) + ".pdf";
    headers.add("Content-Disposition", "inline; filename=" + fileName);
    return ResponseEntity
        .ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_PDF)
        .body(new InputStreamResource(pdfContent));

  }

}