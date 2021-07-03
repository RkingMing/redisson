package org.redisson.spring.starter;

import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCaching
public class RedissonApplication {

    public static void main(String[] args) {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer().setAddress("127.0.0.1:6379").setDatabase(0);

        ClusterServersConfig clusterServersConfig = config.useClusterServers().addNodeAddress("127.0.0.1:6379");

        RedissonClient redissonClient = Redisson.create(config);
        RLock lock = redissonClient.getLock("key");
        RLock lock2 = redissonClient.getLock("key");
        RLock lock3 = redissonClient.getLock("key");
        RedissonRedLock redissonRedLock = new RedissonRedLock(lock, lock2, lock3);
        boolean b = redissonRedLock.tryLock();
        redissonRedLock.unlock();

        try {
            lock.tryLock(1L, 10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (lock.isLocked() && lock.isHeldByCurrentThread()){
            lock.unlock();
        }
        SpringApplication.run(RedissonApplication.class, args);
    }
    
}
