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

import java.util.List;

@Slf4j
public class ChatGptApiServiceImpl implements ChatGptApiService {

    @Resource
    private ChatGptConfig config;

    public ChatGptApiServiceImpl(ChatGptConfig config) {
        this.config = config;
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
}
