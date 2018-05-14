package pl.coderstrust.database.memory;

import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseTest;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabaseTest extends DatabaseTest {

  @Override
  public Database getCleanDatabase() {
    return new InMemoryDatabase(Invoice.class);
  }
}