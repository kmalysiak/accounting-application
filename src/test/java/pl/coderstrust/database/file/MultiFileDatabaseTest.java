package pl.coderstrust.database.file;

import org.apache.commons.io.FileUtils;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseTest;
import pl.coderstrust.database.ObjectMapperHelper;
import pl.coderstrust.database.multifile.Configuration;
import pl.coderstrust.database.multifile.FileCache;
import pl.coderstrust.database.multifile.MultiFileDatabase;
import pl.coderstrust.model.Invoice;

import java.io.File;
import java.io.IOException;

public class MultiFileDatabaseTest extends DatabaseTest {

  @Override
  public Database getCleanDatabase() {
    Configuration config = new Configuration(Invoice.class.getSimpleName());
    try {
      FileUtils.forceMkdir(new File(config.getJsonFilePath()));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    try {
      FileUtils.cleanDirectory(new File(config.getJsonFilePath()));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    ObjectMapperHelper objectMapperHelper = new ObjectMapperHelper(Invoice.class);
    FileCache fileCache = new FileCache(objectMapperHelper, config.getJsonFilePath());
    fileCache.getCache().clear();

    Database database = new MultiFileDatabase<Invoice>(Invoice.class, "\"invoiceId\"");
    return database;
  }
}