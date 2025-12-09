package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando el Admin hace una edición de Usuario inválida.
 *
 * <p>Utilizada por el GlobalExceptionManager.
 *
 * <p>Guarda el correo del usuario como contexto.
 */
public class EdicionUsuarioException extends Exception {
  private String correo;

  /**
   * Constructor de la excepción.
   *
   * @param message por qué no se puede editar
   * @param correo correo del usuario editado
   */
  public EdicionUsuarioException(String message, String correo) {
    super(message);
    this.correo = correo;
  }

  public String getCorreo() {
    return correo;
  }
}
