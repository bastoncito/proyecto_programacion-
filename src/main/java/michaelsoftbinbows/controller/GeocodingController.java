package michaelsoftbinbows.controller;

import michaelsoftbinbows.services.GeocodingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

  @Autowired private GeocodingService geocodingService;

  @GetMapping("/city")
  public String getCoordinates(@RequestParam String city) {
    return geocodingService.getCoordinatesByCity(city);
  }

  @GetMapping("/reverse")
  public String getCity(@RequestParam double lat, @RequestParam double lon) {
    return geocodingService.getCityByCoordinates(lat, lon);
  }
}
