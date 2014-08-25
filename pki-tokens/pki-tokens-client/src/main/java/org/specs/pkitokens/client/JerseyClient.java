package org.specs.pkitokens.client;

import org.glassfish.jersey.SslConfigurator;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class JerseyClient {

    private Client client;

    public JerseyClient(String trustStoreFile, String trustStorePass) {
        this(trustStoreFile, trustStorePass, null, null);
    }

    public JerseyClient(String trustStoreFile, String trustStorePass, String keyStoreFile, String keyStorePass) {
        SslConfigurator sslConfig = SslConfigurator.newInstance();
        if (trustStoreFile != null) {
            sslConfig = sslConfig
                    .trustStoreFile(trustStoreFile)
                    .trustStorePassword(trustStorePass);
        }
        if (keyStoreFile != null) {
            sslConfig = sslConfig
                    .keyStoreFile(keyStoreFile)
                    .keyPassword(keyStorePass);
        }

        SSLContext sslContext = sslConfig.createSSLContext();
        client = ClientBuilder.newBuilder().sslContext(sslContext).build();
    }

    public Client getClient() {
        return client;
    }
}
