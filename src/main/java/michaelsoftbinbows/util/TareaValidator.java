package michaelsoftbinbows.util;

/** Maneja las validaciones para los atributos de Tarea (los que se deben ingresar) */
public class TareaValidator {
  // RECORDAR REPONER EL CONTEXTO DE EXCEPCIONES
  /**
   * Valida el nombre de la tarea.
   *
   * @param nombre El nombre a establecer.
   * @return String con mensaje de error o null
   */
  public String nombreTareaValido(String nombreTarea) {
    if (nombreTarea == null || nombreTarea.isEmpty()) {
      return "El nombre de la tarea no puede estar vacío.";
    }
    if (nombreTarea.length() < 5 || nombreTarea.length() > 30) {
      return "El nombre de la tarea debe tener entre 5 y 30 carácteres";
    }
    return null;
  }

  /**
   * Valida la descripción de la tarea.
   *
   * @param descripcion La descripción a establecer.
   * @return String con mensaje de error o null
   */
  public String descripcionTareaValida(String descripcion) {
    if (descripcion == null || descripcion.isEmpty()) {
      return "La descripción de la tarea no puede estar vacía.";
    }
    if (descripcion.length() < 5 || descripcion.length() > 70) {
      return "La descripción debe tener entre 5 y 80 carácteres";
    }
    return null;
  }

  /**
   * Valida la dificultad de la tarea.
   *
   * @param dificultad Dificultad a establecer
   * @return String con mensaje de error o null
   */
  public String dificultadValida(String dificultad) {
    if (Dificultad.obtenerIdPorDificultad(dificultad) == -1) {
      return "Dificultad inválida: " + dificultad;
    }
    return null;
  }
}
