package com.dqj.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.dqj.config.ChatGptConfig;
import com.dqj.model.dto.ChatGptChatDTO;
import com.dqj.model.vo.ChatGptChatVO;
import com.dqj.model.vo.ChatVO;
import com.dqj.service.ChatGptApiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatGptApiServiceImpl implements ChatGptApiService {

    @Resource
    private ChatGptConfig config;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public ChatGptApiServiceImpl(ChatGptConfig config, RedisTemplate<String, Object> redisTemplate) {
        this.config = config;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ChatVO newChat(String userId, String chatId, String systemMessage) {
        ChatVO chatVO = new ChatVO();
        chatVO.setChatId(chatId);
        chatVO.setUserId(userId);
        String key = userId + "_" + chatId;
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            ChatGptChatDTO.Message message = new ChatGptChatDTO.Message();
            message.setRole("system");
            message.setContent(systemMessage);
            String json = JSON.toJSONString(message);
            redisTemplate.opsForList().rightPushAll(key, json);
        }
        redisTemplate.expire(key, config.getExpire(), TimeUnit.MINUTES);
        return chatVO;
    }

    @Override
    public void destroy(String userId, String chatId) {
        String key = userId + "_" + chatId;
        redisTemplate.delete(key);
    }

    @Override
    public String chat(String question) {
        HttpRequest post = HttpUtil.createPost("https://api.openai.com/v1/chat/completions");
        post.header("Content-Type", "application/json").header("Authorization", "Bearer " + config.getKey());
        ChatGptChatDTO chatGptChatDTO = new ChatGptChatDTO();
        ChatGptChatDTO.Message message = new ChatGptChatDTO.Message();
        message.setRole("user");
        message.setContent(question);
        chatGptChatDTO.setMessages(List.of(message));
        chatGptChatDTO.setModel(config.getModel());
        log.info("[chat]: request: {}", JSON.toJSONString(chatGptChatDTO));
        post.body(JSON.toJSONString(chatGptChatDTO));
        String body;
        try (HttpResponse response = post.execute()) {
            body = response.body();
        }
        log.info("[chat]: response: {}", body);
        ChatGptChatVO vo = JSON.parseObject(body, ChatGptChatVO.class);
        return vo.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public String chat(List<ChatGptChatDTO.Message> messageList) {
        HttpRequest post = HttpUtil.createPost("https://api.openai.com/v1/chat/completions");
        post.header("Content-Type", "application/json").header("Authorization", "Bearer " + config.getKey());
        ChatGptChatDTO chatGptChatDTO = new ChatGptChatDTO();
        chatGptChatDTO.setMessages(messageList);
        chatGptChatDTO.setModel(config.getModel());
        log.info("[chat]: request: {}", JSON.toJSONString(chatGptChatDTO));
        post.body(JSON.toJSONString(chatGptChatDTO));
        String body;
        try (HttpResponse response = post.execute()) {
            body = response.body();
        }
        log.info("[chat]: response: {}", body);
        ChatGptChatVO vo = JSON.parseObject(body, ChatGptChatVO.class);
        return vo.getChoices().get(0).getMessage().getContent();
    }

    @Override
    public String chat(String userId, String chatId, String question, boolean hasContext) {
        String key = userId + "_" + chatId;
        if (!hasContext) {
            return this.chat(question);
        }
        if (!StringUtils.hasLength(chatId) || !StringUtils.hasLength(userId)) {
            return "Error: chatId and userId cannot be null or blank!";
        }
        ChatGptChatDTO.Message message = new ChatGptChatDTO.Message();
        message.setRole("user");
        message.setContent(question);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return "Please use newChat() API to start conversation.";
        }
        ChatGptChatDTO.Message userMsg = new ChatGptChatDTO.Message();
        userMsg.setRole("user");
        userMsg.setContent(question);
        List<Object> messageList = redisTemplate.opsForList().leftPop(key, size);
        assert messageList != null;
        messageList.add(JSON.toJSONString(userMsg));

        List<ChatGptChatDTO.Message> list = new ArrayList<>();
        for (Object o : messageList) {
            String json = (String) o;
            ChatGptChatDTO.Message msg = JSON.parseObject(json, ChatGptChatDTO.Message.class);
            list.add(msg);
        }
        String answer = chat(list);
        ChatGptChatDTO.Message answerMsg = new ChatGptChatDTO.Message();
        answerMsg.setContent(answer);
        answerMsg.setRole("assistant");
        messageList.add(JSON.toJSONString(answerMsg));
        log.info("[chat]: MessageList: {}", JSON.toJSONString(messageList));
        int messageSize = messageList.size();
        int configSize = config.getSize();
        if (messageSize > configSize) {
            do {
                messageList.remove(1);
                messageSize = messageList.size();
            } while (messageSize > configSize);
        }
        redisTemplate.opsForList().rightPushAll(key, messageList);
        redisTemplate.expire(key, config.getExpire(), TimeUnit.MINUTES);
        return answer;
    }
}
