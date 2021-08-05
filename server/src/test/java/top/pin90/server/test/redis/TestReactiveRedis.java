package top.pin90.server.test.redis;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.ObjectHashMapper;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Hooks;
import top.pin90.common.pojo.info.ServerInfo;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestReactiveRedis {
    ReactiveRedisConnectionFactory factory;
    ReactiveRedisTemplate<Object, Object> template;
    ReactiveValueOperations<Object, Object> opsForValue;

    @BeforeAll
    public void init(){
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build();
        LettuceConnectionFactory factory1 = new LettuceConnectionFactory(new RedisStandaloneConfiguration("192.168.0.111", 6379), clientConfig);
        factory1.afterPropertiesSet();
        factory = factory1;
        template = new ReactiveRedisTemplate<>(factory, RedisSerializationContext.java());
//        new ReactiveStringRedisTemplate()
        RedisTemplate redisTemplate;

        opsForValue = template.opsForValue();
        Hooks.onOperatorDebug();
    }
    @AfterEach
    public void afterEach() throws InterruptedException {
        Thread.currentThread().join();

    }
    @Test
    public void set(){
        opsForValue.set("key1",1)
                .subscribe(System.out::println);
    }
    @Test
    public void stringSet(){
        ReactiveValueOperations<String, String> ops = template.opsForValue(RedisSerializationContext.string());
    }

    @Test
    public void setIfAbsent(){
        opsForValue.getAndSet("skey1","value")
                .subscribe(System.out::println);
    }
    @Test
    public void increment(){
        String key="ccc";
        ReactiveValueOperations<String, String> ops = template.opsForValue(RedisSerializationContext.string());
        ops.increment(key,1).subscribe(System.out::println);
    }
    @Test
    public void get(){
        opsForValue.get("ikey").subscribe(System.out::println);
    }
    @Test
    public void setHash(){
        ReactiveHashOperations<String, Object, Object> opsForHash = template.opsForHash(RedisSerializationContext.string());
    }
    @Test
    public void setHashObject(){
//        ReactiveHashOperations<String, Object, Object> opsForHash = template.opsForHash(RedisSerializationContext.string());
        ReactiveHashOperations<Object, Object, Object> opsForHash = template.opsForHash(RedisSerializationContext.java());

        ServerInfo info = new ServerInfo("localhost", 10000);
        HashMapper<Object, byte[], byte[]> mapper = new ObjectHashMapper();
        HashMap<String, Object> map = new HashMap<>();
//        map.put("sk1","ssss");
//        map.put("ik1",2);
//        opsForHash.putAll("hashObject",map).subscribe(System.out::println);
        Map<byte[], byte[]> toHash = mapper.toHash(info);
        opsForHash.putAll("hashObject", toHash).subscribe(System.out::println);
    }
    @Test
    public void hashIncrement(){
        ReactiveHashOperations<String, Object, Object> opsForHash = template.opsForHash(RedisSerializationContext.string());
        opsForHash.increment("hashObject","port",1).subscribe(System.out::println);

    }

}
