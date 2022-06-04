package org.nosov;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.util.Collection;

public class PaymentService {

    public Collection<Payment> getPayments(MongoDatabase connection, String collectionName) {
        return (Collection<Payment>) connection.getCollection(collectionName, Payment.class);
    }
}
