package michaelsoftbinbows.util;

public class LogroValidator {
  public boolean idValida(String id) {
    if (id == null || id.trim().isEmpty() || id.contains(" ")) {
      // throw new IllegalArgumentException(
      // "El ID del logro no puede ser nulo, vacío o contener espacios.");
      return false;
    }
    return true;
  }

  public boolean experienciaRecompensaValida(int experienciaRecompensa) {
    if (experienciaRecompensa < 0) {
      // throw new IllegalArgumentException("La recompensa en puntos no puede ser negativa.");
      return false;
    }
    return true;
  }

  public boolean descripcionValida(String descripcion) {
    if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > 500) {
      // throw new IllegalArgumentException(
      // "La descripción del logro no puede estar vacía y debe tener 500 caracteres o menos.");
      return false;
    }
    return true;
  }

  public boolean nombreValido(String nombre) {
    if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 100) {
      // throw new IllegalArgumentException(
      // "El nombre del logro no puede estar vacío y debe tener 100 caracteres o menos.");
      return false;
    }
    return true;
  }
}
