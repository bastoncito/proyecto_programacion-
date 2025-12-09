package michaelsoftbinbows.exceptions;

/** Excepción lanzada cuando ocurre un error durante el cambio de contraseña del usuario. */
public class CambioContrasenaException extends Exception {
  private final String motivo; // Razón del error (ej. "contraseña actual incorrecta")

  /**
   * Constructor con mensaje de error.
   *
   * @param message El mensaje de error a mostrar al usuario.
   * @param motivo La razón técnica del error.
   */
  public CambioContrasenaException(String message, String motivo) {
    super(message);
    this.motivo = motivo;
  }

  /**
   * Constructor con mensaje de error solamente.
   *
   * @param message El mensaje de error a mostrar al usuario.
   */
  public CambioContrasenaException(String message) {
    super(message);
    this.motivo = null;
  }

  public String getMotivo() {
    return motivo;
  }
}
