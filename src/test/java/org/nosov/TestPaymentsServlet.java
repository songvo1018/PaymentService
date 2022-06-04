package org.nosov;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import org.junit.Test;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.thread.ITestNGThreadPoolExecutor;

import static org.testng.annotations.Test.*;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPaymentsServlet extends Mockito {

    void setUp() {
    }


    void doGet() {
    }

    private String testUserId;
    private String testId;
    private String testSum;

    @BeforeTest
    public void beforeTest() {
        Properties properties = ConfigProperties.getInstance().getProperty();
        testId = properties.getProperty("testId", "72");
        testUserId = properties.getProperty("testUserId", "1331");
        testSum = properties.getProperty("testSum", "100");
    }

    private int counter;
    @BeforeMethod
    public void beforeMethod() {
        counter++;
    }
//    TODO:
//    @AfterTest dropbase / drop collection

//    @Test(threadPoolSize = 3)
    @Test(threadPoolSize = 3, invocationCount = 5,  timeOut = 10000)
    public void failWriteDuplicatePayment() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("userId")).thenReturn(testUserId);
        when(request.getParameter("sum")).thenReturn(testSum);
        when(request.getParameter("id")).thenReturn(testId);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        PaymentsServlet firstRequest = new PaymentsServlet();
        firstRequest.doPost(request, response);
        String result = stringWriter.getBuffer().toString().trim();

        assertEquals(result, "{ \"status\": 0}");
    }

    @Test
    public void writePayment() throws Exception {
        failWriteDuplicatePayment();
    }
}