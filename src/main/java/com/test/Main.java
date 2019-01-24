package com.test;

import com.test.UserVO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;


public class Main {

    public static void main(String[] args) {
        //ApplicationContext applicationContext = null;
        //applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring-redis-new.xml"});

        //RedisClient rc = (RedisClient)applicationContext.getBean("redisCacheManager");
        //RedisService rs = (RedisService)applicationContext.getBean("redisService");

        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisClient redisClient = ctx.getBean(RedisClient.class);

        UserVO user1 = new UserVO();
        user1.setName("user1");
        user1.setAge(18);
        user1.setAddress("beijing");

        UserVO user2 = new UserVO();
        user2.setName("user2");
        user2.setAge(19);
        user2.setAddress("shanghai");


        //根据配置文件重新动态调整RedisTemplate
        /**********************************************************************************
         redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
         redisTemplate.opsForValue().set("user",userVo);
         redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserVO.class));
         redisTemplate.opsForValue().set("userjson",userVo);
         */

        //redisClient.getRedisTemplate().setKeySerializer(new StringRedisSerializer());
        //redisClient.getRedisTemplate().setValueSerializer(new Jackson2JsonRedisSerializer< >(UserVO.class));

        //redisClient.renameKey("user5","user6");


        //redisClient.set("user1",user1);
        //"[\"com.test.domain.UserVO\",{\"name\":\"user1\",\"address\":\"beijing\",\"age\":18}]"
       // redisClient.expireKey("user1",600, TimeUnit.SECONDS);

        redisClient.set("user1",user1,60,TimeUnit.SECONDS);
        UserVO vo = (UserVO) redisClient.get("user1");
        System.out.println(vo.getAddress());

        long key_expire = redisClient.getKeyExpire("user1",TimeUnit.SECONDS);
        System.out.println("key_expire is "+key_expire);



        redisClient.hset("map_key","mapitem",user1);
        UserVO user_map = (UserVO)redisClient.hget("map_key","mapitem");
        System.out.println("map_key:user_map"+user_map);










        //System.out.println(redisClient.getRedisTemplate().opsForValue().get("user5"));
        //redisClient.setex("ex","1", Duration.);


        //ValueOperations<String,Object> operations = rc.getRedisTemplate().opsForValue();


//        System.out.println(operations.get(userVo));

        //rs.deleteKey("user");

        //System.out.println(rs.existsKey("user"));


        /*
        2. 针对jedis客户端中大量api进行了归类封装,将同一类型操作封装为operation接口
        ValueOperations：简单K-V操作
        SetOperations：set类型数据操作
        ZSetOperations：zset类型数据操作
        HashOperations：针对map类型的数据操作
        ListOperations：针对list类型的数据操作
        3. 提供了对key的“bound”(绑定)便捷化操作API，可以通过bound封装指定的key，然后进行一系列的操作而无须“显式”的再次指定Key，即BoundKeyOperations：
        BoundValueOperations
        BoundSetOperations
        BoundListOperations
        BoundSetOperations
        BoundHashOperations

        5. 针对数据的“序列化/反序列化”，提供了多种可选择策略(RedisSerializer)
        JdkSerializationRedisSerializer：POJO对象的存取场景，使用JDK本身序列化机制，将pojo类通过ObjectInputStream/ObjectOutputStream进行序列化操作，最终redis-server中将存储字节序列。是目前最常用的序列化策略。
        StringRedisSerializer：Key或者value为字符串的场景，根据指定的charset对数据的字节序列编码成string，是“new String(bytes, charset)”和“string.getBytes(charset)”的直接封装。是最轻量级和高效的策略。
        JacksonJsonRedisSerializer：jackson-json工具提供了javabean与json之间的转换能力，可以将pojo实例序列化成json格式存储在redis中，也可以将json格式的数据转换成pojo实例。因为jackson工具在序列化和反序列化时，需要明确指定Class类型，因此此策略封装起来稍微复杂。【需要jackson-mapper-asl工具支持】
        OxmSerializer：提供了将javabean与xml之间的转换能力，目前可用的三方支持包括jaxb，apache-xmlbeans；redis存储的数据将是xml工具。不过使用此策略，编程将会有些难度，而且效率最低；不建议使用。【需要spring-oxm模块的支持】
        如果你的数据需要被第三方工具解析，那么数据应该使用StringRedisSerializer而不是JdkSerializationRedisSerializer。
        */
        ApplicationContext applicationContext = null;
        applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring-redis.xml"});

        RedisUtil redisUtil = (RedisUtil)applicationContext.getBean("redisUtil");
        redisUtil.set("user2",user2);
        //"{\"@class\":\"com.test.domain.UserVO\",\"name\":\"user2\",\"address\":\"shanghai\",\"age\":19}"
        System.out.println(redisUtil.get("user2"));








    }
}
