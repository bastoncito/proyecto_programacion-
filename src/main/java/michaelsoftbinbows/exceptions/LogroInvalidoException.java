package michaelsoftbinbows.exceptions;

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
