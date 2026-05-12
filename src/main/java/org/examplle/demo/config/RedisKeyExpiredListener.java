package org.examplle.demo.config;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpiredListener(
            RedisMessageListenerContainer listenerContainer
    ) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageToStr = message.toString();

        if (messageToStr.startsWith("test:")) {
            System.out.println("📩 Received message: " + message.toString()); // message에는 만료된 키가 리턴됨.
        }

    }

}
