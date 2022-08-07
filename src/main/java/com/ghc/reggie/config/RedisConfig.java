package com.ghc.reggie.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

/**
 * @author 郭昊晨
 * @version 1.0
 * 2022/8/6 - 22:31
 */
@Configuration
public class RedisConfig  extends CachingConfigurerSupport {

    @Resource
    RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<Object,Object> redisTemplate(){
        RedisTemplate<Object,Object> redisTemplate=new RedisTemplate<>();
        //默认的key序列化为，JDKSerializationRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}
