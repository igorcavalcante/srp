package com.globo.corp.srp.exampleServers;

import com.globo.corp.srp.ConfigLoader;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ExampleServers {

    public static void main(String... args) throws Exception {
        Properties properties = new ConfigLoader().load();

        List<String> serversProperties = properties
            .stringPropertyNames()
            .stream()
            .filter(key -> key.contains("server"))
            .collect(Collectors.toList());

        List<Server> servers = new ArrayList<>();
        serversProperties.forEach(key -> {
            String[] serverInfo = properties.getProperty(key).split(":");
            String host = serverInfo[0];
            String port = serverInfo[1];

            Server server = new Server();
            ServerConnector connector = new ServerConnector(server);
            connector.setHost(host);
            connector.setPort(new Integer(port));
            server.addConnector(connector);

            server.setHandler(new SimpleHttpHandler("<h3>Server running on hostconfig "+ host +" and port: "+ port +"</h3>"));

            try {
                server.start();
                servers.add(server);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao levantar servidor no host" + host + " e porta: " + port);
            }
        });


        servers.get(0).join();
    }

}
