package Michaelsoft_Binbows.entities;

import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import Michaelsoft_Binbows.util.Dificultad;
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
import java.util.List;

@Entity
public class Tarea {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre, descripcion;
  private int exp;
  private LocalDateTime fechaExpiracion;
  private LocalDateTime fechaCompletada = null;
  private String climaCompatible;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  @JsonIgnore
  private Usuario usuario;

  // Constructores
  public Tarea() {
    // Este constructor se deja vacío a propósito.
    // Es requerido por algunos frameworks como Spring y Thymeleaf para poder
    // crear objetos de esta clase sin necesidad de pasarle argumentos.
  }

  public Tarea(String nombre, String descripcion, String dificultad) throws TareaInvalidaException {
    setNombre(nombre);
    setDescripcion(descripcion);
    // Se calcula la experiencia en base a una de 6 categorías de dificultad
    setExp(Dificultad.obtenerExpPorDificultad(dificultad));
    setFechaExpiracion(Dificultad.obtenerDíasPorDificultad(dificultad));
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

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public String getClimaCompatible() {
    return climaCompatible;
  }

  public void setClimaCompatible(String climaCompatible) {
    this.climaCompatible = climaCompatible;
  }

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

  public void setExp(int exp) throws TareaInvalidaException {
    if (exp <= 0) {
      throw new TareaInvalidaException(
          "La exp de la tarea no puede ser menor a 1.", nombre, descripcion);
    }
    this.exp = exp;
  }

  public void setFechaExpiracion(LocalDateTime fechaExpiracion) throws TareaInvalidaException {
    if (fechaExpiracion == null) {
      throw new TareaInvalidaException("La fecha no puede estar vacía.", nombre, descripcion);
    }
    LocalDateTime fechaActual = LocalDateTime.now();
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

  public void setFechaCompletada(LocalDateTime fechaCompletada) {
    this.fechaCompletada = fechaCompletada;
  }

  public boolean tareaExistePorNombre(List<Tarea> tareas, Tarea tarea) {
    for (Tarea t : tareas) {
      if (t.getNombre().equalsIgnoreCase(tarea.getNombre())) {
        return true;
      }
    }
    return false;
  }

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
