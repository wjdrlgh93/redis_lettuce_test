package org.examplle.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 생성자 주입
    public TransactionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//    public void executeTransaction(String key, String value) {
//        redisTemplate.execute(new SessionCallback<List<Object>>() {
//            @Override
//            public List<Object> execute(RedisOperations operations) throws DataAccessException {
//                // 1. 특정 키 감시 (낙관적 락)
//                operations.watch(key);
//
//                // 2. 트랜잭션 시작 (이후 명령은 큐에 쌓임)
//                operations.multi();
//
//                operations.opsForValue().set(key, value);
//                operations.opsForValue().increment("counter");
//
//                // 3. 트랜잭션 실행 (커밋)
//                // 만약 watch 중인 key가 외부에서 변경되었다면 결과로 null이 반환됨
//                return operations.exec();
//            }
//        });
//    }
public boolean executeTransaction(String key, String value) {
    // 1. 해시 태그 { }를 사용하여 두 키가 반드시 같은 슬롯에 가게 함
    String taggedKey = "{" + key + "}:data";
    String taggedCounter = "{" + key + "}:counter";

    List<Object> results = redisTemplate.execute(new SessionCallback<List<Object>>() {
        @Override
        public List<Object> execute(RedisOperations operations) throws DataAccessException {
            operations.watch(taggedKey); // 낙관적 락

            operations.multi(); // 트랜잭션 시작

            operations.opsForValue().set(taggedKey, value);
            operations.opsForValue().increment(taggedCounter);

            return operations.exec(); // 결과 리스트 반환
        }
    });

    // 2. 결과가 비어있거나 null이면 트랜잭션 실패(watch에 의한 취소 등)
    return results != null && !results.isEmpty();
}



}
