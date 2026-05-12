package org.examplle.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional
    public void doSomething(){
        redisTemplate.opsForValue().set("thing1", "value1");
        // 트랜잭션 내에서 get을 호출하면 큐에 쌓인 상태라 null이 반환될 수 있음에 주의
    }
}
