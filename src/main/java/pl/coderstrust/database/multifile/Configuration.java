package pl.coderstrust.database.multifile;

import java.io.File;

public class Configuration {

  private static final String jsonFilePath =
      "src" + File.separator + "main" + File.separator + "resources" + File.separator
          + "multiFileDB";
  private static final String jsonTempFileName = "temporary.json";
  private String dbEntryClassName;


  public Configuration(String dbEntryClassName) {
    this.dbEntryClassName = dbEntryClassName;
  }

  public String getJsonFilePath() {
    return (jsonFilePath + dbEntryClassName);
  }

  public String getJsonTempFilePath() {
    return (jsonFilePath + dbEntryClassName + File.separator + jsonTempFileName);
  }
}