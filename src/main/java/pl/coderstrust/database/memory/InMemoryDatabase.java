package pl.coderstrust.database.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DbException;
import pl.coderstrust.database.ExceptionMsg;
import pl.coderstrust.model.WithNameIdIssueDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryDatabase<T extends WithNameIdIssueDate> implements Database<T> {

  private static final int INITIAL_ID = 0;
  private static final int INCREMENT_ID = 1;
  private final Logger logger = LoggerFactory.getLogger(InMemoryDatabase.class);

  private HashMap<Long, T> entries = new HashMap<>();
  private long lastId = INITIAL_ID;


  public InMemoryDatabase(Class<T> entryClass) {
  }

  @Override
  public long addEntry(T entry) {
    entry.setId(getNextId());
    entries.put(entry.getId(), entry);
    return entry.getId();
  }

  private long getNextId() {
    lastId += INCREMENT_ID;
    return lastId;
  }

  @Override
  public void deleteEntry(long id) {
    if (!idExist(id)) {
      logger.warn(" from deleteEntry (InMemoryDatabase): "
          + ExceptionMsg.INVOICE_NOT_EXIST);
      throw new DbException(ExceptionMsg.INVOICE_NOT_EXIST);
    }
    entries.remove(id);
  }

  @Override
  public T getEntryById(long id) {
    if (!idExist(id)) {
      logger.warn(" from getEntryByiD (InMemoryDatabase): "
          + ExceptionMsg.INVOICE_NOT_EXIST);
      throw new DbException(ExceptionMsg.INVOICE_NOT_EXIST);
    }
    return entries.get(id);
  }

  @Override
  public void updateEntry(T entry) {
    if (!idExist(entry.getId())) {
      logger.warn(" from updateEntry (InMemoryDatabase): "
          + ExceptionMsg.INVOICE_NOT_EXIST);
      throw new DbException(ExceptionMsg.INVOICE_NOT_EXIST);
    }
    entries.replace(entry.getId(), entry);
  }

  @Override
  public List<T> getEntries() {
    return new ArrayList<>(entries.values());
  }

  @Override
  public boolean idExist(long id) {
    return entries.containsKey(id);
  }
}