package org.nosov;

import java.math.BigDecimal;

public class Payment {

    public String id;

    public Long userId;

    public BigDecimal sum;

    public Payment(String id, Long userId, BigDecimal sum) {
        this.id = id;
        this.userId = userId;
        this.sum = sum;
    }

    public boolean isDataCorrect() {
        return (dataNotNull() || dataNoNZero());
    }

    public boolean dataNoNZero() {
        return (!this.id.equals("0") || this.userId != 0 || !this.sum.equals(new BigDecimal(0)));
    }

    public boolean dataNotNull() {
        return (this.id != null || this.userId != null || this.sum != null);
    }
}
