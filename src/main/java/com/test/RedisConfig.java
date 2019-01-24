package com.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

//import org.msgpack.jackson.dataformat.MessagePackFactory;

//@Configuration
@ComponentScan(basePackages = {"com.test"})
@PropertySource("classpath:redis.properties") //扫描包下只要有一个
public class RedisConfig{

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
    public long maxWaitMillis;

    @Value("${redis.testOnBorrow}")
    public String testOnBorrow;

    @Value("${redis.testOnReturn}")
    public String testOnReturn;


    @Autowired
    JedisConfig jedisConfig;

    @Autowired
    JedisConnectionFactory jedisConnectionFactory;


    /*
    JedisPool 配置
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        System.out.println(jedisConfig.dbIndex);

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        //jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        //jedisPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //jedisPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        return jedisPoolConfig;
    }

    /*************************************************************************************************
    Standalone 部署配置方式
     */
    /*
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(dbIndex);
        return redisStandaloneConfiguration;
    }*/

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {

        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName(host);
        redisStandaloneConfig.setPort(port);

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder
                jedisPoolConfigBuilder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();

        jedisPoolConfigBuilder.poolConfig(jedisPoolConfig);

        //public JedisConnectionFactory(RedisStandaloneConfiguration standaloneConfig, JedisClientConfiguration clientConfig)
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfig,jedisPoolConfigBuilder.build());
        return jedisConnectionFactory;

       /*
        JedisConnectionFactory factory = new JedisConnectionFactory();
        new JedisConnectionFactory(redisStandaloneConfiguration());
        factory.setPoolConfig(jedisPoolConfig);
        factory.setHostName(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setTimeout(timeout);
        factory.setUsePool(usePool);
        factory.setDatabase(database);
        return factory;
        */
    }


   @Bean(name = "redisTemplate")
   public RedisTemplate redisTemplate(){
       RedisTemplate redisTemplate = new RedisTemplate();
       redisTemplate.setConnectionFactory(jedisConnectionFactory);

       Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
       ObjectMapper objectMapper = new ObjectMapper();
       objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
       objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
       //objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

       jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

       RedisSerializer stringRedisSerializer = new StringRedisSerializer();

       redisTemplate.setKeySerializer(stringRedisSerializer);
       redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
       redisTemplate.setHashKeySerializer(stringRedisSerializer);
       redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
       redisTemplate.afterPropertiesSet();
       return redisTemplate;

      /*RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
       redisTemplate.setKeySerializer(new StringRedisSerializer());
       redisTemplate.setHashKeySerializer(new StringRedisSerializer());
       ObjectMapper mapper = new ObjectMapper(new MessagePackFactory());
       //设置存储到redis中的日期格式
       mapper.setDateFormat(new SimpleDateFormat("yyyyMMddHHmmss"));
       Jackson2JsonRedisSerializer Jackson2Serializer = new Jackson2JsonRedisSerializer(Object.class);
       Jackson2Serializer.setObjectMapper(mapper);
       RedisSerializer redisSerializer = Jackson2Serializer;
       redisTemplate.setValueSerializer(redisSerializer);
       redisTemplate.setHashValueSerializer(redisSerializer);
       redisTemplate.setConnectionFactory(jedisConnectionFactory);
       return redisTemplate;
       */
   }

}

