package org.example.chatbot.service;

import org.example.chatbot.model.ChatRequest;
import org.example.chatbot.model.ChatResponse;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatbotService {

    @Autowired
    private WeatherService weatherService;

    private static final String API_KEY = "1888b231e8cf712ed8c8809887434fe5";
    private static final String API_URL = "https://api.openai.com/v1/engines/davinci-codex/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatResponse getChatResponse(ChatRequest chatRequest) {
        String userMessage = chatRequest.getMessage().toLowerCase();

        String responseMessage;
        if (userMessage.contains("hello") || userMessage.contains("hi")) {
            responseMessage = "Hello! How can I assist you today?";
        } else if (userMessage.contains("bye") || userMessage.contains("goodbye")) {
            responseMessage = "Goodbye! Have a great day!";
        } else if (userMessage.contains("weather")) {
            responseMessage = getInformationalResponse(userMessage);
        } else {
            responseMessage = getGeneralResponse(userMessage);
        }

        return new ChatResponse(responseMessage);
    }

    private String getInformationalResponse(String userMessage) {
        if (userMessage.contains("weather")) {
            String city = extractCity(userMessage);
            if (city != null) {
                return weatherService.getWeather(city);
            } else {
                return "Please specify a city to get the weather information.";
            }
        }
        return "I'm not sure about that.";
    }

    private String getGeneralResponse(String userMessage) {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{ \"prompt\": \"" + userMessage + "\", \"max_tokens\": 150 }"
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return objectMapper.readTree(responseBody).get("choices").get(0).get("text").asText().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "I'm sorry, I couldn't process that.";
    }

    private String extractCity(String userMessage) {
        String[] words = userMessage.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("in")) {
                if (i + 1 < words.length) {
                    return words[i + 1];
                }
            }
        }
        return null;
    }
}
