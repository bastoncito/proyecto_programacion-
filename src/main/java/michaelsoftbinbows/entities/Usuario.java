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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(
      mappedBy = "usuario",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Tarea> tareas = new ArrayList<>();

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "usuario_logros", // Nombre de la nueva tabla intermedia
      joinColumns = @JoinColumn(name = "usuario_id"),
      inverseJoinColumns = @JoinColumn(name = "logro_id"))
  private List<Logro> logros = new ArrayList<>();

  private String ciudad;

  /** Constructor vacío requerido por JPA. */
  public Usuario() {
    // Constructor vacío
  }

  /**
   * Constructor principal para crear un nuevo Usuario.
   *
   * @param nombreUsuario El nombre de usuario (debe ser único).
   * @param correoElectronico El correo electrónico (debe ser único).
   * @param contrasena La contraseña (en texto plano, se hasheará en el servicio).
   * @throws RegistroInvalidoException Si alguno de los campos es inválido.
   */
  public Usuario(String nombreUsuario, String correoElectronico, String contrasena)
      throws RegistroInvalidoException {
    setNombreUsuario(nombreUsuario);
    setCorreoElectronico(correoElectronico);
    setContrasena(contrasena);
    this.tareas = new ArrayList<>();
    this.logros = new ArrayList<>();
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

  /** Resetea los puntos de liga del usuario al inicio de una nueva temporada. */
  public void resetearPuntosLiga() {
    this.puntosMesPasado = this.puntosLiga;
    this.puntosLiga = 0;
    this.liga = "Bronce";
  }

  /**
   * Devuelve una copia de la lista de logros que el usuario ha desbloqueado.
   *
   * @return Una lista nueva (para evitar alterar la original) de objetos Logro.
   */
  public List<Logro> getLogros() {
    return this.logros;
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

  /* Recibe una Tarea como parámetro y la agrega a la lista de tareas del usuario.
  public void agregarTarea(Tarea tarea) {
    tareas.add(tarea);
  }

  public void removerTarea(Tarea tarea) {
    tareas.remove(tarea);
  }
  */
  /**
   * Cancela (elimina) una tarea pendiente de la lista del usuario.
   *
   * @param nombreTarea El nombre de la tarea a cancelar.
   * @throws RegistroInvalidoException Si la tarea no se encuentra.
   */
  public void cancelarTarea(String nombreTarea) throws RegistroInvalidoException {
    Tarea tarea = buscarTareaPorNombre(nombreTarea);
    if (tarea == null) {
      throw new RegistroInvalidoException(
          "La tarea '"
              + nombreTarea
              + "' no se encuentra en la lista de tareas pendientes de este usuario.");
    }

    tareas.remove(tarea);
    tarea.setUsuario(null);
    System.out.println("La tarea '" + nombreTarea + "' ha sido eliminada. No has ganado puntos.");
  }

  /*
   * (Por hacer) Comprueba y desbloquea logros basados en el estado actual del usuario.
   */
  // ARREGLO: Renombrado método
  private void comprobarYdesbloquearLogros() {
    // por hacer
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
