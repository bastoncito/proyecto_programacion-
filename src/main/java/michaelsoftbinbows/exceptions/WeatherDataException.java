package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando ocurre un error al procesar datos del clima o al integrar
 * características opcionales del sistema.
 *
 * <p>Ejemplos: - Error al parsear datos JSON del API del clima - Problema al crear tareas
 * recomendadas automáticamente
 */
public class WeatherDataException extends RuntimeException {
  private final String detalles;

  /**
   * Crea una nueva instancia de WeatherDataException.
   *
   * @param message Mensaje descriptivo del error
   * @param detalles Información adicional sobre el error
   */
  public WeatherDataException(String message, String detalles) {
    super(message);
    this.detalles = detalles;
  }

  /**
   * Crea una nueva instancia de WeatherDataException.
   *
   * @param message Mensaje descriptivo del error
   */
  public WeatherDataException(String message) {
    this(message, null);
  }

  public String getDetalles() {
    return detalles;
  }
}
