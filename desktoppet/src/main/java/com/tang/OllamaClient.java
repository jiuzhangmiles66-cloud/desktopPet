package com.tang;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class OllamaClient {

    private static final String URL =
            "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions";

    private static final String API_KEY = "YOUR_API_KEY";

    private static final String MODEL_NAME = "qwen-turbo";

    public static CompletableFuture<String> ask(String prompt) {
        String safePrompt = prompt.replace("\"", "\\\"").replace("\n", "\\n");
        String json = String.format(
                "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}",
                MODEL_NAME, safePrompt
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String body = response.body();

                    if (response.statusCode() != 200) {
                        System.out.println("Cloud API request failed. Details: " + body);
                        return "The AI service returned an error. Please check the console for details.";
                    }

                    try {
                        int start = body.indexOf("\"content\":\"") + 11;
                        int end = body.indexOf("\"", start);

                        while (end > 0 && body.charAt(end - 1) == '\\') {
                            end = body.indexOf("\"", end + 1);
                        }

                        return body.substring(start, end)
                                .replace("\\n", "\n")
                                .replace("\\\"", "\"");
                    } catch (Exception e) {
                        System.out.println("Failed to parse response. Raw response: " + body);
                        return "I couldn't read the AI response. Please try again.";
                    }
                });
    }
}