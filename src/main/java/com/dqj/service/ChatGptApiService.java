package com.dqj.service;

import com.dqj.model.dto.ChatGptChatDTO;

import java.util.List;

public interface ChatGptApiService {
    String chat(String question);

    String chat(List<ChatGptChatDTO.Message> messageList);

    String chat(String chatId, String userId, String question, boolean hasContext);
}
