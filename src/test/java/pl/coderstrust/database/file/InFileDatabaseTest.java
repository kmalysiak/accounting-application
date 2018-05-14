package pl.coderstrust.database.file;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DatabaseTest;
import pl.coderstrust.model.Invoice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InFileDatabaseTest extends DatabaseTest {

  private static final int WAIT_TIME_FOR_FILESYSTEM = 4000;
  private static final int UNIT_WAIT_TIME_FOR_FILESYSTEM = 100;
  private Configuration config = new Configuration(Invoice.class.getSimpleName());
  private FileHelper fileHelper = new FileHelper(config);
  private File dataFile = new File(config.getDbFilePath());

  @Override
  public Database getCleanDatabase() {
    File dbFile = new File(config.getDbFilePath());
    if (dbFile.exists()) {
      try {
        Files.delete(dbFile.toPath());
        int maxChecksCount = WAIT_TIME_FOR_FILESYSTEM / UNIT_WAIT_TIME_FOR_FILESYSTEM;
        int checkNumber = 0;
        while (dbFile.exists() && checkNumber < maxChecksCount) {
          Thread.sleep(UNIT_WAIT_TIME_FOR_FILESYSTEM);
          checkNumber++;
        }
        Files.createFile(dbFile.toPath());
      } catch (IOException ex) {
        ex.printStackTrace();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    Database database = new InFileDatabase<Invoice>(Invoice.class, "\"invoiceId\"");
    return database;
  }

  @Test
  public void shouldCleanTemporaryFileAfterDeleteOperation() {
    //when
    givenDatabase.deleteEntry(INVOICES_COUNT - 1);
    File tempFile = new File(config.getDbTempFilePath());
    try {
      Thread.sleep(WAIT_TIME_FOR_FILESYSTEM);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    //then
    assertThat(tempFile.exists(), is(false));
  }

  @Test
  public void shouldStoreDatabaseInCorrectLocation() {
    //when
    givenDatabase.addEntry(givenInvoice);
    File dataFile = new File(config.getDbFilePath());
    try {
      Thread.sleep(WAIT_TIME_FOR_FILESYSTEM);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    //then
    assertThat(dataFile.exists(), is(true));
  }

  @Test
  public void shouldAddCorrectNumberOfLinesToDbFile() {
    //given
    getCleanDatabase();
    //when
    fileHelper.addLine("test line1");
    fileHelper.addLine("test line2");

    //then
    ArrayList<String> fileContent = getFileContent(dataFile);
    assertThat(String.join("", fileContent), is(equalTo("test line1test line2")));
  }

  @Test
  public void shouldRemoveCorrectNumberOfLinesLineFromDbFile() {
    //given
    getCleanDatabase();
    //when
    fileHelper.addLine("test line1");
    fileHelper.addLine("test line2");
    fileHelper.deleteLine("test line2");

    //then
    ArrayList<String> fileContent = getFileContent(dataFile);
    assertThat(String.join("", fileContent), is(equalTo("test line1")));
  }

  @Test
  public void shouldGetOneLineFromDbFile() {
    getCleanDatabase();
    //when
    fileHelper.addLine("test line1");
    fileHelper.addLine("test line2");
    String output = fileHelper.getLine("test line2");

    //then
    assertThat(output, is(equalTo("test line2")));
  }

  @Test
  public void shouldReturnAllLinesAtDbFile() {
    //given
    getCleanDatabase();

    //when
    fileHelper.addLine("test line1");
    fileHelper.addLine("test line2");
    ArrayList<String> output = new ArrayList(fileHelper.getAllLines());

    //then
    ArrayList<String> fileContent = getFileContent(dataFile);
    assertThat(output.toArray(), is(equalTo(fileContent.toArray())));
  }

  @Test
  public void shouldNotDestroyDbFileContentAtNewFileHelperCreation() {
    //given
    getCleanDatabase();

    //when
    fileHelper.addLine("test line1");
    FileHelper newFileHelper = new FileHelper(config);
    String output = newFileHelper.getLine("test line1");

    //then
    assertThat(output, is(equalTo("test line1")));

  }

  ArrayList<String> getFileContent(File file) {
    try (Stream<String> dbStream = Files.lines(file.toPath())) {
      return dbStream
          .collect(Collectors.toCollection(ArrayList::new));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return new ArrayList<>();
  }

  @Test
  public void shouldAddInvoiceCorrectlyAfterDbReinitialization() {
    //given
    long lastId = invoiceIds[INVOICES_COUNT - 1];

    //when
    Database dbInstance = new InFileDatabase<Invoice>(Invoice.class, "\"invoiceId\"");

    //then
    long nextId = dbInstance.addEntry(generator.getTestInvoice(1, 1));
    assertThat(nextId > lastId, is(true));
  }

}