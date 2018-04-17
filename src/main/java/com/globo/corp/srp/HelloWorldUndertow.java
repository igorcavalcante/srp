package com.globo.corp.srp;

import io.undertow.Undertow;
import io.undertow.util.Headers;

public class HelloWorldUndertow {

    public static void main(String... args) {

        Undertow server = Undertow.builder()
            .addHttpListener(8080, "0.0.0.0")
            .setHandler(exchange -> {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Hello world");
            })
            .build();

        server.start();
    }

}
