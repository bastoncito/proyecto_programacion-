package michaelsoftbinbows.controller;

import michaelsoftbinbows.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controlador para llamadas a la API referentes a Geocoding. */
@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

  @Autowired private GeocodingService geocodingService;

  /**
   * Muestra datos de la ciudad seleccionada.
   *
   * @param city nombre de la ciudad
   * @return datos de la ciudad
   */
  @GetMapping("/city")
  public String getCoordinates(@RequestParam String city) {
    return geocodingService.getCoordinatesByCity(city);
  }

  /**
   * El método anterior pero con una entrada diferente.
   *
   * @param lat latitud de ciudad
   * @param lon longitud de ciudad
   * @return datos de la ciudad
   */
  @GetMapping("/reverse")
  public String getCity(@RequestParam double lat, @RequestParam double lon) {
    return geocodingService.getCityByCoordinates(lat, lon);
  }
}
