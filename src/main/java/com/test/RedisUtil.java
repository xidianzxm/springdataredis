package com.test;

import org.springframework.data.redis.core.*;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RedisUtil<T> {

    private RedisTemplate<String, T> redisTemplate;

    public RedisTemplate<String, T> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


/***************************************************************
     通过RedisTemplate类做的关于key的操作
     */

    /**
     * 判断是否存在key
     *
     * @param key
     */
    public boolean existsKey(String key) {
        return this.redisTemplate.hasKey(key);
    }



    /**
     * 重名名key，如果newKey已经存在，则newKey的原值被覆盖
     *
     * @param oldKey
     * @param newKey
     */
    public void renameKey(String oldKey, String newKey) {
        this.redisTemplate.rename(oldKey, newKey);
    }


    /**
     * newKey不存在时才重命名
     *
     * @param oldKey
     * @param newKey
     * @return 修改成功返回true
     */
    public boolean renameKeyNotExist(String oldKey, String newKey) {
        return this.redisTemplate.renameIfAbsent(oldKey, newKey);
    }


    /**
     * 删除key
     *
     * @param key
     */
    public void deleteKey(String key) {
        this.redisTemplate.delete(key);
    }

    /**
     * 删除多个key
     *
     * @param keys
     */
    public void deleteKey(String... keys) {
        Set<String> kSet = Stream.of(keys).map(k -> k).collect(Collectors.toSet());
        this.redisTemplate.delete(kSet);
    }

    /**
     * 删除Key的集合
     *
     * @param keys
     */
    public void deleteKey(Collection<String> keys) {
        Set<String> kSet = keys.stream().map(k -> k).collect(Collectors.toSet());
        this.redisTemplate.delete(kSet);
    }

    /**
     * 设置key的生命周期
     *
     * @param key
     * @param time
     * @param timeUnit
     */
    public void expireKey(String key, long time, TimeUnit timeUnit) {
        this.redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 指定key在指定的日期过期
     *
     * @param key
     * @param date
     */
    public void expireKeyAt(String key, Date date) {
        this.redisTemplate.expireAt(key, date);
    }

    /**
     * 查询key的生命周期
     *
     * @param key
     * @param timeUnit
     * @return
     */
    public long getKeyExpire(String key, TimeUnit timeUnit) {
        return this.redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 将key设置为永久有效
     *
     * @param key
     */
    public void persistKey(String key) {
        this.redisTemplate.persist(key);
    }







    /****************************************************************
     * 对redis字符串类型数据操作
     *ValueOperations：简单K-V操作
     *SetOperations：set类型数据操作
     *ZSetOperations：zset类型数据操作
     *HashOperations：针对map类型的数据操作
     *ListOperations：针对list类型的数据操作
     */

    public ValueOperations<String, T> valueOperations() {
        return (ValueOperations<String, T>) redisTemplate.opsForValue();
    }



    /****************************************************************
     * 对hash类型的数据操作
     */
    public HashOperations<String, String, T> hashOperations() {
        return redisTemplate.opsForHash();
    }


    /****************************************************************
     * 对链表类型的数据操作
     */
    public ListOperations<String, T> listOperations() {
        return redisTemplate.opsForList();
    }

    /****************************************************************
     * 对无序集合类型的数据操作
     */
    public SetOperations<String, T> setOperations() {
        return redisTemplate.opsForSet();
    }


    /*****************************************************************
     * 对有序集合类型的数据操作
     */
    public ZSetOperations<String, T> zSetOperations(RedisTemplate<String, T> redisTemplate) {
        return redisTemplate.opsForZSet();
    }




    public boolean set(String key, T value) {
        try {

            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public T get(String key) {
        try {
          return (T) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    public boolean setex(String key, Object value,Duration duration) {
        try {
            redisTemplate.opsForValue().set("test","testvalue",60);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/


    /*
    ValueOperations：简单K-V操作
    SetOperations：set类型数据操作
    ZSetOperations：zset类型数据操作
    HashOperations：针对map类型的数据操作
    ListOperations：针对list类型的数据操作
    */

}
