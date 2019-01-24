package com.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
//@PropertySource("classpath:redis.properties")
public class JedisConfig {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    public int port;

    @Value("${redis.maxTotal}")
    public int maxTotal;

    @Value("${redis.maxIdle}")
    public int maxIdle;

    @Value("${redis.dbIndex}")
    public int dbIndex;

    @Value("${redis.maxWaitMillis}")
    public String maxWaitMillis;

    @Value("${redis.testOnBorrow}")
    public String testOnBorrow;

    @Value("${redis.testOnReturn}")
    public String testOnReturn;

    /*
    @Value("${redis.minIdle}")
    public int minIdle;

    @Value("${redis.maxActive}")
    public int maxActive;

    @Value("${redis.timeout}")
    public String timeout;
    */


}
