package com.globo.corp.srp;

import org.eclipse.jetty.server.Server;

public class App {

    public static void main(String[] args) throws Exception {
        Server server = new SrpProxy().load();
        server.join();
    }

}
