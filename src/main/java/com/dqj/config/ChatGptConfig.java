package com.dqj.config;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Slf4j
@ConfigurationProperties(prefix = "gpt.config")
public class ChatGptConfig {
    private ModelEnum model;
    private String key;
    /**
     * context size
     */
    private int size = 20;
    /**
     * expire time, minutes(default 60 min)
     */
    private int expire = 60;

    public void setModel(String model) {
        ModelEnum modelEnum = ModelEnum.getByModel(model);
        if (modelEnum == null) {
            log.warn("[gpt-auto-configuration]: there is not any adapted model named '{}' be found. " +
                    "Default model 'GPT_4' will be loaded.", model);
            modelEnum = ModelEnum.GPT_4;
        }
        this.model = modelEnum;
        log.info("[gpt-auto-configuration]: Model '{}' has been loaded.", this.model.getModel());
    }

    @Getter
    public enum ModelEnum {
        GPT_35_TRUBO_16K_0613("gpt-3.5-turbo-16k-0613"),
        GPT_35_TURBO_16K("gpt-3.5-turbo-16k"),
        GPT_35_TURBO("gpt-3.5-turbo"),
        GPT_35_TURBO_0125("gpt-3.5-turbo-0125"),
        GPT_35_TURBO_0301("gpt-3.5-turbo-0301"),
        GPT_35_TURBO_0613("gpt-3.5-turbo-0613"),
        GPT_35_TURBO_1106("gpt-35-1106"),
        GPT_4_0613("gpt-4-0613"),
        GPT_4_0125_PREVIEW("gpt-4-0125-preview"),
        GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"),
        GPT_4("gpt-4"),
        GPT_4_1106_PREVIEW("gpt-3.5-turbo-1106");

        ModelEnum(String model) {
            this.model = model;
        }

        private final String model;

        public static ModelEnum getByModel(String model) {
            for (ModelEnum modelEnum : ModelEnum.values()) {
                if (modelEnum.getModel().equals(model)) {
                    return modelEnum;
                }
            }
            return null;
        }
    }
}
