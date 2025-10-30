package Michaelsoft_Binbows.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "c4d5108dcbf5ea5d68893c0e7f88b8c8"; //API Key de OpenWeatherMap

    public String getCoordinatesByCity(String city) {
        String url = "https://api.openweathermap.org/geo/1.0/direct?q=" + city +
                     "&limit=1&appid=" + apiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    public String getCityByCoordinates(double lat, double lon) {
        String url = "https://api.openweathermap.org/geo/1.0/reverse?lat=" + lat +
                     "&lon=" + lon + "&limit=1&appid=" + apiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
}