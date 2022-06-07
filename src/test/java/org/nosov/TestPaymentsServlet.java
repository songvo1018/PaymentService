package org.nosov;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.testng.annotations.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.nosov.MongoConnection.dropCollection;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Properties;

public class TestPaymentsServlet extends Mockito {
    private String testUserId;
    private String testId;
    private String testSum;

    private Properties properties;

    @BeforeTest
    public void beforeTest() {
        properties = ConfigProperties.getInstance().getProperties();
        testId = properties.getProperty("testId", "72");
        testUserId = properties.getProperty("testUserId", "1331");
        testSum = properties.getProperty("testSum", "100");

        dropCollection(
                properties.getProperty("mongo_db", "payments_test"),
                properties.getProperty("mongo_collection", "payments_test")
        );
    }

    @BeforeMethod
    public void beforeMethod() {
        dropCollection(
                properties.getProperty("mongo_db", "payments_test"),
                properties.getProperty("mongo_collection", "payments_test")
        );
    }

    @Test
    public void paymentWillStored() throws Exception {
        System.out.println("TEST: paymentWillStored()");
        Gson gson = new Gson();
        Payment toStorage = new Payment(testUserId, Long.parseLong(testId), new BigDecimal(testSum));

        assertEquals(sendRequest(gson.toJson(toStorage)), "{ \"status\": 0}");
    }

    @Test
    public void manyPaymentWillStored() throws Exception {
        System.out.println("TEST: manyPaymentWillStored()");
        int correctlyStoredPayments = 0;
        int REQUIRED_CONFIRMATION_RECEIVED = 15;
        for (int i = 1; i <= REQUIRED_CONFIRMATION_RECEIVED; i++) {
            Gson gson = new Gson();
            Payment toStorage = new Payment(testUserId+i, Long.parseLong(testId + 10), new BigDecimal(testSum + 100));

            if (sendRequest(gson.toJson(toStorage)).equals("{ \"status\": 0}") ) {
                correctlyStoredPayments++;
            }
        }
        assertEquals(correctlyStoredPayments, REQUIRED_CONFIRMATION_RECEIVED);
    }

    @Test
    public void duplicatePaymentNotBeStored() throws Exception {
        System.out.println("TEST: duplicatePaymentNotBeStored()");
        Gson gson = new Gson();
        Payment toStorage = new Payment(testUserId, Long.parseLong(testId), new BigDecimal(testSum));

        assertNotEquals(sendRequest(gson.toJson(toStorage)), sendRequest(gson.toJson(toStorage)));
    }

    @Test
    public void requestWithoutSumThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutSumThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.PAYMENT_SUM_FIELD);
        } catch (Exception e) {
            ConfigProperties.getInstance().getLOGGER().warn(e.getMessage());
            assertNotEquals(e.getMessage(), "");
        }
    }

    @Test
    public void requestWithoutUserIdThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutUserIdThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.USER_ID_FIELD);
        } catch (Exception e) {
            ConfigProperties.getInstance().getLOGGER().warn(e.getMessage());
            assertNotEquals(e.getMessage(), "");
        }
    }

    @Test
    public void requestWithoutIdThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutIdThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.PAYMENT_ID_FIELD);
        } catch (Exception e) {
            ConfigProperties.getInstance().getLOGGER().warn(e.getMessage());
            assertNotEquals(e.getMessage(), "");
        }
    }

    private void sendRequestWithoutParameter(String parameter) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Gson gson = new Gson();
        Payment toStorage = new Payment(testUserId, Long.parseLong(testId), new BigDecimal(testSum));

        switch (parameter) {
            case PaymentService.USER_ID_FIELD:
                toStorage.sum = null;
                break;
            case PaymentService.PAYMENT_SUM_FIELD:
                toStorage.userId = null;
                break;
            case PaymentService.PAYMENT_ID_FIELD:
                toStorage.id = null;
                break;
        }
        when(request.getParameter("payload")).thenReturn(gson.toJson(toStorage));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        PaymentsServlet servlet = new PaymentsServlet();
        servlet.doPost(request, response);
    }

    public String sendRequest(String json) throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("payload")).thenReturn(json);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        PaymentsServlet firstRequest = new PaymentsServlet();
        firstRequest.doPost(request, response);
        return stringWriter.getBuffer().toString().trim();
    }
}