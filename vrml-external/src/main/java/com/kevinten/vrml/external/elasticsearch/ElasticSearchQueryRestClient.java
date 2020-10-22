package com.kevinten.vrml.external.elasticsearch;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * The Elastic search query rest client.
 */
public class ElasticSearchQueryRestClient extends RestHighLevelClient {

    /**
     * Instantiates a new Elastic search query rest client.
     *
     * @param elasticSearchRestConfig the elastic search rest config
     */
    public ElasticSearchQueryRestClient(ElasticSearchRestConfig elasticSearchRestConfig) {
        super(RestClient
                .builder(
                        new HttpHost(elasticSearchRestConfig.getAddress(), elasticSearchRestConfig.getPort(), elasticSearchRestConfig.getProtocol()))
                .setDefaultHeaders(
                        new Header[]{new BasicHeader(HttpHeaders.AUTHORIZATION, elasticSearchRestConfig.getBaseAuth())})
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder.setSocketTimeout(elasticSearchRestConfig.getSocketTimeout())));
    }

    /**
     * The ElasticSearch's rest client configuration.
     */
    public static class ElasticSearchRestConfig {

        // ElasticSearch's address.
        private String address;
        // ElasticSearch's port.
        private int port;
        // ElasticSearch's protocol.
        private String protocol;
        // ElasticSearch's base Auth
        private String baseAuth;
        // ElasticSearch's socket timeout duration.
        private int socketTimeout;

        /**
         * Gets address.
         *
         * @return the address
         */
        public String getAddress() {
            return address;
        }

        /**
         * Sets address.
         *
         * @param address the address
         */
        public void setAddress(String address) {
            this.address = address;
        }

        /**
         * Gets port.
         *
         * @return the port
         */
        public int getPort() {
            return port;
        }

        /**
         * Sets port.
         *
         * @param port the port
         */
        public void setPort(int port) {
            this.port = port;
        }

        /**
         * Gets protocol.
         *
         * @return the protocol
         */
        public String getProtocol() {
            return protocol;
        }

        /**
         * Sets protocol.
         *
         * @param protocol the protocol
         */
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        /**
         * Gets base auth.
         *
         * @return the base auth
         */
        public String getBaseAuth() {
            return baseAuth;
        }

        /**
         * Sets base auth.
         *
         * @param baseAuth the base auth
         */
        public void setBaseAuth(String baseAuth) {
            this.baseAuth = baseAuth;
        }

        /**
         * Gets socket timeout.
         *
         * @return the socket timeout
         */
        public int getSocketTimeout() {
            return socketTimeout;
        }

        /**
         * Sets socket timeout.
         *
         * @param socketTimeout the socket timeout
         */
        public void setSocketTimeout(int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }
    }
}
