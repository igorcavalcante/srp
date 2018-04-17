package com.globo.corp.srp;

import io.undertow.Undertow;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.proxy.SimpleProxyClientProvider;
import io.undertow.util.Headers;

import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static void main(String... args) throws URISyntaxException {

        Undertow server = Undertow.builder()
            .addHttpListener(8081, "0.0.0.0")
            .setHandler(exchange -> {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Hello world");
            })
            .build();
        server.start();

        SimpleProxyClientProvider client1 = new SimpleProxyClientProvider(new URI("http://localhost:8081"));
        Undertow server1 = Undertow.builder()
            .addHttpListener(8082, "0.0.0.0")
            .setHandler(ProxyHandler.builder().setProxyClient(client1).build())
            .build();

        server1.start();
    }

}
