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
   * Establece el nombre de la tarea.
   *
   * @param nombre El nuevo nombre.
   * @throws TareaInvalidaException Si el nombre es inválido.
   */
  public void setNombre(String nombre) throws TareaInvalidaException {
    this.nombre = nombre;
  }

  /**
   * Establece la descripción de la tarea.
   *
   * @param descripcion La nueva descripción.
   * @throws TareaInvalidaException Si la descripción es inválida.
   */
  public void setDescripcion(String descripcion) throws TareaInvalidaException {
    this.descripcion = descripcion;
  }

  /**
   * Establece la experiencia de la tarea.
   *
   * @param exp La nueva experiencia.
   * @throws TareaInvalidaException Si la experiencia es inválida.
   */
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

  /**
   * Calcula la fecha y hora a mitad del tiempo de vida de la tarea (punto medio entre su creación e
   * expiración).
   *
   * <p>La mitad del tiempo de vida se calcula como: fechaCreacion + (fechaExpiracion -
   * fechaCreacion) / 2
   *
   * @return Un LocalDateTime representando el punto medio de la vida de la tarea.
   */
  public LocalDateTime calcularMitadVida() {
    if (fechaExpiracion == null) {
      return LocalDateTime.now(ZoneId.systemDefault());
    }
    // Estimamos la fecha de creación como: fechaExpiracion - días de dificultad
    // Obtenemos el ID de dificultad basado en la experiencia
    int diasVida =
        switch (exp) {
          case 10 -> 1; // Muy fácil: 1 día
          case 25 -> 2; // Fácil: 2 días
          case 50 -> 3; // Medio: 3 días
          case 100 -> 4; // Difícil: 4 días
          case 150 -> 5; // Muy difícil: 5 días
          default -> 3; // Por defecto: 3 días (Medio)
        };
    // La fecha de creación es aproximadamente: fechaExpiracion - diasVida
    LocalDateTime fechaCreacion = fechaExpiracion.minusDays(diasVida);
    // Calculamos el punto medio
    long segundosVida =
        java.time.temporal.ChronoUnit.SECONDS.between(fechaCreacion, fechaExpiracion);
    long segundosMedio = segundosVida / 2;
    return fechaCreacion.plusSeconds(segundosMedio);
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
