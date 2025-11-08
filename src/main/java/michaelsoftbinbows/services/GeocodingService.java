package michaelsoftbinbows.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Servicio para interactuar con la API de Geocoding de OpenWeatherMap. */
@Service
public class GeocodingService {

  private final RestTemplate restTemplate;
  private final String apiKey;

  /**
   * Constructor para inyección de dependencias. Spring Boot buscará un "Bean" de RestTemplate y el
   * valor "owm.api.key" y se los pasará a este servicio automáticamente.
   */
  public GeocodingService(RestTemplate restTemplate, @Value("${owm.api.key}") String apiKey) {
    this.restTemplate = restTemplate;
    this.apiKey = apiKey;
  }

  /** Obtiene las coordenadas (latitud y longitud) para una ciudad específica. */
  public String getCoordinatesByCity(String city) {
    String url =
        "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + apiKey;

    // Usamos el restTemplate que nos pasaron
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
  }

  /** Obtiene el nombre de la ciudad basado en coordenadas de latitud y longitud. */
  public String getCityByCoordinates(double lat, double lon) {
    String url =
        "https://api.openweathermap.org/geo/1.0/reverse?lat="
            + lat
            + "&lon="
            + lon
            + "&limit=1&appid="
            + apiKey;

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
  }
}
