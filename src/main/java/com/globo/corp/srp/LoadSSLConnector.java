package com.globo.corp.srp;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.Properties;

public class LoadSSLConnector {

    public ServerConnector load(Server server, Properties properties) {
        String keyStoreName = properties.getProperty("keyStoreName");
        String keyStorePassword = properties.getProperty("keyStorePassword");
        String keyManagerPassword = properties.getProperty("keyManagerPassword");
        String keyStorePath = SrpProxy.class.getClassLoader().getResource(keyStoreName).getPath();

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStorePath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);

        ServerConnector sslConnector = new ServerConnector(
            server,
            new SslConnectionFactory(sslContextFactory, "http/1.1"),
            new HttpConnectionFactory(https));
        sslConnector.setPort(8489);

        return sslConnector;
    }

}
