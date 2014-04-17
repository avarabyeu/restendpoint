package com.github.avarabyeu.restendpoint.http;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;


/**
 * SSL Client Factory. Can be customized via {@link HttpRequestInterceptor}
 *
 * @author Andrei Varabyeu
 */
public class SslClientFactory extends BasicAuthClientFactory {

    private KeyStore keyStore;

    public SslClientFactory(Credentials credentials, KeyStore keyStore) {
        super(credentials);
        this.keyStore = keyStore;
    }

    public SslClientFactory(Credentials credentials, InputStream keyStore, String keyStorePass) {
        super(credentials);
        this.keyStore = loadKeyStore(keyStore, keyStorePass);
    }

    public SslClientFactory(Credentials credentials, String keyStoreResource, String keyStorePass, List<HttpRequestInterceptor> interceptors) {
        super(credentials, interceptors);
        this.keyStore = loadKeyStore(this.getClass().getClassLoader().getResourceAsStream(keyStoreResource), keyStorePass);

    }

    public SslClientFactory(Credentials credentials, InputStream keyStore, String keyStorePass, List<HttpRequestInterceptor> interceptors) {
        super(credentials, interceptors);
        this.keyStore = loadKeyStore(keyStore, keyStorePass);
    }

    @Override
    public HttpClient createHttpClient() {
        try {

            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore).build();

			/*
             * Unreal magic, but we can't use
			 * org.apache.http.conn.ssl.SSLConnectionSocketFactory
			 * .BROWSER_COMPATIBLE_HOSTNAME_VERIFIER here due to some problems
			 * related to classloaders. Initialize host name verifier explicitly
			 */
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new AllowAllHostnameVerifier());
            HttpClientBuilder builder = initDefaultBuilder();

            builder.setSSLSocketFactory(sslsf);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create http client", e);
        }
    }

    /**
     * Loads keystore
     *
     * @param keyStore
     * @param password
     * @return
     */
    private KeyStore loadKeyStore(InputStream keyStore, String password) {
        try {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(keyStore, password.toCharArray());
            return trustStore;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load trust store", e);
        } finally {
            IOUtils.closeQuietly(keyStore);
        }
    }

}
