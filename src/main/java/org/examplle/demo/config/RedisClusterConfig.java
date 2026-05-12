package org.examplle.demo.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class RedisClusterConfig {

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        // 1. 클러스터 노드 설정
//        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(
//                Arrays.asList("192.168.3.51:10010", "192.168.3.52:10010", "192.168.3.53:10010"));
//
//        // 2. 토폴로지 자동 갱신 설정
//        ClusterTopologyRefreshOptions refreshOptions = ClusterTopologyRefreshOptions.builder()
//                .enableAllAdaptiveRefreshTriggers() // 노드 추가/삭제 등 이벤트 발생 시 갱신
//                .refreshPeriod(Duration.ofMinutes(10))
//                .build();
//
//        ClusterClientOptions clientOptions = ClusterClientOptions.builder()
//                .topologyRefreshOptions(refreshOptions)
//                .build();
//
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .clientOptions(clientOptions)
//                .build();
//
//        return new LettuceConnectionFactory(clusterConfig, clientConfig);
//    }
}
