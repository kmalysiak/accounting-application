package pl.coderstrust.database.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DbException;
import pl.coderstrust.database.ExceptionMsg;
import pl.coderstrust.database.ObjectMapperHelper;
import pl.coderstrust.model.WithNameIdIssueDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


public class InFileDatabase<T extends WithNameIdIssueDate> implements Database<T> {

  private static final int FIRST_ID = 0;
  private static final int INCREMENT_ID = 1;
  private final Logger logger = LoggerFactory.getLogger(InFileDatabase.class);

  private FileHelper fileHelper;
  private ObjectMapperHelper mapper;
  private HashSet<Long> savedIds;
  private String keyName;

  public InFileDatabase(Class<T> entryClass, String dbKey) {
    this.keyName = dbKey;
    mapper = new ObjectMapperHelper(entryClass);
    fileHelper = new FileHelper(new Configuration(entryClass.getSimpleName()));
    savedIds = getIdsFromDbFile();
  }

  @Override
  public synchronized long addEntry(T entry) {
    entry.setId(getNextId());
    fileHelper.addLine(mapper.toJson(entry));
    savedIds.add(entry.getId());
    return entry.getId();
  }

  private long getNextId() {
    if (savedIds.isEmpty()) {
      return FIRST_ID;
    } else {
      return Collections.max(savedIds) + INCREMENT_ID;
    }
  }

  @Override
  public synchronized void deleteEntry(long systemId) {
    if (!idExist(systemId)) {
      logger.warn(" from deleteEntry (InFileDatabase): "
          + ExceptionMsg.INVOICE_NOT_EXIST);
      throw new DbException(ExceptionMsg.INVOICE_NOT_EXIST);
    } else {
      fileHelper.deleteLine(idToLineKey(systemId));
      savedIds.remove(systemId);
    }
  }

  @Override
  public synchronized T getEntryById(long systemId) {
    if (!idExist(systemId)) {
      logger.warn(" from getEntryById (InFileDatabase): "
          + ExceptionMsg.INVOICE_NOT_EXIST);
      throw new DbException(ExceptionMsg.INVOICE_NOT_EXIST);
    } else {
      String jsonEntry = fileHelper.getLine(idToLineKey(systemId));
      return (T) mapper.toObject(jsonEntry);
    }
  }

  private String idToLineKey(long systemId) {
    return keyName + ":" + String.valueOf(systemId);
  }

  @Override
  public synchronized void updateEntry(WithNameIdIssueDate entry) {
    deleteEntry(entry.getId());
    fileHelper.addLine(mapper.toJson(entry));
    savedIds.add(entry.getId());
  }

  @Override
  public List<T> getEntries() {
    return fileHelper.getAllLines().stream()
        .map(line -> (T) mapper.toObject(line))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  @Override
  public boolean idExist(long id) {
    return savedIds.contains(id);
  }

  private HashSet<Long> getIdsFromDbFile() {
    return getEntries().stream()
        .map(invoice -> invoice.getId())
        .collect(Collectors.toCollection(HashSet::new));
  }
}