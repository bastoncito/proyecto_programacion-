package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando ocurre un error al editar o completar una Tarea.
 *
 * <p>Utilizada por el GlobalExceptionManager.
 *
 * <p>Guarda el ID de la tarea como contexto.
 */
public class EdicionTareaException extends Exception {
  private Long tareaId;

  /**
   * Constructor de la excepción.
   *
   * @param message por qué no se puede editar la tarea
   * @param tareaId ID de la tarea que se intentó editar
   */
  public EdicionTareaException(String message, Long tareaId) {
    super(message);
    this.tareaId = tareaId;
  }

  public Long getTareaId() {
    return tareaId;
  }
}
