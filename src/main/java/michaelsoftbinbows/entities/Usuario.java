package michaelsoftbinbows.entities;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.model.Rol;
import michaelsoftbinbows.util.SistemaNiveles;

/**
 * Entidad que representa a un Usuario en el sistema. Almacena información de perfil, estado de
 * juego (EXP, nivel) y sus tareas.
 */
@Entity
public class Usuario {

  @NonNull
  @Column(nullable = false, unique = true)
  private String nombreUsuario;

  @NonNull
  @Column(nullable = false)
  private String contrasena;

  @NonNull
  @Column(nullable = false, unique = true)
  private String correoElectronico;

  // ARREGLO: Variables separadas en múltiples líneas
  private int experiencia;
  private int nivelExperiencia;
  private int racha;
  private Rol rol;
  private LocalDateTime fechaRegistro;
  private LocalDate fechaRacha;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int puntosLiga;

  @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Bronce'")
  private String liga;

  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int puntosMesPasado;

  // CAMPO A TRABAJAR EN FUTURO
  private String avatarUrl;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(
      mappedBy = "usuario",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Tarea> tareas = new ArrayList<>();

  @OneToMany(
      mappedBy = "usuario",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private List<UsuarioLogro> usuarioLogros = new ArrayList<>();

  private String ciudad;

  /** Constructor vacío requerido por JPA. */
  public Usuario() {
    // Constructor vacío
  }

  /** Constructor principal para crear un nuevo Usuario. */
  public Usuario(String nombreUsuario, String correoElectronico, String contrasena)
      throws RegistroInvalidoException {
    setNombreUsuario(nombreUsuario);
    setCorreoElectronico(correoElectronico);
    setContrasena(contrasena);
    this.tareas = new ArrayList<>();
    this.usuarioLogros = new ArrayList<>();
    this.experiencia = 0;
    this.nivelExperiencia = 1;
    this.racha = 0;
    this.puntosLiga = 0;
    this.liga = "Bronce";
    this.puntosMesPasado = 0;
    this.rol = Rol.USUARIO;
    this.fechaRegistro = LocalDateTime.now(ZoneId.systemDefault());
    this.fechaRacha = null;
  }

  /** Getters/Setters. */
  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public String getCorreoElectronico() {
    return correoElectronico;
  }

  public String getContrasena() {
    return contrasena;
  }

  public List<Tarea> getTareas() {
    return tareas;
  }

  public int getNivelExperiencia() {
    return nivelExperiencia;
  }

  public int getExperiencia() {
    return experiencia;
  }

  public LocalDateTime getFechaRegistro() {
    return fechaRegistro;
  }

  public Rol getRol() {
    return rol;
  }

  public int getRacha() {
    return racha;
  }

  public LocalDate getFechaRacha() {
    return fechaRacha;
  }

  public Long getId() {
    return id;
  }

  public int getPuntosLiga() {
    return puntosLiga;
  }

  public String getLiga() {
    return liga;
  }

  public int getPuntosMesPasado() {
    return puntosMesPasado;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setRol(Rol rol) {
    this.rol = rol;
  }

  public void setPuntosLiga(int puntosLiga) {
    this.puntosLiga = puntosLiga;
  }

  public void setLiga(String liga) {
    this.liga = liga;
  }

  public void setExperiencia(int experiencia) {
    this.experiencia = experiencia;
  }

  public void setNivelExperiencia(int nivelExperiencia) {
    this.nivelExperiencia = nivelExperiencia;
  }

  public void setRacha(int racha) {
    this.racha = racha;
  }

  public void setFechaRegistro(LocalDateTime fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }

  public void setFechaRacha(LocalDate fechaRacha) {
    this.fechaRacha = fechaRacha;
  }

  public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public void setCorreoElectronico(String correoElectronico) {
    this.correoElectronico = correoElectronico;
  }

  public void setContrasena(String contrasena) {
    this.contrasena = contrasena;
  }

  public String getCiudad() {
    return ciudad;
  }

  public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
  }

  public void setPuntosMesPasado(int puntosMesPasado) {
    this.puntosMesPasado = puntosMesPasado;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  /** Resetea los puntos de liga del usuario al inicio de una nueva temporada. */
  public void resetearPuntosLiga() {
    this.puntosMesPasado = this.puntosLiga;
    this.puntosLiga = 0;
    this.liga = "Bronce";
  }

  /**
   * Devuelve la lista de asociaciones UsuarioLogro (que incluye la fecha de completado).
   *
   * @return Una lista de objetos UsuarioLogro.
   */
  public List<UsuarioLogro> getUsuarioLogros() {
    return this.usuarioLogros;
  }

  /**
   * Devuelve una lista simple de los Logros que el usuario ha desbloqueado. Este es un método
   * "helper" que "desenvuelve" la lista de UsuarioLogro. Es útil para comprobaciones como
   * `getLogros().contains(logro)`.
   *
   * @return Una lista de objetos Logro.
   */
  @Transient
  public List<Logro> getLogros() {
    if (this.usuarioLogros == null) {
      return new ArrayList<>();
    }
    // Transforma la lista de "UsuarioLogro" en una lista de "Logro"
    return this.usuarioLogros.stream().map(UsuarioLogro::getLogro).collect(Collectors.toList());
  }

  /**
   * Devuelve solo las tareas PENDIENTES (no completadas). La tarea es PENDIENTE si
   * getFechaCompletada() devuelve null.
   *
   * @return Lista de tareas pendientes.
   */
  public List<Tarea> getTareasPendientes() {
    return tareas.stream()
        // Si la fecha de completado es null, la tarea está PENDIENTE
        .filter(t -> t.getFechaCompletada() == null)
        .collect(Collectors.toList());
  }

  /**
   * Devuelve solo las tareas COMPLETADAS ordenadas por fecha de completado (más recientes primero).
   * La tarea es COMPLETADA si getFechaCompletada() NO es null.
   *
   * @return Lista de tareas completadas para el historial.
   */
  public List<Tarea> getTareasCompletadas() {
    return tareas.stream()
        // Si la fecha de completado no es null, la tarea está COMPLETADA
        .filter(t -> t.getFechaCompletada() != null)
        // Las tareas completadas ya no serán null en este punto,
        // pero mantenemos el comparador por si acaso, aunque Comparator.nullsLast ya no sería
        // estrictamente necesario.
        .sorted(
            Comparator.comparing(
                Tarea::getFechaCompletada, Comparator.nullsLast(Comparator.reverseOrder())))
        .collect(Collectors.toList());
  }

  /**
   * Devuelve el número de tareas completadas. La tarea es COMPLETADA si getFechaCompletada() NO es
   * null.
   *
   * @return Cantidad de tareas completadas.
   */
  public int getNumeroCompletadas() {
    return (int)
        tareas.stream()
            // Contamos solo si la fecha de completado no es null
            .filter(t -> t.getFechaCompletada() != null)
            .count();
  }

  /**
   * Busca una tarea PENDIENTE por su nombre. Una tarea es PENDIENTE si getFechaCompletada()
   * devuelve null.
   *
   * @param nombre Nombre de la tarea a buscar.
   * @return La tarea encontrada o null si no existe.
   */
  public Tarea buscarTareaPorNombre(String nombre) {
    for (Tarea t : getTareasPendientes()) {
      if (t.getNombre().equalsIgnoreCase(nombre)) {
        return t;
      }
    }
    for (Tarea t : getTareasCompletadas()) {
      if (t.getNombre().equalsIgnoreCase(nombre)) {
        return t;
      }
    }
    return null;
  }

  /**
   * Calcula la experiencia TOTAL ganada sumando todas las tareas completadas. Una tarea está
   * COMPLETADA si getFechaCompletada() NO es null.
   *
   * @return La suma de experiencia de todas las tareas completadas.
   */
  public int getExperienciaTotal() {
    return tareas.stream()
        // Nueva condición: el filtro busca tareas donde la fecha de completado NO sea null.
        .filter(t -> t.getFechaCompletada() != null)
        .mapToInt(Tarea::getExp)
        .sum();
  }

  @Override
  public String toString() {
    // Calculamos la experiencia para el proximo nivel para mostrarla (ya que el usuario no la
    // guarda)
    int expParaSiguienteNivel = SistemaNiveles.experienciaParaNivel(this.nivelExperiencia + 1);

    return String.format(
        "<<< Usuario: %s >>>\n"
            + "Rol: %s\n"
            + "Nivel: %d\n"
            + "Experiencia: %d / %d\n"
            + "Correo: %s\n"
            + "Tareas Pendientes: %d\n"
            + "Tareas Completadas: %d",
        this.nombreUsuario,
        this.rol,
        this.nivelExperiencia,
        this.experiencia,
        expParaSiguienteNivel,
        this.correoElectronico,
        getTareasPendientes().size(),
        getNumeroCompletadas());
  }
}
