package org.nosov;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.Mockito;
import org.testng.annotations.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.nosov.MongoConnection.dropCollection;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
        assertEquals(sendRequest(), "{ \"status\": 0}");
    }

    @Test
    public void manyPaymentWillStored() throws Exception {
        System.out.println("TEST: manyPaymentWillStored()");
        int correctlyStoredPayments = 0;
        int REQUIRED_CONFIRMATION_RECEIVED = 15;
        for (int i = 1; i <= REQUIRED_CONFIRMATION_RECEIVED; i++) {
            if (sendRequest(i, (i + 10), (i + 20)).equals("{ \"status\": 0}") ) {
                correctlyStoredPayments++;
            }
        }
        assertEquals(correctlyStoredPayments, REQUIRED_CONFIRMATION_RECEIVED);
    }

    @Test
    public void duplicatePaymentNotBeStored() throws Exception {
        System.out.println("TEST: duplicatePaymentNotBeStored()");
        assertNotEquals(sendRequest(), sendRequest());
    }

    @Test
    public void requestWithoutSumThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutSumThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.PAYMENT_SUM_FIELD);
        } catch (Exception e) {
            assertNotEquals(e.getMessage(), "");
        }
    }

    @Test
    public void requestWithoutUserIdThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutUserIdThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.USER_ID_FIELD);
        } catch (Exception e) {
            assertNotEquals(e.getMessage(), "");
        }
    }

    @Test
    public void requestWithoutIdThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutIdThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.PAYMENT_ID_FIELD);
        } catch (Exception e) {
            assertNotEquals(e.getMessage(), "");
        }
    }

    private void sendRequestWithoutParameter(String parameter) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter(PaymentService.USER_ID_FIELD)).thenReturn(testUserId);
        when(request.getParameter(PaymentService.PAYMENT_SUM_FIELD)).thenReturn(testSum);
        when(request.getParameter(PaymentService.PAYMENT_ID_FIELD)).thenReturn(testId);

        switch (parameter) {
            case PaymentService.USER_ID_FIELD:
                when(request.getParameter(PaymentService.USER_ID_FIELD)).thenReturn(null);
                break;
            case PaymentService.PAYMENT_SUM_FIELD:
                when(request.getParameter(PaymentService.PAYMENT_SUM_FIELD)).thenReturn(null);
                break;
            case PaymentService.PAYMENT_ID_FIELD:
                when(request.getParameter(PaymentService.PAYMENT_ID_FIELD)).thenReturn(null);
                break;
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        PaymentsServlet servlet = new PaymentsServlet();
        servlet.doPost(request, response);
    }

    public String sendRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter(PaymentService.USER_ID_FIELD)).thenReturn(testUserId);
        when(request.getParameter(PaymentService.PAYMENT_SUM_FIELD)).thenReturn(testSum);
        when(request.getParameter(PaymentService.PAYMENT_ID_FIELD)).thenReturn(testId);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        PaymentsServlet firstRequest = new PaymentsServlet();
        firstRequest.doPost(request, response);
        return stringWriter.getBuffer().toString().trim();
    }

    public String sendRequest(int userId, int paymentSum, int id) throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter(PaymentService.USER_ID_FIELD)).thenReturn(userId + testUserId);
        when(request.getParameter(PaymentService.PAYMENT_SUM_FIELD)).thenReturn(paymentSum + testSum);
        when(request.getParameter(PaymentService.PAYMENT_ID_FIELD)).thenReturn(id + testId);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        PaymentsServlet firstRequest = new PaymentsServlet();
        firstRequest.doPost(request, response);
        return stringWriter.getBuffer().toString().trim();
    }
}