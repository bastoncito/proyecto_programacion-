package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando el Admin intenta crear una Tarea inválida. Utilizada por el
 * GlobalExceptionManager.
 */
public class AdminCrearTareaException extends Exception {
  /**
   * Constructor de este tipo de excepción
   *
   * @param message el por qué la tarea no es válida
   */
  public AdminCrearTareaException(String message) {
    super(message);
  }
}
