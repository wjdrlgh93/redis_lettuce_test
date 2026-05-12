package org.examplle.demo.controller;

import org.examplle.demo.service.PipeLiningService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
public class PipeController {
    private final PipeLiningService pipeLiningService;
    private final StringRedisTemplate stringRedisTemplate;

    public PipeController(PipeLiningService pipeLiningService, StringRedisTemplate stringRedisTemplate) {
        this.pipeLiningService = pipeLiningService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 1. 테스트 전 데이터 채우기 (LPUSH)
//    @PostMapping("/seed")
    @GetMapping("/seed")
    public String seedData(@RequestParam int count) {
        for (int i = 1; i <= count; i++) {
            stringRedisTemplate.opsForList().leftPush("myqueue", "Item-" + i);
        }
        return count + "개의 아이템이 'myqueue'에 담겼습니다.";
    }

    // 2. 파이프라이닝 테스트 (RPOP)
    @GetMapping("/pop")
    public String popItems(@RequestParam int batchSize) {
        pipeLiningService.popMultipleItems(batchSize);
        return "콘솔 로그(System.out)를 확인해 보세요! " + batchSize + "개를 Pop 했습니다.";
    }
}
