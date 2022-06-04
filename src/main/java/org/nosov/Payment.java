package org.nosov;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

public class Payment {
    @BsonId
    ObjectId _id;

    String id;

    Long userId;

    BigDecimal sum;
}
