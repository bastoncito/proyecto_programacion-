package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando el Admin intenta crear un Usuario inválido. Utilizada por el
 * GlobalExceptionManager.
 */
public class AdminCrearUsuarioException extends Exception {
  /**
   * Constructor de este tipo de excepción
   *
   * @param message el por qué el usuario no es válido
   */
  public AdminCrearUsuarioException(String message) {
    super(message);
  }
}
