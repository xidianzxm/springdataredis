package com.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RedisClient<T> {


    @Autowired
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


    // ============================Common=============================

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


    // ============================String=============================
    /**
     * 递增
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }


    /**
     * 递减
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
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
    public boolean set(String key, T value, long l,TimeUnit timeUnit) {
        try {

            redisTemplate.opsForValue().set(key,value,l,timeUnit);
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
    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public T hget(String key, String item) {
        return (T) redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time,TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expireKey(key, time,timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time,TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expireKey(key, time,timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }



// ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<T> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, T... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, TimeUnit timeUnit,T... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expireKey(key, time,timeUnit);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }



    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<T> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, T value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, T value, long time,TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expireKey(key, time,timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<T> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<T> value, long time,TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expireKey(key, time,timeUnit);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, T value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, T value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    // ===============================Mixed=====================================
    /**
     * 功能描述：获取所有键值
     *
     * @param
     * @return
     * @author
     * @Date 2018/9/7 16:05
     */
    public Set<String> getKeys(String pattern) {
        return redisTemplate.keys(pattern);


    }

    /**
     * 功能描述：通过key值模糊查询匹配带key的map集合
     *
     * @param decode 是否编码（true-Base64,false-解码Base64）
     * @return Map<String       ,   Object>
     * @Author
     * @Param key
     * @Param flag true-取 与的值  false-取 或的值
     * @Date 2018/8/31 10:16
     */
    public Map<String, Object> likeMap(Map<Object, Object> map, Boolean isAnd, Boolean decode, String... keys) {
        Map<String, Object> resultMap = new HashMap<>();
        if (keys.length > 0 && !map.isEmpty()) {
            if (isAnd) {
                for (Map.Entry entry : map.entrySet()) {
                    for (int i = 0; i < keys.length; i++) {
                        /**忽略大小写判断*/
                        Pattern pattern = Pattern.compile(keys[i], Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(entry.getKey().toString());
                        if (!matcher.find()) {
                            break;
                        } else if (keys.length - 1 == i) {
                            if (decode) {
                                resultMap.put(entry.getKey().toString(), entry.getValue());
                            } else {
                                resultMap.put(entry.getKey().toString(), ConvertUtil.decodeBase64(entry.getValue().toString()));
                            }

                        }
                    }
                }
            } else {
                for (String key : keys) {
                    for (Map.Entry entry : map.entrySet()) {
                        /**忽略大小写判断*/
                        Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(entry.getKey().toString());
                        if (matcher.find()) {
                            if (decode) {
                                resultMap.put(entry.getKey().toString(), entry.getValue());
                            } else {
                                resultMap.put(entry.getKey().toString(), ConvertUtil.decodeBase64(entry.getValue().toString()));
                            }
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 功能描述：获取模糊匹配的map集合
     *
     * @param flag true-取 与的值  false-取 或的值
     * @return
     * @author Jesson
     * @Date 2018/9/7 10:26
     */
    public Map<String, Object> likeMap(Map<String, Object> map, Boolean flag, String... keys) {
        Map<String, Object> resultMap = new HashMap<>();
        if (keys.length > 0 && !map.isEmpty()) {
            if (flag) {
                for (Map.Entry entry : map.entrySet()) {
                    for (int i = 0; i < keys.length; i++) {
                        /**忽略大小写判断*/
                        Pattern pattern = Pattern.compile(keys[i], Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(entry.getKey().toString());
                        if (!matcher.find()) {
                            break;
                        } else if (keys.length - 1 == i) {
                            resultMap.put(entry.getKey().toString(), entry.getValue());
                        }
                    }
                }
            } else {
                for (String key : keys) {
                    for (Map.Entry entry : map.entrySet()) {
                        /**忽略大小写判断*/
                        Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(entry.getKey().toString());
                        if (matcher.find()) {
                            resultMap.put(entry.getKey().toString(), entry.getValue());
                        }
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 功能描述：查询结果集
     *
     * @param key     键值
     * @param type    文件类型（pack，unpack，template，Exception等）
     * @param txnCode 交易码
     * @param decode  是否编码（true-Base64,false-解码Base64）
     * @return
     * @author
     * @Date
     */
    public Map<String, Object> hmget(String key, Boolean decode, String type, String txnCode) {
        Map<Object, Object> hmap = hmget(key);
        if (!hmap.isEmpty()) {
            return likeMap(hmap, true, decode, type, txnCode);
        } else {
            return null;
        }
    }

    /**
     * 功能描述：在前缀筛选结果集中获取目标结果集
     *
     * @param args 为 "且" 关系
     * @return
     * @author
     * @Date
     */
    public Set<String> getKeySet(String condition, String... args) {
        Set<String> resultSet = new HashSet<>();
        if (!condition.isEmpty()) {
            Set<String> set = getKeys(condition);
            if (set.size() > 0) {
                if (args.length > 0) {
                    for (String rs : set) {
                        for (int i = 0; i < args.length; i++) {
                            /**忽略大小写判断*/
                            Pattern pattern = Pattern.compile(args[i], Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(rs);
                            if (!matcher.find()) {
                                break;
                            } else if (args.length - 1 == i) {
                                resultSet.add(rs);
                            }
                        }
                    }
                } else {
                    return set;
                }

            }
        }
        return resultSet;
    }

    /**
     * 将非对账类交易入库。
     *
     * @return 缓存键值对应的数据
     */

    /*
    public boolean setListObject(T value) {

        //logger.debug("存入list缓存 key:" + );
        try {
            redisTemplate.opsForList().leftPush("igaps.db.txn.general", value);
            return true;
        } catch (Exception ex) {
            //GapsLogger.sysError(ExceptionUtil.getExceptonInfo(ex));
            return false;
        }
   */

















    /****************************************************************
     * 对redis字符串类型数据操作
     *ValueOperations：简单K-V操作
     *SetOperations：set类型数据操作
     *ZSetOperations：zset类型数据操作
     *HashOperations：针对map类型的数据操作
     *ListOperations：针对list类型的数据操作
     */
    /****************************************************************
     * 对String类型的数据操作
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


}

class ConvertUtil{
   public static Object decodeBase64(String s){
       return null;
   }
}
