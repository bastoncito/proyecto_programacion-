package michaelsoftbinbows.controller;

import michaelsoftbinbows.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

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
  public String getCoordinates(@RequestParam("city") String city) {
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
    public String getCity(@RequestParam("lat") double lat, @RequestParam("lon") double lon) {
    return geocodingService.getCityByCoordinates(lat, lon);
  }

  /**
   * Endpoint para validar si una ciudad existe.
   *
   * @param city El nombre de la ciudad a validar.
   * @return Un JSON con la forma {"valid": true} o {"valid": false}.
   */
  @GetMapping("/validate")
   public ResponseEntity<Map<String, Boolean>> validateCity(@RequestParam("city") String city) {
    boolean isValid = geocodingService.isValidCity(city);
    return ResponseEntity.ok(Map.of("valid", isValid));
  }
}
