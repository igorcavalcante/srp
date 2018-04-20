package com.globo.corp.srp;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import java.io.IOException;
import java.util.Properties;

public class SrpProxy {

    public Server load() throws Exception {
        Properties properties = new ConfigLoader().load();
        Server server = new Server();

        ServerConnector sslConnector = new SSLConnectorLoader().load(server, properties);
        server.setConnectors(new Connector[] { sslConnector });

        ContextHandlerCollection contextHandlerCollection = new ContextHandlerCollection();
        contextHandlerCollection.setHandlers(new ContextsLoader().load(properties));

        server.setHandler(contextHandlerCollection);
        server.start();

        return server;
    }

}
