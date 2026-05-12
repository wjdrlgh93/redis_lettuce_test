package org.examplle.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RedisScriptService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> checkAndSetScript;

    public void testScripting(){
        String key ="ttestt:script:key";

        // init
        redisTemplate.opsForValue().set(key, "apple");
        System.out.println("초기 값: " + redisTemplate.opsForValue().get(key));

        // 2. 스크립트 실행 준비
        // Lua 스크립트의 KEYS[1]에 들어갈 값들을 List로 만듭니다.
        List<String> keys = Collections.singletonList(key);

        // 3. 실패하는 케이스: 현재 값이 "banana"일 때 "grape"로 바꿔라
        Boolean isFail = redisTemplate.execute(checkAndSetScript, keys, "banana", "grape");
        System.out.println("banana -> grape 변경 시도 결과: " + isFail); // false 출력 예상

        // 4. 성공하는 케이스: 현재 값이 "apple"일 때 "orange"로 바꿔라
        Boolean isSuccess = redisTemplate.execute(checkAndSetScript, keys, "apple", "orange");
        System.out.println("apple -> orange 변경 시도 결과: " + isSuccess); // true 출력 예상
        System.out.println("최종 값: " + redisTemplate.opsForValue().get(key)); // "orange" 출력 예상
    }
}
