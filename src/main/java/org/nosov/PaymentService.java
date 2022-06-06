package org.nosov;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PaymentService {
    public final static String USER_ID_FIELD = "userId";
    public final static String PAYMENT_SUM_FIELD = "sum";
    public final static String PAYMENT_ID_FIELD = "id";

    public static void handleGetAllPayments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Properties properties = ConfigProperties.getInstance().getProperties();
        MongoDatabase database = MongoConnection.getInstance().getDatabase(properties.getProperty("mongo_db", "payments_test"));

        MongoCollection<Document> collection = database.getCollection(
                properties.getProperty("mongo_collection", "payments_test")).withWriteConcern(WriteConcern.MAJORITY);
        List<String> stored = new ArrayList<>();

        for (Document doc : collection.find()) {
            stored.add(doc.toJson());
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(stored);
    }


    public static String handleCreatePayment (HttpServletRequest request) {
        String userId = request.getParameter(USER_ID_FIELD);
        String paymentId = request.getParameter(PAYMENT_ID_FIELD);
        String paymentSum = request.getParameter(PAYMENT_SUM_FIELD);

        String result = "1";
        Logger logger = ConfigProperties.getInstance().getLOGGER();

        if (userId == null || paymentId == null || paymentSum == null) {
            throw new IllegalArgumentException("One of required fields is empty.");
        }

        Properties properties = ConfigProperties.getInstance().getProperties();
        MongoDatabase database = MongoConnection.getInstance().getDatabase(properties.getProperty("mongo_db", "payments_test"));

        MongoCollection<Document> collection = database.getCollection(
                properties.getProperty("mongo_collection", "payments_test")).withWriteConcern(WriteConcern.MAJORITY);

        Document toInsert = new Document()
                .append(USER_ID_FIELD, userId)
                .append(PAYMENT_SUM_FIELD, paymentSum)
                .append(PAYMENT_ID_FIELD, paymentId);

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
