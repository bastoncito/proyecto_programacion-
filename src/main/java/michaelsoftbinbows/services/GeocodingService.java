package michaelsoftbinbows.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para interactuar con la API de Geocoding de OpenWeatherMap. Permite obtener coordenadas
 * a partir de una ciudad y viceversa.
 */
@Service
public class GeocodingService {

  private final RestTemplate restTemplate = new RestTemplate();

  /**
   * Obtiene las coordenadas (latitud y longitud) para una ciudad específica.
   *
   * @param city El nombre de la ciudad a buscar.
   * @return Un JSON (como String) con la información de geocodificación.
   */
  public String getCoordinatesByCity(String city) {
    String url =
        "https://api.openweathermap.org/geo/1.0/direct?q="
            + city
            + "&limit=1&appid="
            + System.getProperty("OWM_API_KEY");
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
  }

  /**
   * Obtiene el nombre de la ciudad basado en coordenadas de latitud y longitud.
   *
   * @param lat La latitud.
   * @param lon La longitud.
   * @return Un JSON (como String) con la información de la ciudad.
   */
  public String getCityByCoordinates(double lat, double lon) {
    String url =
        "https://api.openweathermap.org/geo/1.0/reverse?lat="
            + lat
            + "&lon="
            + lon
            + "&limit=1&appid="
            + System.getProperty("OWM_API_KEY");
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
  }
}
