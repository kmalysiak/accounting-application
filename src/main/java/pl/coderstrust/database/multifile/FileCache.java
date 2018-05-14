package pl.coderstrust.database.multifile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.coderstrust.database.DbException;
import pl.coderstrust.database.ExceptionMsg;
import pl.coderstrust.database.ObjectMapperHelper;
import pl.coderstrust.model.WithNameIdIssueDate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileCache<T extends WithNameIdIssueDate> {

  private final Logger logger = LoggerFactory.getLogger(FileCache.class);
  private ObjectMapperHelper objectMapper;
  private HashMap<Long, String> cache;
  private String jsonFilePath;

  public FileCache(ObjectMapperHelper objectMapper, String jsonFilePath) {
    this.objectMapper = objectMapper;
    this.jsonFilePath = jsonFilePath;
    cache = getActualFileCache();
  }

  private HashMap getActualFileCache() {
    ArrayList<String> allFiles = getAllFilesEntries();
    HashMap<Long, String> tempCache = new HashMap<>();
    for (String json : allFiles) {
      T entry = jsonToEntry(json);
      tempCache.put(entry.getId(), new PathSelector(jsonFilePath).getFilePath(entry));
    }
    return tempCache;
  }

  public ArrayList<String> getAllFilesEntries() {
    ArrayList<String> readFiles = new ArrayList<>();
    if (!new File(jsonFilePath).exists()) {
      return readFiles;
    }
    listFiles((jsonFilePath)).stream().map(File::toString).forEach(str -> {
      try (BufferedReader bufferedReader = new BufferedReader(new FileReader(str))) {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          readFiles.add(line);
        }
      } catch (IOException ex) {
        logger.warn(" from getAllFilesEntries in FileCache: "
            + ExceptionMsg.IO_ERROR_WHILE_READING, ex);
        throw new DbException(ExceptionMsg.IO_ERROR_WHILE_READING, ex);
      }
    });
    return readFiles;
  }

  private List<File> listFiles(String directoryName) {
    File dir = new File(directoryName);
    String[] extensions = new String[]{"json"};
    return (List<File>) FileUtils.listFiles(dir, extensions, true);
  }

  private T jsonToEntry(String json) {
    return (T) objectMapper.toObject(json);
  }

  public HashMap getCache() {
    return cache;
  }
}