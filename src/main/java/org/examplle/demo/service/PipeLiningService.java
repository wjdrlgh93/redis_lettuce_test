package org.examplle.demo.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PipeLiningService {

    private final StringRedisTemplate stringRedisTemplate;

    public PipeLiningService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    public void popMultipleItems(int batchSize) {
        // executePipelined를 호출하여 파이프라인 시작
        List<Object> results = stringRedisTemplate.executePipelined(
                new RedisCallback<Object>() {
                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        // String 타입으로 처리하기 위해 커넥션 래핑
                        StringRedisConnection stringRedisConn = new DefaultStringRedisConnection(connection);

                        for(int i = 0; i < batchSize; i++) {
                            stringRedisConn.rPop("myqueue");
                            // 큐에서 아이템 꺼내기 (응답을 당장 기다리지 않고 큐잉됨)
                        }

                        // 주의: 파이프라인 안의 콜백은 반드시 null을 반환해야 합니다.
                        return null;
                    }
                }
        );

        // 결과 리스트에는 파이프라인 내부에서 실행한 순서대로 모든 응답이 담겨 있습니다.
        System.out.println("Pipelining Results: " + results);
    }
}
