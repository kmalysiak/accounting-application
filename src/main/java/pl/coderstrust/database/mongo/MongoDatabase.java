package pl.coderstrust.database.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.DbException;
import pl.coderstrust.database.ExceptionMsg;
import pl.coderstrust.database.ObjectMapperHelper;
import pl.coderstrust.database.file.InFileDatabase;
import pl.coderstrust.model.WithNameIdIssueDate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MongoDatabase<T extends WithNameIdIssueDate> implements Database<T> {

  private final Logger logger = LoggerFactory.getLogger(InFileDatabase.class);
  private MongoClient mongoClient;
  private DB database;
  private DBCollection collection;
  private ObjectMapperHelper<T> mapperHelper;
  private long index;
  private Class entryClass;
  private String keyName;

  public MongoDatabase(Class<T> entryClass, String keyName, boolean isEmbeded) {
    this.keyName = keyName;
    this.entryClass = entryClass;
    mapperHelper = new ObjectMapperHelper(entryClass);
    if (isEmbeded) {
      MongoConfig mongoConfig = new MongoConfig();
      try {
        MongoTemplate mongoTemplate = mongoConfig.mongoTemplate();
        collection = mongoTemplate.getCollection(entryClass.getSimpleName());
      } catch (IOException ex) {
        logger.warn(" From MongoDatabase constructor " + ex);
        throw new DbException(ExceptionMsg.IO_ERROR_WHILE_INITIALIZING, ex);
      }
    } else {
      mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
      database = mongoClient.getDB("AccountantApp");
      collection = database.getCollection(entryClass.getSimpleName());
    }
    index = getCurrentMaxIndex();
  }

  @Override
  public long addEntry(T entry) {
    long id = getNextIndex();
    entry.setId(id);
    String json = mapperHelper.toJson(entry);
    DBObject object = (DBObject) JSON.parse(json);
    object.put("_id", id);
    collection.save(object);
    return id;
  }

  @Override
  public void deleteEntry(long id) {
    BasicDBObject document = new BasicDBObject();
    document.put("_id", id);
    collection.remove(document);
  }

  @Override
  public T getEntryById(long id) {
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put("_id", id);
    DBCursor cursor = collection.find(whereQuery);
    return cursor.hasNext() ? mapperHelper.toObject(cursor.one()) : null;
  }

  @Override
  public void updateEntry(T entry) {
    long id = entry.getId();
    DBObject object = (DBObject) JSON.parse(mapperHelper.toJson(entry));
    BasicDBObject searchQuery = new BasicDBObject().append("_id", id);
    collection.update(searchQuery, object);
  }

  @Override
  public List<T> getEntries() {
    DBCursor cursor = collection.find();
    List<T> entriesList = new ArrayList<>();
    while (cursor.hasNext()) {
      entriesList.add(mapperHelper.toObject(cursor.next()));
    }
    return entriesList;
  }

  @Override
  public boolean idExist(long id) {
    BasicDBObject whereQuery = new BasicDBObject();
    whereQuery.put("_id", id);
    DBCursor cursor = collection.find(whereQuery);
    return cursor.hasNext();
  }

  private long getCurrentMaxIndex() {
    DBObject sort = new BasicDBObject();
    sort.put(entryClass.getSimpleName().toLowerCase() + "Id", -1);
    return collection.count() == 0 ? 0 : new Long(
        (Integer) collection.find().sort(sort).limit(1).one()
            .get(entryClass.getSimpleName().toLowerCase() + "Id"));
  }

  private long getNextIndex() {
    index += 1;
    return index;
  }


}
