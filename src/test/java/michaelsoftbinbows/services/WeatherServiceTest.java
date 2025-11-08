package michaelsoftbinbows.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import michaelsoftbinbows.exceptions.WeatherApiException;
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

    /** Test 2: Verifica que getFilteredWeatherByCity retorna los datos filtrados correctamente. */
    @Test
    void testGetFilteredWeatherByCitySuccess() {
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
                    "main": {
                        "temp": 18.5,
                        "humidity": 75
                    },
                    "weather": [
                        {
                            "description": "parcialmente nublado"
                        }
                    ],
                    "wind": {
                        "speed": 5.2
                    },
                    "dt": 1699459200,
                    "timezone": -10800
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(responseEntity);

        String result = weatherService.getFilteredWeatherByCity(city);

        assertNotNull(result);
        assertTrue(result.contains("temperatura"));
        assertTrue(result.contains("clima"));
        assertTrue(result.contains("humedad"));
        assertTrue(result.contains("viento"));
        assertTrue(result.contains("hora_actual"));
        assertTrue(result.contains("18.5"));
        assertTrue(result.contains("75"));
        assertTrue(result.contains("5.2"));
        assertTrue(result.contains("parcialmente nublado"));

        verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
    }

    /** Test 3: Verifica que funciona con diferentes ciudades. */
    @Test
    void testGetWeatherByCityWithDifferentCity() {

        String city = "Concepción";

        String expectedUrl =
            "https://api.openweathermap.org/data/2.5/weather?q="
                + city
                + "&appid="
                + API_KEY
                + "&units=metric&lang=es";

        String mockResponse =
            """
                {
                    "main": {
                        "temp": 15.0,
                        "humidity": 80
                    },
                    "weather": [
                        {
                            "description": "lluvia ligera"
                        }
                    ],
                    "wind": {
                        "speed": 8.5
                    },
                    "dt": 1699459200,
                    "timezone": -10800
                }
                """;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(responseEntity);
        
        String result = weatherService.getFilteredWeatherByCity(city);
        
        assertNotNull(result);
        assertTrue(result.contains("temperatura"));
        assertTrue(result.contains("15.0"));
        assertTrue(result.contains("80"));
        assertTrue(result.contains("lluvia ligera"));
        assertTrue(result.contains("8.5"));

        verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
    }

    /** Test 4: Verifica que se lanza excepción cuando la API falla. */
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
