package michaelsoftbinbows.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.util.Dificultad;

/**
 * Entidad que representa una Tarea. Contiene la lógica de negocio y las validaciones para una
 * tarea.
 */
@Entity
public class Tarea {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ARREGLO: Variables separadas en dos líneas
  private String nombre;
  private String descripcion;
  private int exp;
  private LocalDateTime fechaExpiracion;
  private LocalDateTime fechaCompletada = null;
  private String climaCompatible;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  @JsonIgnore
  private Usuario usuario;

  // Constructores
  /**
   * Constructor vacío requerido por JPA y otros frameworks. Es requerido por Spring y Thymeleaf
   * para poder instanciar objetos de esta clase sin argumentos.
   */
  public Tarea() {
    // Este constructor se deja vacío a propósito.
  }

  /**
   * Constructor para crear una nueva Tarea con validaciones.
   *
   * @param nombre El nombre de la tarea.
   * @param descripcion La descripción de la tarea.
   * @param dificultad El string de dificultad (ej. "Fácil").
   * @throws TareaInvalidaException Si alguno de los parámetros no es válido.
   */
  public Tarea(String nombre, String descripcion, String dificultad) throws TareaInvalidaException {
    setNombre(nombre);
    setDescripcion(descripcion);
    // Se calcula la experiencia en base a una de 6 categorías de dificultad
    setExp(Dificultad.obtenerExpPorDificultad(dificultad));
    setFechaExpiracion(Dificultad.obtenerDiasPorDificultad(dificultad));
  }

  public String getNombre() {
    return nombre;
  }

  public LocalDateTime getFechaExpiracion() {
    return fechaExpiracion;
  }

  public int getExp() {
    return exp;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public LocalDateTime getFechaCompletada() {
    return fechaCompletada;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public Long getId() {
    return id;
  }

  /**
   * Asigna el usuario propietario de esta tarea (relación bidireccional).
   *
   * @param usuario El usuario propietario.
   */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getClimaCompatible() {
    return climaCompatible;
  }

  /**
   * Define el clima con el que esta tarea es compatible (para recomendaciones).
   *
   * @param climaCompatible El nombre del clima (ej. "Soleado").
   */
  public void setClimaCompatible(String climaCompatible) {
    this.climaCompatible = climaCompatible;
  }

  public void setNombre(String nombre) throws TareaInvalidaException {
    this.nombre = nombre;
  }

  public void setDescripcion(String descripcion) throws TareaInvalidaException {
    this.descripcion = descripcion;
  }

  public void setExp(int exp) throws TareaInvalidaException {
    this.exp = exp;
  }

  public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
    this.fechaExpiracion = fechaExpiracion;
  }

  /**
   * Establece la fecha en que se completó la tarea.
   *
   * @param fechaCompletada La fecha y hora de completitud (o null si no está completada).
   */
  public void setFechaCompletada(LocalDateTime fechaCompletada) {
    this.fechaCompletada = fechaCompletada;
  }

  /**
   * Comprueba si una tarea ya existe en una lista, basándose en el nombre (ignorando mayúsculas).
   *
   * @param tareas La lista de tareas donde buscar.
   * @param tarea La tarea a comprobar.
   * @return true si ya existe, false si no.
   */
  public boolean tareaExistePorNombre(List<Tarea> tareas, Tarea tarea) {
    for (Tarea t : tareas) {
      if (t.getNombre().equalsIgnoreCase(tarea.getNombre())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Comprueba si una tarea ya existe en una lista, basándose en la descripción (ignorando
   * mayúsculas).
   *
   * @param tareas La lista de tareas donde buscar.
   * @param tarea La tarea a comprobar.
   * @return true si ya existe, false si no.
   */
  public boolean tareaExistePorDescripcion(List<Tarea> tareas, Tarea tarea) {
    for (Tarea t : tareas) {
      if (t.getDescripcion().equalsIgnoreCase(tarea.getDescripcion())) {
        return true;
      }
    }
    return false;
  }

  @Column(name = "es_semanal")
  private Boolean esSemanal = false;

  public Boolean getEsSemanal() {
    return esSemanal;
  }

  public void setEsSemanal(Boolean esSemanal) {
    this.esSemanal = esSemanal;
  }

  public boolean isCompletada() {
    return fechaCompletada != null;
  }

  @Override
  public String toString() {
    return "La tarea '"
        + nombre
        + "' ("
        + descripcion
        + ") que otorga "
        + exp
        + " puntos de experiencia, vence el "
        + fechaExpiracion;
  }
}
