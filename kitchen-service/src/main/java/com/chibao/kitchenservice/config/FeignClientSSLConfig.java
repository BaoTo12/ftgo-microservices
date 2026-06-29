package com.chibao.kitchenservice.config;

import feign.Client;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class FeignClientSSLConfig {

    @Bean
    public Client feignClient() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keyStoreStream = new ClassPathResource("kitchen-keystore.p12").getInputStream()) {
                keyStore.load(keyStoreStream, "password".toCharArray());
            }

            KeyStore trustStore = KeyStore.getInstance("PKCS12");
            try (InputStream trustStoreStream = new ClassPathResource("kitchen-truststore.p12").getInputStream()) {
                trustStore.load(trustStoreStream, "password".toCharArray());
            }

            SSLContext sslContext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, "password".toCharArray())
                    .loadTrustMaterial(trustStore, null)
                    .build();

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .build();

            return new feign.httpclient.ApacheHttpClient(httpClient);

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to configure secure mTLS Feign Client!", ex);
        }
    }
}
