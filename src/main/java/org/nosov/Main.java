package org.nosov;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties properties =ConfigProperties.getInstance().getProperties();
        String PORT = properties.getProperty("server_port", String.valueOf(8082));

        Server server = new Server(Integer.parseInt(PORT));
        Connector connector = new ServerConnector(server);
        server.addConnector(connector);

        var handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(PaymentsServlet.class, "/*");

        ConfigProperties.getInstance().getLOGGER().info("Server started on:" + PORT);
        server.start();
        server.join();
    }
}