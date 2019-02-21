package com.test;

import com.test.lock.RedisDistributedLock;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IRunnable1 implements Runnable {

    public IRunnable1() {
    }

    private RedisDistributedLock lock;

    public RedisDistributedLock getLock() {
        return lock;
    }

    public void setLock(RedisDistributedLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisClient redisClient = ctx.getBean(RedisClient.class);

        //DistributedRedisLock lock = new DistributedRedisLock(redisClient.getRedisTemplate(),"count_key");

       while(Integer.parseInt(redisClient.get("count").toString())!=0){
           lock.lock("redis_lock",1000,5,500);
           System.out.println(Thread.currentThread().getName()+"当前count值"+redisClient.get("count"));
           redisClient.decr("count",1);
           lock.releaseLock("redis_lock");
       }


    }
}
