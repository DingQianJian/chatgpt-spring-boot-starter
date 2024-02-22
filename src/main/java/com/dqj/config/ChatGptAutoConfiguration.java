package com.dqj.config;

import com.dqj.service.ChatGptApiService;
import com.dqj.service.impl.ChatGptApiServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties({ChatGptConfig.class, RedisConfig.class})
public class ChatGptAutoConfiguration {

    @Resource
    private ChatGptConfig chatGptConfig;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public ChatGptApiService chatGptApiService() {
        return new ChatGptApiServiceImpl(chatGptConfig, redisTemplate);
    }

}
