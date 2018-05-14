package pl.coderstrust.database.multifile;

import pl.coderstrust.model.WithNameIdIssueDate;

import java.io.File;

public class PathSelector {

  private String jsonFilePath;

  public PathSelector(String jsonFilePath) {
    this.jsonFilePath = jsonFilePath;
  }

  public String getFilePath(WithNameIdIssueDate entry) {
    StringBuilder stringBuilder = new StringBuilder();
    String invoiceDateYear = String.valueOf(entry.getIssueDate().getYear());
    String invoiceDateMonth = String.valueOf(entry.getIssueDate().getMonth());
    String invoiceDateDay = String.valueOf(entry.getIssueDate().getDayOfMonth());
    stringBuilder.append(jsonFilePath);
    stringBuilder.append(File.separator);
    stringBuilder.append(invoiceDateYear);
    stringBuilder.append(File.separator);
    stringBuilder.append(invoiceDateMonth);
    stringBuilder.append(File.separator);
    stringBuilder.append(invoiceDateDay);
    stringBuilder.append(".json");
    return stringBuilder.toString();
  }
}
