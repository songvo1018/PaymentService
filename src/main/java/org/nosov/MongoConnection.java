package org.nosov;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.mongodb.client.MongoDatabase;


public class MongoConnection {
    public static MongoClient getClient (String port) {
        return MongoClients.create("mongodb://localhost:" + port);
    }

    public static MongoDatabase getDatabase(String port, String databaseName) {
        MongoClient client = getClient(port);
        return client.getDatabase(databaseName);
    }
}
