package org.examplle.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.api.StatefulConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisProtocol;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.csc.CacheConfig;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

//    @Value("${spring.redis.username}")
//    private String username;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModules(new JavaTimeModule(), new Jdk8Module());
        return mapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
//        redisStandaloneConfiguration.setUsername(username);
        redisStandaloneConfiguration.setPassword(password);

        // pool 설정하여 연결하는 부분
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(10); // max-active
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(3);
        poolConfig.setTestOnBorrow(true);

        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .clientName("wesome-test")
                .build();

        LettuceConnectionFactory lettuceConnectionFactory =
                new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
        // 요청마다 별도의 연결 사용(트랜잭션, Pub/Sub, 비동기 처리, 멀티스레드 환경)
        lettuceConnectionFactory.setShareNativeConnection(false);

        return lettuceConnectionFactory;
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        RedisSerializer<Object> serializer = RedisSerializer.json();

        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash를 사용할 경우 시리얼라이저(Value 부분을 JsonString 형태로 저장 및 조회)
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        // 모든 경우
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
    @Bean
    public CommandLineRunner connectionTest(RedisTemplate<String, Object> redisTemplate) {
        return args -> {
            try {
                // 1. 실제로 데이터를 써서 연결을 활성화합니다.
                redisTemplate.opsForValue().set("test:connection", "active");
                System.out.println("==========================================");
                System.out.println(">>> Redis에 신호를 보냈습니다! (wesome-test)");
                System.out.println(">>> 이제 60초 동안 앱이 켜져 있습니다.");
                System.out.println(">>> 지금 바로 redis-cli에서 client list를 입력하세요!");
                System.out.println("==========================================");

                // 2. 확인하실 동안 앱이 꺼지지 않게 1분 대기
//                Thread.sleep(60000);
            } catch (Exception e) {
                System.err.println(">>> Redis 연결 실패: " + e.getMessage());
            }
        };
    }


    @Bean
    public UnifiedJedis unifiedJedis() {
        // 1. Redis 서버 주소 및 포트
        HostAndPort endPoint = new HostAndPort("192.168.3.50", 12000);

        // 2.RESP3 설정 (클라이언트 사이드캐싱의 필수조건)
        DefaultJedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password(password)
                .protocol(RedisProtocol.RESP3)
                .build();

        // 3. 로컬캐시 상세 설정
        // maxSize: 로컬 메모리에 저장할 키의 최대 갯수
        CacheConfig cacheConfig =CacheConfig.builder()
                .maxSize(1000)
                .build();
        return  new UnifiedJedis(endPoint, config, cacheConfig);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        // Redis와의 연결을 설정합니다.
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
