package michaelsoftbinbows.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import michaelsoftbinbows.exceptions.WeatherApiException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Servicio para interactuar con la API de OpenWeatherMap. */
@Service
public class WeatherService {

  private final RestTemplate restTemplate;
  private final String apiKey;

  /** Constructor para inyección de dependencias. */
  public WeatherService(RestTemplate restTemplate, @Value("${owm.api.key}") String apiKey) {
    this.restTemplate = restTemplate;
    this.apiKey = apiKey;
  }

  /** Obtiene la respuesta JSON cruda del clima para una ciudad. */
  public String getWeatherByCity(String city) {
    try {
      String url =
          "https://api.openweathermap.org/data/2.5/weather?q="
              + city
              + "&appid="
              + apiKey
              + "&units=metric&lang=es";

      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      return response.getBody();
    } catch (Exception e) {
      throw new WeatherApiException("No se pudo obtener el clima para la ciudad indicada.");
    }
  }

  /** Obtiene los datos del clima filtrados (temperatura, clima, hora) para una ciudad. */
  public String getFilteredWeatherByCity(String city) {
    try {
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

      // ARREGLO: Línea partida extrayendo el valor a una variable
      String climaDescripcion =
          json.getJSONArray("weather").getJSONObject(0).getString("description");
      result.put("clima", climaDescripcion);

      result.put("humedad", json.getJSONObject("main").getInt("humidity"));
      result.put("viento", json.getJSONObject("wind").getDouble("speed"));

      long timestamp = json.getLong("dt") + json.getInt("timezone");
      String horaActual =
          DateTimeFormatter.ofPattern("HH:mm:ss")
              .withZone(ZoneId.of("UTC"))
              .format(Instant.ofEpochSecond(timestamp));
      result.put("hora_actual", horaActual);

      return result.toString();
    } catch (Exception e) {
      throw new WeatherApiException(
          "No se pudo obtener el clima filtrado para la ciudad indicada.");
    }
  }
}
