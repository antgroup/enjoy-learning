package com.knowledge.springboot.common.util;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 * @author xiangkun.lxk
 * @version ModelAPIUtil.java, v 0.1 2025/5/23 14:26 xiangkun.lxk
 */
public class ModelAPIUtil {

    public static void main(String[] args) {
        OpenAIClient client = OpenAIOkHttpClient.builder()
            .apiKey("")
            .baseUrl("").build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
            .addUserMessage("早安")
            .model("DeepSeek-R1")
            .build();
        ChatCompletion chatCompletion = client.chat().completions().create(params);
        System.out.println(chatCompletion.choices().get(0).message().content().get());
//        System.out.println(JSON.toJSONString(chatCompletion.choices()));
    }
}
