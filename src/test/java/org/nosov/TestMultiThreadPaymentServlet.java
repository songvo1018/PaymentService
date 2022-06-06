package org.nosov;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.testng.annotations.Test;
import org.testng.annotations.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.nosov.MongoConnection.dropCollection;

@Ignore
@Test
public class TestMultiThreadPaymentServlet {
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

    @AfterTest
    public void afterAll () {
        dropCollection(
                properties.getProperty("mongo_db", "payments_test"),
                properties.getProperty("mongo_collection", "payments_test")
        );
    }

    @Test(threadPoolSize = 2, invocationCount = 10,  timeOut = 10000)
    public void onlyOnePaymentShouldBeStore() throws Exception {
            assertEquals(sendRequest(), "{ \"status\": 0}");
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
