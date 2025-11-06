package michaelsoftbinbows.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import michaelsoftbinbows.exceptions.WeatherApiException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para interactuar con la API de OpenWeatherMap.
 * Proporciona métodos para obtener el clima por ciudad.
 */
@Service
public class WeatherService {

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Obtiene la respuesta JSON cruda del clima para una ciudad.
   *
   * @param city El nombre de la ciudad a consultar.
   * @return El JSON (como String) completo de la API.
   * @throws WeatherApiException Si no se puede obtener el clima.
   */
  public String getWeatherByCity(String city) {
    try {
      String url =
          "https://api.openweathermap.org/data/2.5/weather?q="
              + city
              + "&appid="
              + System.getProperty("OWM_API_KEY")
              + "&units=metric&lang=es";
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      return response.getBody();
    } catch (Exception e) {
      throw new WeatherApiException("No se pudo obtener el clima para la ciudad indicada.");
    }
  }

  /**
   * Obtiene los datos del clima filtrados (temperatura, clima, hora) para una ciudad.
   *
   * @param city El nombre de la ciudad a consultar.
   * @return Un JSON (como String) filtrado con los datos clave.
   * @throws WeatherApiException Si no se puede obtener o parsear el clima.
   */
  public String getFilteredWeatherByCity(String city) {
    try {
      String url =
          "https://api.openweathermap.org/data/2.5/weather?q="
              + city
              + "&appid="
              + System.getProperty("OWM_API_KEY")
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
    } catch (Exception e) {
      throw new WeatherApiException(
          "No se pudo obtener el clima filtrado para la ciudad indicada.");
    }
  }
}