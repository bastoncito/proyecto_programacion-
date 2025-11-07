package michaelsoftbinbows.exceptions;

/** Excepción lanzada cuando se intenta registrar a un usuario inválido. */
public class RegistroInvalidoException extends Exception {
  /**
   * Constructor de excepción
   *
   * @param message por qué el usuario nuevo es inválido
   */
  public RegistroInvalidoException(String message) {
    super(message);
  }
}
