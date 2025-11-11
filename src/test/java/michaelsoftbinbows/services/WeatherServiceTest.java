package michaelsoftbinbows.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import michaelsoftbinbows.exceptions.WeatherApiException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** Pruebas unitarias para WeatherService. */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private WeatherService weatherService;

  private static final String API_KEY = "test-api-key";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(weatherService, "apiKey", API_KEY);
  }

  /** Test 1: Verifica que getWeatherByCity retorna los datos del clima correctamente. */
  @Test
  void testGetWeatherByCitySuccess() {
    String city = "Santiago";
    String expectedUrl =
        "https://api.openweathermap.org/data/2.5/weather?q="
            + city
            + "&appid="
            + API_KEY
            + "&units=metric&lang=es";
    String mockResponse = "{\"main\":{\"temp\":25.5},\"weather\":[{\"description\":\"soleado\"}]}";

    ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(responseEntity);

    String result = weatherService.getWeatherByCity(city);

    assertNotNull(result);
    assertEquals(mockResponse, result);
    verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
  }

  /** Test 2: Verifica que getFilteredWeatherByCity PARSEA los datos filtrados correctamente. */
  @Test
  void testGetFilteredWeatherByCitySuccess() {
    // --- ARRANGE ---
    String city = "Valparaíso";
    String expectedUrl =
        "https://api.openweathermap.org/data/2.5/weather?q="
            + city
            + "&appid="
            + API_KEY
            + "&units=metric&lang=es";

    String mockResponse =
        """
                {
                    "main": { "temp": 18.5, "humidity": 75 },
                    "weather": [ { "description": "parcialmente nublado" } ],
                    "wind": { "speed": 5.2 },
                    "dt": 1699459200,
                    "timezone": -10800
                }
                """;

    ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
    when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(responseEntity);

    String result = weatherService.getFilteredWeatherByCity(city);

    assertNotNull(result);

    JSONObject resultJson = new JSONObject(result);

    assertEquals(18.5, resultJson.getDouble("temperatura"));
    assertEquals(75, resultJson.getInt("humedad"));
    assertEquals(5.2, resultJson.getDouble("viento"));
    assertEquals("parcialmente nublado", resultJson.getString("clima"));
    // El timestamp es (1699459200 + -10800) = 1699448400 -> "13:00:00" UTC
    assertEquals("13:00:00", resultJson.getString("hora_actual"));

    verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
  }

  /** Test 3: Verifica que se lanza excepción cuando la API falla. */
  @Test
  void testGetWeatherByCityThrowsException() {
    String city = "CiudadInexistente";
    String expectedUrl =
        "https://api.openweathermap.org/data/2.5/weather?q="
            + city
            + "&appid="
            + API_KEY
            + "&units=metric&lang=es";

    when(restTemplate.getForEntity(expectedUrl, String.class))
        .thenThrow(new RestClientException("API Error"));

    WeatherApiException exception =
        assertThrows(WeatherApiException.class, () -> weatherService.getWeatherByCity(city));

    assertEquals("No se pudo obtener el clima para la ciudad indicada.", exception.getMessage());
    verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
  }
}
