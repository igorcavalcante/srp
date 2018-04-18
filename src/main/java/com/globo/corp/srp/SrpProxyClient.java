package com.globo.corp.srp;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.UndertowClient;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.util.AttachmentKey;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channel;
import java.util.concurrent.TimeUnit;

public class SrpProxyClient implements ProxyClient {

    private UndertowClient client = UndertowClient.getInstance();

    private final AttachmentKey<ClientConnection> clientAttachmentKey = AttachmentKey.create(ClientConnection.class);

    private URI test1 = new URI("http://test1.olimpo:8081");
    private URI test2 = new URI("http://test2.olimpo:8082");
    private URI test3 = new URI("http://test3.olimpo:8083");

    public SrpProxyClient() throws URISyntaxException {
    }


    @Override
    public ProxyTarget findTarget(HttpServerExchange exchange) {
        return new ProxyTarget() { };
    }

    @Override
    public void getConnection(ProxyTarget target, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit) {
        URI selectedUri = null;
        if(exchange.getHostName().contains("test1") )
            selectedUri = test1;
        else if (exchange.getHostName().contains("test2"))
            selectedUri = test2;
        else if(exchange.getHostName().contains("test3"))
            selectedUri = test3;
        if(selectedUri != null) {
            client.connect(new ConnectNotifier(selectedUri, callback, exchange), selectedUri, exchange.getIoThread(), exchange.getConnection().getByteBufferPool(), OptionMap.EMPTY);
        }
    }

    private final class ConnectNotifier implements ClientCallback<ClientConnection> {
        private final ProxyCallback<ProxyConnection> callback;
        private final HttpServerExchange exchange;
        private final URI uri;

        private ConnectNotifier(URI uri, ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange) {
            this.callback = callback;
            this.exchange = exchange;
            this.uri = uri;
        }

        @Override
        public void completed(final ClientConnection connection) {
            final ServerConnection serverConnection = exchange.getConnection();
            //we attach to the connection so it can be re-used
            serverConnection.putAttachment(clientAttachmentKey, connection);
            serverConnection.addCloseListener(new ServerConnection.CloseListener() {
                @Override
                public void closed(ServerConnection serverConnection) {
                    IoUtils.safeClose(connection);
                }
            });
            connection.getCloseSetter().set(new ChannelListener<Channel>() {
                @Override
                public void handleEvent(Channel channel) {
                    serverConnection.removeAttachment(clientAttachmentKey);
                }
            });
            callback.completed(exchange, new ProxyConnection(connection, uri.getPath() == null ? "/" : uri.getPath()));
        }

        @Override
        public void failed(IOException e) {
            callback.failed(exchange);
        }
    }


}
