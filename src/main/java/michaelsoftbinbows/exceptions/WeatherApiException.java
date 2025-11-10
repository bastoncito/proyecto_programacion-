package michaelsoftbinbows.exceptions;

/** Excepción lanzada cuando hay un error al llamar a la API de OpenWeatherMap. */
public class WeatherApiException extends RuntimeException {
  /**
   * Constructor de excepción.
   *
   * @param message error en sí
   */
  public WeatherApiException(String message) {
    super(message);
  }
}
