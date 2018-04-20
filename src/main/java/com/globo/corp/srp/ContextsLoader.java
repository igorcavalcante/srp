package com.globo.corp.srp;

import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Properties;
import java.util.stream.Stream;

public class ContextsLoader {

    public ContextHandler[] load(Properties properties) {
        Stream<String> propertiesAsStream = properties.stringPropertyNames().stream();
        Stream<String> servers = propertiesAsStream.filter(key -> key.contains("server"));
        Stream<ContextHandler> handlers = servers.map(key -> {
            String[] serverInfo = properties.getProperty(key).split(":");

            ConnectHandler proxy = new ConnectHandler();
            ServletContextHandler context = new ServletContextHandler(proxy, "/", ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.setVirtualHosts(new String[]{serverInfo[0]});

            ServletHolder proxyServlet1 = new ServletHolder(ProxyServlet.Transparent.class);
            proxyServlet1.setInitParameter("proxyTo", "http://" + serverInfo[0] + ":" + serverInfo[1]);
            proxyServlet1.setInitParameter("Prefix", "/");
            context.addServlet(proxyServlet1, "/*");

            return context;
        });

        return handlers.toArray(ContextHandler[]::new);
    }

}
