package michaelsoftbinbows.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

  private final RestTemplate restTemplate = new RestTemplate();

  public String getCoordinatesByCity(String city) {
    String url =
        "https://api.openweathermap.org/geo/1.0/direct?q="
            + city
            + "&limit=1&appid="
            + System.getProperty("OWM_API_KEY");
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    return response.getBody();
  }

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
