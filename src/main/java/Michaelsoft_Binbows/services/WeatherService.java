package Michaelsoft_Binbows.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

  private final RestTemplate restTemplate = new RestTemplate();
  private final String apiKey = "c4d5108dcbf5ea5d68893c0e7f88b8c8"; // API Key de OpenWeatherMap

  public String getWeatherByCity(String city) {
    String url =
        "https://api.openweathermap.org/data/2.5/weather?q="
            + city
            + "&appid="
            + apiKey
            + "&units=metric&lang=es";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
  }

  // Nuevo metodo para filtrar y mapear los datos principales
  public String getFilteredWeatherByCity(String city) {
    String url =
        "https://api.openweathermap.org/data/2.5/weather?q="
            + city
            + "&appid="
            + apiKey
            + "&units=metric&lang=es";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    JSONObject json = new JSONObject(response.getBody());

    JSONObject result = new JSONObject();
    result.put("temperatura", json.getJSONObject("main").getDouble("temp"));
    result.put("clima", json.getJSONArray("weather").getJSONObject(0).getString("description"));
    result.put("humedad", json.getJSONObject("main").getInt("humidity"));
    result.put("viento", json.getJSONObject("wind").getDouble("speed"));

    // Hora actual en la ciudad (usando el campo "dt" y "timezone")
    long timestamp = json.getLong("dt") + json.getInt("timezone");
    String horaActual =
        DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.of("UTC"))
            .format(Instant.ofEpochSecond(timestamp));
    result.put("hora_actual", horaActual);

    return result.toString();
  }
}
