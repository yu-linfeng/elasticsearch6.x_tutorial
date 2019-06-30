package com.coderbuff.transportclientelasticsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * Created by OKevin on 2019-06-30 13:30
 */
@Slf4j
@Component("elasticSearchClient")
public class ElasticSearchClient implements InitializingBean {

    private TransportClient transportClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("init ElasticSearch Client.");
        transportClient = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
    }

    public TransportClient getClient() {
        return transportClient;
    }
}
