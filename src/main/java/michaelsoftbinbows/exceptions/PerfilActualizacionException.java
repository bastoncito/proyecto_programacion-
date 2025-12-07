package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando ocurre un error durante la actualización de la información personal del
 * usuario en su perfil.
 */
public class PerfilActualizacionException extends Exception {
  private final String motivo; // Razón del error (ej. "correo duplicado")

  /**
   * Constructor con mensaje de error.
   *
   * @param message El mensaje de error a mostrar al usuario.
   * @param motivo La razón técnica del error.
   */
  public PerfilActualizacionException(String message, String motivo) {
    super(message);
    this.motivo = motivo;
  }

  /**
   * Constructor con mensaje de error solamente.
   *
   * @param message El mensaje de error a mostrar al usuario.
   */
  public PerfilActualizacionException(String message) {
    super(message);
    this.motivo = null;
  }

  public String getMotivo() {
    return motivo;
  }
}
