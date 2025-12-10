package michaelsoftbinbows.exceptions;

/** Excepción lanzada cuando un logro no es válido. */
public class LogroInvalidoException extends Exception {

  /**
   * Constructor de la excepción.
   *
   * @param message por qué el logro es inválido
   */
  public LogroInvalidoException(String message) {
    super(message);
  }
}
