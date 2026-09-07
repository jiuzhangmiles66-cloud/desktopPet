package com.tang;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class OllamaClient {

    // 1. 阿里云百炼的云端 API 地址 (使用通用的 OpenAI 兼容格式)
   private static final String URL = "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions";
    
    // 2. 将下面引号里的内容，替换成你刚刚申请到的 API Key
    private static final String API_KEY = "sk-ac167efab9be45ac96a5680bf4bdcd92"; 
    
    // 3. 使用千问极速且免费的云端模型
    private static final String MODEL_NAME = "qwen-turbo"; 

    public static CompletableFuture<String> ask(String prompt) {
        // 构造云端大模型需要的 JSON 格式
        String safePrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String json = String.format(
            "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
            MODEL_NAME, safePrompt
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY) // 这是最关键的一步：带着你的钥匙去敲云端的门
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String body = response.body();

                    // 如果网络请求失败或 Key 不对，直接在控制台报错
                    if (response.statusCode() != 200) {
                        System.out.println("云端连接失败，错误详情: " + body);
                        return "连接云端大脑失败，请检查一下控制台";
                    }

                    // 从云端复杂的 JSON 返回包里，精准切出 AI 说的内容
                    try {
                        int start = body.indexOf("\"content\":\"") + 11;
                        int end = body.indexOf("\"", start);
                        
                        // 防止 AI 回复里带有双引号导致提前被切断
                        while (end > 0 && body.charAt(end - 1) == '\\') {
                            end = body.indexOf("\"", end + 1);
                        }
                        
                        // 清理转义符，还原成人话
                        return body.substring(start, end)
                                   .replace("\\n", "\n")
                                   .replace("\\\"", "\"");
                    } catch (Exception e) {
                        System.out.println("解析失败，云端返回原文: " + body);
                        return "喵？云端信号出现了一点干扰...";
                    }
                });
    }
}