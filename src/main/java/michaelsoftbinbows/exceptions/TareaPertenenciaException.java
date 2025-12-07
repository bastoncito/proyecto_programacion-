package michaelsoftbinbows.exceptions;

/**
 * Excepción lanzada cuando se intenta operar sobre una tarea que no pertenece al usuario, o cuando
 * la tarea no se encuentra en el sistema.
 *
 * <p>Ejemplos: - Intentar completar una tarea que pertenece a otro usuario - Intentar eliminar una
 * tarea que no existe - Intentar editar una tarea de un usuario diferente
 */
public class TareaPertenenciaException extends RuntimeException {
  private final Long usuarioId;
  private final String nombreTarea;

  /**
   * Crea una nueva instancia de TareaPertenenciaException.
   *
   * @param message Mensaje descriptivo del error
   * @param usuarioId ID del usuario involucrado en la operación
   * @param nombreTarea Nombre de la tarea (puede ser null)
   */
  public TareaPertenenciaException(String message, Long usuarioId, String nombreTarea) {
    super(message);
    this.usuarioId = usuarioId;
    this.nombreTarea = nombreTarea;
  }

  /**
   * Crea una nueva instancia de TareaPertenenciaException con solo ID de usuario.
   *
   * @param message Mensaje descriptivo del error
   * @param usuarioId ID del usuario involucrado en la operación
   */
  public TareaPertenenciaException(String message, Long usuarioId) {
    this(message, usuarioId, null);
  }

  public Long getUsuarioId() {
    return usuarioId;
  }

  public String getNombreTarea() {
    return nombreTarea;
  }
}
