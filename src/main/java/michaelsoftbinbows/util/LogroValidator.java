package michaelsoftbinbows.util;

/** Valida los atributos de la entidad Logro. */
public class LogroValidator {
  /**
   * Valida que el ID del logro no sea nulo o vacío.
   *
   * @param id El ID a validar.
   * @return true si es válido, false en caso contrario.
   */
  public boolean idValida(String id) {
    if (id == null || id.trim().isEmpty() || id.contains(" ")) {
      // throw new IllegalArgumentException(
      // "El ID del logro no puede ser nulo, vacío o contener espacios.");
      return false;
    }
    return true;
  }

  /**
   * Valida que la experiencia de recompensa sea válida (no negativa).
   *
   * @param experienciaRecompensa El valor a validar.
   * @return true si es válido, false en caso contrario.
   */
  public boolean experienciaRecompensaValida(int experienciaRecompensa) {
    if (experienciaRecompensa < 0) {
      // throw new IllegalArgumentException("La recompensa en puntos no puede ser negativa.");
      return false;
    }
    return true;
  }

  /**
   * Valida que la descripción del logro sea válida.
   *
   * @param descripcion La descripción a validar.
   * @return true si es válida, false en caso contrario.
   */
  public boolean descripcionValida(String descripcion) {
    if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > 500) {
      // throw new IllegalArgumentException(
      // "La descripción del logro no puede estar vacía y debe tener 500 caracteres o menos.");
      return false;
    }
    return true;
  }

  /**
   * Valida que el nombre del logro sea válido.
   *
   * @param nombre El nombre a validar.
   * @return true si es válido, false en caso contrario.
   */
  public boolean nombreValido(String nombre) {
    if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 100) {
      // throw new IllegalArgumentException(
      // "El nombre del logro no puede estar vacío y debe tener 100 caracteres o menos.");
      return false;
    }
    return true;
  }
}
