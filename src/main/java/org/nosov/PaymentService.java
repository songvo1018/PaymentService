package org.nosov;

import com.google.gson.*;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.util.Properties;

public class PaymentService {
    public final static String USER_ID_FIELD = "userId";
    public final static String PAYMENT_SUM_FIELD = "sum";
    public final static String PAYMENT_ID_FIELD = "id";

    public static String handleCreatePayment (HttpServletRequest request) {
        String payload = request.getParameter("payload");
        if (payload == null) {
            throw new IllegalArgumentException("Payload not represented.");
        }

        Payment toStorage;
        Gson gson = new Gson();
        Logger logger = ConfigProperties.getInstance().getLOGGER();
        String result = "1";

        JsonElement json = JsonParser.parseString(payload).getAsJsonObject();

        try {
            toStorage = gson.fromJson(json, Payment.class);
        } catch (JsonSyntaxException e) {
            ConfigProperties.getInstance().getLOGGER().warn(e.getMessage());
            throw new IllegalArgumentException("One of required fields is empty.");
        }

        if (!toStorage.isDataCorrect()) {
            throw new IllegalArgumentException("Incorrect data.");
        }

        Properties properties = ConfigProperties.getInstance().getProperties();
        MongoDatabase database = MongoConnection.getInstance().getDatabase(properties.getProperty("mongo_db", "payments_test"));

        MongoCollection<Document> collection = database.getCollection(
                properties.getProperty("mongo_collection", "payments_test")).withWriteConcern(WriteConcern.MAJORITY);

        Document toInsert = new Document()
                .append(USER_ID_FIELD, toStorage.userId)
                .append(PAYMENT_SUM_FIELD, toStorage.sum)
                .append(PAYMENT_ID_FIELD, toStorage.id);

        IndexOptions indexOptions = new IndexOptions().unique(true);

        try {
            collection.createIndex(Indexes.ascending(PAYMENT_ID_FIELD), indexOptions);
            try {
                collection.insertOne(toInsert);
                result = "0";
            } catch (MongoException e) {
                logger.error("PaymentService: Write error", e);
            }
        } catch (DuplicateKeyException e) {
            logger.error("PaymentService: Duplicate key", e);
        }
        return result;
    }


}
