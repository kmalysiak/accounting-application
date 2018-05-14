package pl.coderstrust.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.coderstrust.database.file.InFileDatabase;
import pl.coderstrust.database.memory.InMemoryDatabase;
import pl.coderstrust.database.mongo.MongoDatabase;
import pl.coderstrust.database.multifile.MultiFileDatabase;
import pl.coderstrust.database.sql.CompaniesSqlDb;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;


@Configuration
public class DatabaseProvider {

  private static final String IN_FILE = "inFile";
  private static final String MULTIFILE = "multifile";
  private static final String MONGO = "mongo";
  private static final String MONGO_EMB = "mongo_emb";
  private static final String SQL_DB = "sql_db";

  @Value("${pl.coderstrust.database.MasterDatabase}")
  private String masterDbType;

  @Value("${pl.coderstrust.database.FilterDatabase}")
  private String filterDbType;

  @Value("${pl.coderstrust.database.MasterDatabase.key}")
  private String masterDbKey;

  @Value("${pl.coderstrust.database.FilterDatabase.key}")
  private String filterDbKey;


  @Bean
  public Database<Invoice> invoicesDatabase() {
    switch (masterDbType) {
      case IN_FILE:
        return new InFileDatabase<>(Invoice.class, masterDbKey);
      case MULTIFILE:
        return new MultiFileDatabase<>(Invoice.class, masterDbKey);
      case MONGO:
        return new MongoDatabase<>(Invoice.class, masterDbKey, false);
      case MONGO_EMB:
        return new MongoDatabase<>(Invoice.class, masterDbKey, true);
      //TODO SQL db for Invoices
      // case SQL_DB:
      // return new CompaniesSqlDb<>(Invoice.class);
      default:
        return new InMemoryDatabase<>(Invoice.class);
    }
  }

  @Bean
  public Database<Company> companiesDatabase() {
    switch (masterDbType) {
      case IN_FILE:
        return new InFileDatabase<>(Company.class, filterDbKey);
      case MULTIFILE:
        return new MultiFileDatabase<>(Company.class, filterDbKey);
      case MONGO:
        return new MongoDatabase<>(Company.class, filterDbKey, false);
      case MONGO_EMB:
        return new MongoDatabase<>(Company.class, filterDbKey, true);
      case SQL_DB:
        return new CompaniesSqlDb();
      default:
        return new InMemoryDatabase<>(Company.class);
    }
  }
}
