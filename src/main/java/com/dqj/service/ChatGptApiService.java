package com.dqj.service;

import com.dqj.model.dto.ChatGptChatDTO;
import com.dqj.model.vo.ChatVO;

import java.util.List;

public interface ChatGptApiService {

    ChatVO newChat(String userId, String chatId, String systemMessage);

    void destroy(String userId, String chatId);

    String chat(String question);

    String chat(List<ChatGptChatDTO.Message> messageList);

    String chat( String userId, String chatId,String question, boolean hasContext);
}
