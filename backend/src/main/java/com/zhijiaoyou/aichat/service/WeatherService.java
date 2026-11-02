package com.zhijiaoyou.aichat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 天气查询服务 — Open-Meteo 免费 API（无需 Key），供大模型通过 @Tool 调用。
 * 查询分两步：城市名 -> 经纬度 -> 当前天气。
 */
@Component
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    private static final String GEO_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Tool(description = "根据城市名查询实时天气")
    public String getWeather(@ToolParam(description = "城市名称，如：北京、济南") String city) {
        try {
            Map<String, Double> geo = geocode(city);
            if (geo == null || geo.isEmpty()) {
                return "没有找到「" + city + "」的坐标信息，请确认城市名是否正确。";
            }

            String url = WEATHER_URL + "?latitude=" + geo.get("lat")
                    + "&longitude=" + geo.get("lng")
                    + "&current=temperature_2m,rain,wind_speed_10m,weather_code"
                    + "&timezone=auto";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode current = mapper.readTree(response.body()).path("current");
            if (current.isMissingNode()) {
                return "暂时没有查询到「" + city + "」的天气数据。";
            }

            double temp = current.path("temperature_2m").asDouble();
            double rain = current.path("rain").asDouble();
            double wind = current.path("wind_speed_10m").asDouble();
            int code = current.path("weather_code").asInt();

            return city + "当前天气：" + weatherDesc(code)
                    + "，气温：" + temp + "℃，降雨量：" + rain + "mm，风速：" + wind + "km/h";
        } catch (Exception e) {
            log.warn("天气查询失败: city={}", city, e);
            return "天气服务暂时不可用，请稍后再试。";
        }
    }

    private Map<String, Double> geocode(String city) throws Exception {
        String url = GEO_URL + "?name=" + URLEncoder.encode(city, StandardCharsets.UTF_8)
                + "&count=1&language=zh&format=json&countryCode=CN";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode results = mapper.readTree(response.body()).path("results");
        if (!results.isArray() || results.isEmpty()) {
            return null;
        }
        JsonNode cityNode = results.get(0);
        Map<String, Double> geo = new LinkedHashMap<>();
        geo.put("lat", cityNode.path("latitude").asDouble());
        geo.put("lng", cityNode.path("longitude").asDouble());
        return geo;
    }

    private String weatherDesc(int code) {
        return switch (code) {
            case 0 -> "晴天";
            case 1, 2, 3 -> "多云";
            case 45, 48 -> "雾";
            case 51, 53, 55 -> "毛毛雨";
            case 61, 63, 65 -> "雨";
            case 66, 67 -> "冻雨";
            case 71, 73, 75, 77 -> "雪";
            case 80, 81, 82 -> "阵雨";
            case 85, 86 -> "阵雪";
            case 95, 96, 99 -> "雷暴";
            default -> "未知(" + code + ")";
        };
    }
}
