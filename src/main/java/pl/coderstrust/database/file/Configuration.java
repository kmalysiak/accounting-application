package pl.coderstrust.database.file;

public class Configuration {


  private static final String dbFilePath = "src/main/resources/";
  private static final String dbFileExtension = ".json";
  private static final String dbNamePrefix = "db";
  private static final String dbTempNamePrefix = "temp";

  private static final int fileSystemWaitTimeMs = 2000;
  private static final int UNIT_SLEEP_TIME_MS = 20;

  private String dbEntryClassName;

  public Configuration(String dbEntryClassName) {
    this.dbEntryClassName = dbEntryClassName;
  }

  public String getDbFilePath() {
    return dbFilePath
        + dbNamePrefix + dbEntryClassName + dbFileExtension;
  }

  public String getDbTempFilePath() {
    return dbFilePath
        + dbTempNamePrefix + dbEntryClassName + dbFileExtension;
  }

  public int getFileSystemWaitTimeMs() {
    return fileSystemWaitTimeMs;
  }

  public int getUnitSleepTimeMs() {
    return UNIT_SLEEP_TIME_MS;
  }
}