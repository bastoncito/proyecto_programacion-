package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando se intenta completar una tarea antes de que haya transcurrido la mitad
 * de su tiempo de vida.
 */
public class TareaCompletadaPrematuramenteException extends Exception {
  private final String nombreTarea;
  private final Long tareaId;

  /**
   * Constructor con mensaje personalizado.
   *
   * @param mensaje El mensaje de error.
   * @param nombreTarea El nombre de la tarea.
   * @param tareaId El ID de la tarea.
   */
  public TareaCompletadaPrematuramenteException(String mensaje, String nombreTarea, Long tareaId) {
    super(mensaje);
    this.nombreTarea = nombreTarea;
    this.tareaId = tareaId;
  }

  public String getNombreTarea() {
    return nombreTarea;
  }

  public Long getTareaId() {
    return tareaId;
  }
}
