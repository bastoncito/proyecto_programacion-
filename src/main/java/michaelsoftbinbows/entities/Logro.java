package michaelsoftbinbows.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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

  @OneToMany(mappedBy = "logro", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UsuarioLogro> usuarioLogros = new ArrayList<>();

  /** * Constructor vacío requerido por JPA. */
  public Logro() {
    this.id = "";
    this.nombre = "";
    this.descripcion = "";
    this.experienciaRecompensa = 0;
    this.activo = true;
    this.imagenUrl = null;
  }

  /** Constructor de la clase Logro. */
  public Logro(String id, String nombre, String descripcion, int experienciaRecompensa) {

    if (id == null || id.trim().isEmpty() || id.contains(" ")) {
      throw new IllegalArgumentException(
          "El ID del logro no puede ser nulo, vacío o contener espacios.");
    }
    if (nombre == null || nombre.trim().isEmpty() || nombre.length() > 100) {
      throw new IllegalArgumentException(
          "El nombre del logro no puede estar vacío y debe tener 100 caracteres o menos.");
    }
    if (descripcion == null || descripcion.trim().isEmpty() || descripcion.length() > 500) {
      throw new IllegalArgumentException(
          "La descripción del logro no puede estar vacía y debe tener 500 caracteres o menos.");
    }
    if (experienciaRecompensa < 0) {
      throw new IllegalArgumentException("La recompensa en puntos no puede ser negativa.");
    }

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

  public List<UsuarioLogro> getUsuarioLogros() {
    return usuarioLogros;
  }

  // SETTERS
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public void setActivo(boolean activo) {
    this.activo = activo;
  }

  public String getImagenUrl() {
    return imagenUrl;
  }

  public void setImagenUrl(String imagenUrl) {
    this.imagenUrl = imagenUrl;
  }

  public void setExperienciaRecompensa(int experiencia) {
    this.experienciaRecompensa = experiencia;
  }

  public void setUsuarioLogros(List<UsuarioLogro> usuarioLogros) {
    this.usuarioLogros = usuarioLogros;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || !(o instanceof Logro)) return false;
    Logro logro = (Logro) o;
    return Objects.equals(id, logro.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
