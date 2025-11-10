package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando el Usuario intenta crear una tarea inválida.
 *
 * <p>Guarda los datos de esta como contexto.
 */
public class TareaInvalidaException extends Exception {
  private String nombre;
  private String descripcion;

  /**
   * Constructor de la excepción.
   *
   * @param message por qué es inválida
   * @param nombre nombre de la tarea
   * @param descripcion descripción de la tarea
   */
  public TareaInvalidaException(String message, String nombre, String descripcion) {
    super(message);
    this.nombre = nombre;
    this.descripcion = descripcion;
  }

  public String getNombre() {
    return nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
