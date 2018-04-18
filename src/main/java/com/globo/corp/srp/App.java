package com.globo.corp.srp;

import io.undertow.Undertow;
import io.undertow.server.handlers.ProxyPeerAddressHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.proxy.SimpleProxyClientProvider;
import io.undertow.util.Headers;

import java.net.URI;
import java.net.URISyntaxException;

public class App {

    public static void main(String... args) throws URISyntaxException {

        Undertow testServer1 = Undertow.builder()
            .addHttpListener(8081, "test1.olimpo")
            .setHandler(exchange -> {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Hello world from test1.olimpo");
            })
            .build();
        testServer1.start();

        Undertow testServer2 = Undertow.builder()
                .addHttpListener(8082, "test2.olimpo")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.getResponseSender().send("Hello world from test2.olimpo");
                })
                .build();
        testServer2.start();

        Undertow testServer3 = Undertow.builder()
                .addHttpListener(8083, "test3.olimpo")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.getResponseSender().send("Hello world from test3.olimpo");
                })
                .build();
        testServer3.start();

        Undertow server1 = Undertow.builder()
            .addHttpListener(8080, "0.0.0.0")
            .setHandler(ProxyHandler.builder().setProxyClient(new SrpProxyClient()).build())
            .build();

        server1.start();
    }

}
