package com.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IRunnable implements Runnable {

    public IRunnable() {
    }

    private DistributedRedisLock lock;

    public DistributedRedisLock getLock() {
        return lock;
    }

    public void setLock(DistributedRedisLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(RedisConfig.class);
        RedisClient redisClient = ctx.getBean(RedisClient.class);

        //DistributedRedisLock lock = new DistributedRedisLock(redisClient.getRedisTemplate(),"count_key");

       while(Integer.parseInt(redisClient.get("count").toString())!=0){
           lock.lock();
           System.out.println(Thread.currentThread().getName()+"当前count值"+redisClient.get("count"));
           redisClient.decr("count",1);
           lock.unlock();
       }


    }
}
