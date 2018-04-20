package com.globo.corp.srp.exampleServers;

public class ExampleServersApp {

    public static void main(String[] args) throws Exception {
        new ExampleServers().load().get(0).join();
    }

}
