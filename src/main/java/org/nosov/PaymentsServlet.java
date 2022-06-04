package org.nosov;

import com.mongodb.MongoWriteException;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.util.*;

public class PaymentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static String USER_ID_FIELD = "userId";
    private static String PAYMENT_SUM_FIELD = "sum";
    private static String PAYMENT_ID_FIELD = "id";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Properties properties = ConfigProperties.getInstance().getProperty();
        MongoDatabase database = MongoConnection.getDatabase(
                properties.getProperty("mongo_port", "27017"),
                properties.getProperty("mongo_db", "payments_test"));

        MongoCollection<Document> collection = database.getCollection(
                properties.getProperty("mongo_collection", "payments_test")).withWriteConcern(WriteConcern.MAJORITY);
        List<String> stored = new ArrayList<>();

        for (Document doc : collection.find()) {
            stored.add(doc.toJson());
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(stored.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = request.getParameter(USER_ID_FIELD);
        String paymentId = request.getParameter(PAYMENT_ID_FIELD);
        String paymentSum = request.getParameter(PAYMENT_SUM_FIELD);

        if (userId == null || paymentId == null || paymentSum == null) {
            throw new IllegalArgumentException("One of required fields is empty.");
        }

        Properties properties = ConfigProperties.getInstance().getProperty();

        MongoDatabase database = MongoConnection.getDatabase(
                properties.getProperty("mongo_port", "27017"),
                properties.getProperty("mongo_db", "payments_test"));

        MongoCollection<Document> collection = database.getCollection(
                properties.getProperty("mongo_collection", "payments_text")).withWriteConcern(WriteConcern.MAJORITY);

        Document toInsert = new Document()
                .append(USER_ID_FIELD, userId)
                .append(PAYMENT_SUM_FIELD, paymentSum)
                .append(PAYMENT_ID_FIELD, paymentId);

        IndexOptions indexOptions = new IndexOptions().unique(true);
        String result = "1";
        try {
            collection.insertOne(toInsert);
            collection.createIndex(Indexes.ascending(PAYMENT_ID_FIELD), indexOptions);
            result = "0";
        } catch (MongoWriteException e) {
            System.out.println(e.toString());
        } finally {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{ \"status\": " + result +"}");
        }
    }
}
