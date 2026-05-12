package org.examplle.demo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class LuaScriptingService {

    private final StringRedisTemplate redisTemplate;

    public LuaScriptingService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String checkAndSet(String key, String expectedValue, String newValue) {
        // 1. 스크립트 로드
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/check_and_set.lua"));
        script.setResultType(String.class);

        // 2. 실행 (키 목록, 인자 목록 전달)
        // 리스트로 전달하는 이유는 루아의 KEYS, ARGV 배열에 매핑하기 위함입니다.
        return redisTemplate.execute(script, Collections.singletonList(key), expectedValue, newValue);
    }
}