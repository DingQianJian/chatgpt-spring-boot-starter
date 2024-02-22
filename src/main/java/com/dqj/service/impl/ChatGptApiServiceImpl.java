package com.dqj.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.dqj.config.ChatGptConfig;
import com.dqj.model.dto.ChatGptChatDTO;
import com.dqj.model.vo.ChatGptChatVO;
import com.dqj.service.ChatGptApiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
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
    public String chat(String question) {
        HttpRequest post = HttpUtil.createPost("https://api.openai.com/v1/chat/completions");
        post.header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getKey());
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
        post.header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getKey());
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
    public String chat(String chatId, String userId, String question, boolean hasContext) {
        if (!hasContext) {
            return this.chat(question);
        }
        if (!StringUtils.hasLength(chatId) || !StringUtils.hasLength(userId)) {
            return "Error: chatId and userId cannot be null or blank!";
        }
        List<Object> messageList;
        String key = userId + "_" + chatId;
        ChatGptChatDTO.Message message = new ChatGptChatDTO.Message();
        message.setRole("user");
        message.setContent(question);
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            messageList = new ArrayList<>();
            ChatGptChatDTO.Message systemMsg = new ChatGptChatDTO.Message();
            systemMsg.setRole("system");
            systemMsg.setContent(config.getBehave());
            messageList.add(JSON.toJSONString(systemMsg));
            redisTemplate.opsForList().rightPush(key, JSON.toJSONString(systemMsg));
            size = 1L;
        }
        ChatGptChatDTO.Message userMsg = new ChatGptChatDTO.Message();
        userMsg.setRole("user");
        userMsg.setContent(question);
        messageList = redisTemplate.opsForList().leftPop(key, size);
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
            while (true) {
                messageList.remove(0);
                messageSize = messageList.size();
                if (messageSize <= configSize) {
                    break;
                }
            }
        }
        redisTemplate.opsForList().rightPushAll(key, messageList);
        redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        return answer;
    }
}
