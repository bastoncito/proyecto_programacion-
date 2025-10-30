package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public String getWeather(@RequestParam String city) {
        return weatherService.getWeatherByCity(city);
    }
}
