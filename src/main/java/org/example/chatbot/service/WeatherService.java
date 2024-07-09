package org.example.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {

    private static final String WEATHER_API_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.api.key}")
    private String apiKey;

    public String getWeather(String city) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(WEATHER_API_URL).newBuilder();
        urlBuilder.addQueryParameter("q", city);
        urlBuilder.addQueryParameter("appid", apiKey);
        urlBuilder.addQueryParameter("units", "metric");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                return parseWeatherResponse(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "I couldn't fetch the weather data.";
    }

    private String parseWeatherResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String description = root.path("weather").get(0).path("description").asText();
            double temp = root.path("main").path("temp").asDouble();
            return String.format("The weather is currently %s with a temperature of %.2f°C.", description, temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "I couldn't parse the weather data.";
    }
}
