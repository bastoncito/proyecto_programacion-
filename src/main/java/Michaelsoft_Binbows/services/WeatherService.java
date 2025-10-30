package Michaelsoft_Binbows.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "c4d5108dcbf5ea5d68893c0e7f88b8c8"; //API Key de OpenWeatherMap

    public String getWeatherByCity(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                     "&appid=" + apiKey + "&units=metric&lang=es";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
}
