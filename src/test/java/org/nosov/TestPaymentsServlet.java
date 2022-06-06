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
        System.out.println(" ");
    }

    @Test
    public void duplicatePaymentNotBeStored() throws Exception {
        System.out.println("TEST: duplicatePaymentNotBeStored()");
        assertNotEquals(sendRequest(), sendRequest());
        System.out.println(" ");
    }

    @Test
    public void requestWithoutSumThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutSumThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.PAYMENT_SUM_FIELD);
        } catch (Exception e) {
            assertNotEquals(e.getMessage(), "");
        }
        System.out.println(" ");
    }

    @Test
    public void requestWithoutUserIdThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutUserIdThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.USER_ID_FIELD);
        } catch (Exception e) {
            assertNotEquals(e.getMessage(), "");
        }
        System.out.println(" ");
    }

    @Test
    public void requestWithoutIdThrowsException() throws Exception {
        System.out.println("TEST: requestWithoutIdThrowsException()");
        try {
            sendRequestWithoutParameter(PaymentService.PAYMENT_ID_FIELD);
        } catch (Exception e) {
            assertNotEquals(e.getMessage(), "");
        }
        System.out.println(" ");
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

        PaymentsServlet firstRequest = new PaymentsServlet();
        firstRequest.doPost(request, response);
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
}