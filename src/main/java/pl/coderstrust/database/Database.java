package pl.coderstrust.database;

import pl.coderstrust.model.WithNameIdIssueDate;

import java.util.List;

public interface Database<T extends WithNameIdIssueDate> {

  long addEntry(T entry);

  void deleteEntry(long id);

  T getEntryById(long id);

  void updateEntry(T entry);

  List<T> getEntries();

  boolean idExist(long id);

}
