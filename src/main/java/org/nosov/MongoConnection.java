package org.nosov;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoConnection {
    private static MongoConnection instance;
    private final MongoClient client;
    private String mongoPort;

    private MongoConnection () {
        mongoPort = ConfigProperties.getInstance().getProperties().getProperty("mongo_port", "27017");
        client = createClient();
    }

    public  MongoClient createClient () {
        return MongoClients.create("mongodb://localhost:" + mongoPort);
    }

    public MongoDatabase getDatabase(String database) {
        return client.getDatabase(database);
    }

    public static MongoConnection getInstance () {
        if (instance == null) {
            return new MongoConnection();
        }
        return MongoConnection.instance;
    }

    public static void dropCollection(String databaseName, String collectionName) {
        MongoDatabase database = MongoConnection.getInstance().getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.drop();
        collection.dropIndexes();
    }
}
