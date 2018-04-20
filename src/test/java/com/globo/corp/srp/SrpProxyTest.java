package com.globo.corp.srp;

import com.globo.corp.srp.exampleServers.ExampleServers;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class SrpProxyTest {


    private Server proxyServer;
    private List<Server> backendServers;
    private SslContextFactory sslContextFactory;

    @Before
    public void setUp() throws Exception {
        proxyServer = new SrpProxy().load();
        backendServers = new ExampleServers().load();

        String keyStorePath = SrpProxy.class.getClassLoader().getResource("olimpo-server-ks.jks").getPath();

        sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStorePath);
        sslContextFactory.setKeyStorePassword("123456");
        sslContextFactory.setKeyManagerPassword("123456");
    }

    @After
    public void tearDown() throws Exception {
        proxyServer.stop();
        for (Server server : backendServers) {
            server.stop();
        }
    }

    @Test
    public void loadRegisteredServers() throws Exception {

        HttpClient client = new HttpClient(sslContextFactory);
        client.start();

        ContentResponse response1 = client.GET("https://test1.olimpo:8489");
        ContentResponse response2 = client.GET("https://test2.olimpo:8489");

        assertEquals(200, response1.getStatus());
        assertEquals(200, response2.getStatus());
    }

    @Test(expected = ExecutionException.class)
    public void loadUknowServer() throws Exception {
        HttpClient client = new HttpClient(sslContextFactory);
        client.start();
        client.GET("https://test5.olimpo:8489");
    }
}