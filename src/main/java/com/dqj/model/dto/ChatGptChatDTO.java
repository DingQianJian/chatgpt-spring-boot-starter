package com.dqj.model.dto;

import com.dqj.config.ChatGptConfig;
import lombok.Data;

import java.util.List;

@Data
public class ChatGptChatDTO {

    /**
     * Required
     * A list of messages comprising the conversation so far.
     */
    private List<Message> messages;
    /**
     * Required
     * ID of the model to use. See the model endpoint compatibility table for details on which models work with the Chat API.
     * value:
     * - gpt-3.5-turbo-16k-0613
     * - gpt-3.5-turbo-16k
     * - gpt-3.5-turbo-0125
     * - gpt-3.5-turbo
     * - gpt-3.5-turbo-0301
     * - gpt-4-0613
     * - gpt-4-0125-preview
     * - gpt-3.5-turbo-0613
     * - gpt-4-turbo-preview
     * - gpt-4
     * - gpt-3.5-turbo-1106
     * - gpt-4-1106-preview
     * - gpt-4-vision-preview (just for image_url input, not support now)
     */
    private String model;

    public void setModel(ChatGptConfig.ModelEnum modelEnum) {
        this.model = modelEnum.getModel();
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }



}
