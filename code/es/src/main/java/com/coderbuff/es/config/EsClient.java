package com.coderbuff.es.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * 连接ES服务
 * Created by OKevin on 2019-06-26 00:45
 */
@Component("esClient")
public class EsClient implements InitializingBean {

    /**
     * ES服务host
     */
    private static final String HOST = "localhost";

    /**
     * ES服务端口，socket连接的ES端口是9300，http连接的ES端口是9200
     */
    private static final int PORT = 9300;

    /**
     * ES客户端连接
     */
    private TransportClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST), PORT));
    }

    /**
     * 获取ES客户端连接
     * @return ES客户端
     */
    public TransportClient getClient() {
        return client;
    }
}
