package pl.coderstrust.database.mongo;

import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseTest;
import pl.coderstrust.model.Invoice;

public class MongoDbTest extends DatabaseTest {

  @Override
  public Database getCleanDatabase() {
    return new MongoDatabase(Invoice.class, null, true);
  }

  @Override
  public void shouldDeleteSingleInvoiceById() throws Exception {
  }
}
