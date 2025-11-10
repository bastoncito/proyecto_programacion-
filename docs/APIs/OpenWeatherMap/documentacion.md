# API Externa: <br> OpenWeatherMap

## Utilidad

Esta API proporciona información climática según la ciudad que se desee, cosa esencial para una de las funciones de este proyecto: la recomendación de tareas según el clima actual.

## Información

La url base de la API es la sigiuente: https://api.openweathermap.org, desde la cual llamaremos a sus endpoints relevantes.

### Endpoints relevantes

- data/2.5/weather
  - Parámetros:
    - q (Ciudad y país en formato ISO, ej: Curico,CL)
    - appid (API key)
    - lang (idioma de respuesta, se utiliza "es")
    - units (unidades de medida, utilizamos "metric" para Celsius)
- geo/1.0/direct
  - Parámetros:
    - q (Ciudad y país en formato ISO, ej: Curico,CL)
    - limit (se deja en 1)
    - appid (API key)
- geo/1.0/reverse
  - Parámetros:
    - lat (latitud de ciudad)
    - lon (longitud de ciudad)
    - limit (se deja en 1)
    - appid (API key)

## Implementación

Se utiliza el plan gratuito de la API, con un límite de 1000 llamadas diarias y 60 por minuto.<br>
Para manejar las peticiones desde nuestra aplicación, se utilizan Controllers de Spring (WeatherController y GeocodingController).<br>
Se utilizan Services (WeatherService y GeocodingService) para manejar las llamadas a la url de la API. Estos poseen (o reciben) los parámetros necesarios para este fin, como lo sería la ciudad, API key, etc, y son llamados desde los Controllers de la página web para mostrar el clima en el apartado de "Home".

## Endpoints actuales

### GET

- api/geocoding/city<br>Va al endpoint de geo/1.0/direct
  - Parámetros:
    - city (Ciudad objetivo)
  - Retorno:
    - Información de la ciudad (nombre, país, estado/región, latitud, y longitud)
  - Ejemplo:
    - api/geocoding/city?city=Curico devuelve lo siguiente:

```text
[
    {
        "name": "Curicó",
        "local_names": {
            "es": "Curicó",
            "ru": "Курико",
            "uk": "Куріко"
        },
        "lat": -34.985368,
        "lon": -71.2393705,
        "country": "CL",
        "state": "Maule Region"
    }
]
```

- api/geocoding/reverse<br>Va al endpoint de geo/1.0/reverse
  - Parámetros:
    - lat & lon (latitud y longitud)
  - Retorno:
    - Información de la ciudad (nombre, país, estado/región, latitud, y longitud)
  - Ejemplo:
    - api/geocoding/reverse?lat=-34.9833&lon=-71.2393705 devuelve lo siguiente:

```text
[
    {
        "name": "Curicó",
        "local_names": {
            "uk": "Куріко",
            "es": "Curicó",
            "ru": "Курико"
        },
        "lat": -34.985368,
        "lon": -71.2393705,
        "country": "CL",
        "state": "Maule Region"
    }
]
```

- api/weather<br>Va al endpoint de data/2.5/weather
  - Parámetros:
    - city (Ciudad objetivo)
  - Retorno:
    - Información del clima actual en la ciudad (estado, temperatura, humedad, viento, etc.)
  - Ejemplo:
    - api/weather?city=Curico devuelve lo siguiente:

```text
{
    "coord": {
        "lon": -71.2333,
        "lat": -34.9833
    },
    "weather": [
        {
            "id": 800,
            "main": "Clear",
            "description": "cielo claro",
            "icon": "01d"
        }
    ],
    "base": "stations",
    "main": {
        "temp": 21.82,
        "feels_like": 21.52,
        "temp_min": 21.82,
        "temp_max": 21.82,
        "pressure": 1014,
        "humidity": 56,
        "sea_level": 1014,
        "grnd_level": 983
    },
    "visibility": 10000,
    "wind": {
        "speed": 2.24,
        "deg": 243,
        "gust": 2.28
    },
    "clouds": {
        "all": 7
    },
    "dt": 1761863986,
    "sys": {
        "country": "CL",
        "sunrise": 1761817461,
        "sunset": 1761865958
    },
    "timezone": -10800,
    "id": 3892870,
    "name": "Curicó",
    "cod": 200
}
```
