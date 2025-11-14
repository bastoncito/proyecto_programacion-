package michaelsoftbinbows.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Locale;

/**
 * Entidad que representa un Logro (Achievement) que un usuario puede desbloquear. Contiene la
 * definición del logro y la recompensa de experiencia.
 */
@Entity
public class Logro {
  @Id private String id; // Usamos el ID como clave primaria
  private String nombre;
  private String descripcion;
  private int experienciaRecompensa;
  private boolean activo = true;
  private String imagenUrl;

  /**    * Constructor vacío requerido por JPA.     */
  public Logro() {
    this.id = "";
    this.nombre = "";
    this.descripcion = "";
    this.experienciaRecompensa = 0;
    this.activo = true;
    this.imagenUrl = null;
  }

  /**
   * Constructor de la clase Logro. Valida todos los parámetros antes de crear el objeto. Si alguna
   * validación falla, lanza una excepción para prevenir la creación de un objeto en estado
   * inválido.
   *
   * @param id El identificador único del logro (sin espacios, se convertirá a mayúsculas).
   * @param nombre El nombre visible para el usuario (ej. "¡Primeros Pasos!").
   * @param descripcion La explicación de cómo se obtiene el logro.
   * @param experienciaRecompensa La cantidad de EXP que otorga el logro (puede ser 0).
   */
  public Logro(String id, String nombre, String descripcion, int experienciaRecompensa) {

    // Validacion de ID: sin espacios y en mayúsculas (por convención).
    if (id == null || id.trim().isEmpty() || id.contains(" ")) {
      throw new IllegalArgumentException(
          "El ID del logro no puede ser nulo, vacío o contener espacios.");
    }

    // Validacion del Nombre: no puede estar vacío y debe tener una longitud razonable.
    if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 100) {
      throw new IllegalArgumentException(
          "El nombre del logro no puede estar vacío y debe tener 100 caracteres o menos.");
    }

    // Validacion de la Descripción: no puede estar vacía.
    if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > 500) {
      throw new IllegalArgumentException(
          "La descripción del logro no puede estar vacía y debe tener 500 caracteres o menos.");
    }

    // Validacion de la experienciaRecompensa: no puede ser negativa.
    if (experienciaRecompensa < 0) {
      throw new IllegalArgumentException("La recompensa en puntos no puede ser negativa.");
    }

    // Si el objeto no lanzo excepciones se crea
    this.id = id.trim().toUpperCase(Locale.getDefault());
    this.nombre = nombre.trim();
    this.descripcion = descripcion.trim();
    this.experienciaRecompensa = experienciaRecompensa;
    this.activo = true;
  }

  // GETTERS
  public String getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public int getPuntosRecompensa() {
    return experienciaRecompensa;
  }

  public boolean isActivo() { 
    return activo; 
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }
 
  public void setActivo(boolean activo) { 
    this.activo = activo; 
  }

  public String getImagenUrl() { return imagenUrl; }
  public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
  
  public void setExperienciaRecompensa(int experiencia){
    this.experienciaRecompensa = experiencia;
  }
}
