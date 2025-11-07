package michaelsoftbinbows.exceptions;

import michaelsoftbinbows.entities.Usuario;

/**
 * Excepción lanzada cuando el Admin hace una edición de Tarea inválida. Utilizada por el
 * GlobalExceptionManager. Guarda 3 atributos como contexto para que el estado de la pantalla previo
 * al error se mantenga.
 */
public class AdminGuardarTareaException extends Exception {
  private Usuario usuario;
  private String nombreTarea;
  private String descripcionTarea;

  /**
   * Constructor de este tipo de excepción
   *
   * @param message por qué la tarea no se puede guardar
   * @param usuario usuario objetivo para la tarea
   * @param nombreTarea nombre de tarea inválida
   * @param descripcionTarea descripción de tarea inválida
   */
  public AdminGuardarTareaException(
      String message, Usuario usuario, String nombreTarea, String descripcionTarea) {
    super(message);
    this.usuario = usuario;
    this.nombreTarea = nombreTarea;
    this.descripcionTarea = descripcionTarea;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public String getNombreTarea() {
    return nombreTarea;
  }

  public String getDescripcionTarea() {
    return descripcionTarea;
  }
}
