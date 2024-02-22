package com.dqj.config;

import com.dqj.service.ChatGptApiService;
import com.dqj.service.impl.ChatGptApiServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ChatGptConfig.class)
public class ChatGptAutoConfiguration {

    @Resource
    private ChatGptConfig chatGptConfig;

    @Bean
    @ConditionalOnMissingBean
    public ChatGptApiService chatGptApiService() {
        return new ChatGptApiServiceImpl(chatGptConfig);
    }

}
