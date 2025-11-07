package michaelsoftbinbows.controller;

import michaelsoftbinbows.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controlador para llamadas a la API referentes al clima. */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

  @Autowired private WeatherService weatherService;

  /**
   * Muestra datos del clima actual en la ciudad elegida.
   *
   * @param city ciudad para revisar
   * @return información del clima en la ciudad
   */
  @GetMapping
  public String getWeather(@RequestParam String city) {
    return weatherService.getWeatherByCity(city);
  }
}
