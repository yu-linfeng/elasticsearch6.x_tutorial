package com.coderbuff.es.config;

import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.InitializingBean;

/**
 * 连接ES服务
 * Created by OKevin on 2019-06-26 00:45
 */
public class EsClient implements InitializingBean {

    private TransportClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (client == null) {

        }
    }

    public TransportClient getClient() {
        return client;
    }
}
