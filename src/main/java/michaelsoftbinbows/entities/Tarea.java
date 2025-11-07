package michaelsoftbinbows.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

  /**
   * Establece el nombre de la tarea, con validación.
   *
   * @param nombre El nombre a establecer.
   * @throws TareaInvalidaException Si el nombre no es válido.
   */
  public void setNombre(String nombre) throws TareaInvalidaException {
    if (nombre == null || nombre.isEmpty()) {
      throw new TareaInvalidaException(
          "El nombre de la tarea no puede estar vacío.", nombre, descripcion);
    }
    if (nombre.length() < 5 || nombre.length() > 30) {
      throw new TareaInvalidaException(
          "El nombre de la tarea debe tener entre 5 y 30 carácteres", nombre, descripcion);
    }
    this.nombre = nombre;
  }

  /**
   * Establece la descripción de la tarea, con validación.
   *
   * @param descripcion La descripción a establecer.
   * @throws TareaInvalidaException Si la descripción no es válida.
   */
  public void setDescripcion(String descripcion) throws TareaInvalidaException {
    if (descripcion == null || descripcion.isEmpty()) {
      throw new TareaInvalidaException(
          "La descripción de la tarea no puede estar vacía.", nombre, descripcion);
    }
    if (descripcion.length() < 5 || descripcion.length() > 70) {
      throw new TareaInvalidaException(
          "La descripción debe tener entre 5 y 80 carácteres", nombre, descripcion);
    }
    this.descripcion = descripcion;
  }

  /**
   * Establece la experiencia (EXP) de la tarea, con validación.
   *
   * @param exp La cantidad de EXP (debe ser > 0).
   * @throws TareaInvalidaException Si la EXP es 0 o negativa.
   */
  public void setExp(int exp) throws TareaInvalidaException {
    if (exp <= 0) {
      throw new TareaInvalidaException(
          "La exp de la tarea no puede ser menor a 1.", nombre, descripcion);
    }
    this.exp = exp;
  }

  /**
   * Establece la fecha de expiración de la tarea, con validación.
   *
   * @param fechaExpiracion La fecha de expiración (debe ser futura).
   * @throws TareaInvalidaException Si la fecha no es válida.
   */
  public void setFechaExpiracion(LocalDateTime fechaExpiracion) throws TareaInvalidaException {
    if (fechaExpiracion == null) {
      throw new TareaInvalidaException("La fecha no puede estar vacía.", nombre, descripcion);
    }
    LocalDateTime fechaActual = LocalDateTime.now(ZoneId.systemDefault());
    if (fechaExpiracion.isBefore(fechaActual)) {
      throw new TareaInvalidaException(
          "La fecha debe ser igual o posterior a hoy.", nombre, descripcion);
    }
    // revisa si la fecha está al menos 1 hora en el futuro
    long diferenciaMilisegundos = Duration.between(fechaActual, fechaExpiracion).toMillis();
    if (diferenciaMilisegundos < 60 * 60 * 1000) {
      throw new TareaInvalidaException(
          "La fecha debe ser al menos 1 hora posterior a la actual.", nombre, descripcion);
    }
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
