package org.examplle.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClusterTestService {
    private final StringRedisTemplate redisTemplate;

    public void testCluster(){
        for (int i = 0; i <100; i++) {
            redisTemplate.opsForValue().set("key"+i,"value"+i);
        }
    }
}
