package com.globo.corp.srp;

import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.Properties;

public class SrpProxy {

    public static void main(String[] args) throws Exception {

        Properties properties = new ConfigLoader().load();
        Server server = new Server();

        ServerConnector sslConnector = new LoadSSLConnector().load(server, properties);
        server.setConnectors(new Connector[] { sslConnector });

        ContextHandler context1 = new ContextHandler();
        context1.setContextPath("/");
        context1.setVirtualHosts(new String[]{"test1.olimpo"});
        context1.setHandler(new HelloWorld1Handler("Hello 1"));

        ConnectHandler proxy = new ConnectHandler();
        ContextHandler context2 = new ContextHandler();
        context2.setContextPath("/");
        context2.setVirtualHosts(new String[]{"test2.olimpo"});
        context2.setHandler(proxy);

        ServletContextHandler context = new ServletContextHandler(proxy, "/", ServletContextHandler.SESSIONS);
        ServletHolder proxyServlet = new ServletHolder(ProxyServlet.Transparent.class);
        proxyServlet.setInitParameter("proxyTo", "http://test2.olimpo:8082/");
        proxyServlet.setInitParameter("Prefix", "/");
        context.addServlet(proxyServlet, "/*");

        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        contextHandlerCollection.setHandlers(new Handler[] {context1, context2});

        server.setHandler(contextHandlerCollection);
        server.start();

        Server server2 = new Server(8082);
        server2.setHandler(new HelloWorld1Handler("Greetings from server 2"));

        server2.start();

        server.join();
        server2.join();
    }

}
