package michaelsoftbinbows.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/** Pruebas unitarias para GeocodingService. */
@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock private RestTemplate restTemplate;

    @InjectMocks private GeocodingService geocodingService;

    private static final String API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(geocodingService, "apiKey", API_KEY);
    }

    /** Test 1: Verifica que getCoordinatesByCity retorna las coordenadas correctamente. */
    @Test
    void testGetCoordinatesByCitySuccess() {
        String city = "Santiago";
        String expectedUrl =
            "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + API_KEY;
        String mockResponse = "[{\"lat\":-33.4489,\"lon\":-70.6693,\"name\":\"Santiago\"}]";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(responseEntity);

        String result = geocodingService.getCoordinatesByCity(city);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
    }

    /** Test 2: Verifica que getCityByCoordinates retorna el nombre de la ciudad correctamente. */
    @Test
    void testGetCityByCoordinatesSuccess() {
        double lat = -33.4489;
        double lon = -70.6693;
        String expectedUrl =
            "https://api.openweathermap.org/geo/1.0/reverse?lat="
                + lat
                + "&lon="
                + lon
                + "&limit=1&appid="
                + API_KEY;
        String mockResponse = "[{\"name\":\"Santiago\",\"country\":\"CL\"}]";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(expectedUrl, String.class)).thenReturn(responseEntity);

        String result = geocodingService.getCityByCoordinates(lat, lon);

        assertNotNull(result);
        assertEquals(mockResponse, result);
        verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
    }

    /** Test 3: Prueba la reacción del servicio a un error 404 (Ciudad No Encontrada). */
    @Test
    void testGetCoordinatesByCity_Throws404Error() {

        String city = "CiudadQueNoExiste";
        String expectedUrl =
                "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + API_KEY;

        when(restTemplate.getForEntity(expectedUrl, String.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(
                HttpClientErrorException.class,
                () -> {
                    geocodingService.getCoordinatesByCity(city);
                }
        );
        
        verify(restTemplate, times(1)).getForEntity(expectedUrl, String.class);
    }
}
