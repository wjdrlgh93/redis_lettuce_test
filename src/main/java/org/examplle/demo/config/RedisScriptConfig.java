package org.examplle.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<Boolean> checkAndSetScript(){
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        // 만든파일 지정
        script.setLocation(new ClassPathResource("scripts/checkAndSet.lua"));
        // 스크립트가 반환할 타입 지정 (Lua 에서 true/false 를 반환하므로 Boolean)
        script.setResultType(Boolean.class);
        return script;
    }
}
