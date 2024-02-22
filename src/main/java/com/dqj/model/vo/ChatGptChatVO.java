package com.dqj.model.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class ChatGptChatVO {

    private String id;
    /**
     * The object type, which is always chat.completion.
     */
    private String object;
    /**
     * The Unix timestamp (in seconds) of when the chat completion was created.
     */
    private long created;
    /**
     * The model used for the chat completion.
     */
    private String model;
    /**
     * This fingerprint represents the backend configuration that the model runs with.
     * Can be used in conjunction with the seed request parameter to understand
     * when backend changes have been made that might impact determinism.
     */
    @JSONField(name = "system_fingerprint")
    private String systemFingerprint;
    /**
     * A list of chat completion choices. Can be more than one if n is greater than 1.
     */
    private List<Choice> choices;
    /**
     * Usage statistics for the completion request.
     */
    private Usage usage;

    @Data
    public static class Choice {
        /**
         * The index of the choice in the list of choices.
         */
        private long index;
        /**
         * A chat completion message generated by the model.
         */
        private Message message;
        /**
         * Log probability information for the choice.
         */
        @JSONField(name = "logprobs")
        private List<LogProb> logProbList;
        /**
         * The reason the model stopped generating tokens.
         * This will be stop if the model hit a natural stop point or a provided stop sequence,
         * length if the maximum number of tokens specified in the request was reached,
         * content_filter if content was omitted due to a flag from our content filters,
         * tool_calls if the model called a tool, or function_call (deprecated) if the model called a function.
         */
        @JSONField(name = "finish_reason")
        private String finishReason;
    }

    @Data
    public static class Message {
        private String role;
        /**
         * A list of message content tokens with log probability information.
         */
        private String content;
    }

    @Data
    public static class Usage {
        /**
         * Number of tokens in the prompt.
         */
        @JSONField(name = "prompt_tokens")
        private long promptTokens;
        /**
         * Number of tokens in the generated completion.
         */
        @JSONField(name = "completion_tokens")
        private long completionTokens;
        /**
         * Total number of tokens used in the request (prompt + completion).
         */
        @JSONField(name = "total_tokens")
        private long totalTokens;
    }

    @Data
    public static class LogProb {
        /**
         * A list of message content tokens with log probability information.
         */
        @JSONField(name = "content")
        private List<LogProbContent> contentList;
    }

    @Data
    public static class LogProbContent {
        /**
         * The token.
         */
        private String token;
        /**
         * The log probability of this token.
         */
        @JSONField(name = "logprob")
        private long logProb;
        /**
         * A list of integers representing the UTF-8 bytes representation of the token.
         * Useful in instances where characters are represented by multiple tokens and their byte representations must
         * be combined to generate the correct text representation.
         * Can be null if there is no bytes representation for the token.
         */
        private byte[] bytes;
        /**
         * List of the most likely tokens and their log probability, at this token position.
         * In rare cases, there may be fewer than the number of requested top_logprobs returned.
         */
        @JSONField(name = "top_logprobs")
        private List<TopLogProb> topLogProbList;
    }

    @Data
    public static class TopLogProb{
        /**
         * The token.
         */
        private String token;
        /**
         * The log probability of this token.
         */
        private long logprob;
        /**
         * A list of integers representing the UTF-8 bytes representation of the token.
         * Useful in instances where characters are represented by multiple tokens and their byte representations
         * must be combined to generate the correct text representation.
         * Can be null if there is no bytes representation for the token.
         */
        private byte[] bytes;
    }
}