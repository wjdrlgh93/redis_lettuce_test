package org.examplle.demo.runner;


import org.examplle.demo.service.RedisScriptService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RedisTestRunner implements CommandLineRunner {

    private final RedisScriptService redisScriptService;

    public RedisTestRunner(RedisScriptService redisScriptService) {
        this.redisScriptService = redisScriptService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Redis 스크립트 실습 시작 ===");
        redisScriptService.testScripting();
        System.out.println("=== Redis 실습 종료 ===");
    }
}
