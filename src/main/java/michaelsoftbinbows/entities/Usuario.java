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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
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

  @Transient private List<Logro> logros = new ArrayList<>();

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

  public void setRol(Rol rol) {
    this.rol = rol;
  }

  public void setId(Long id) {
    this.id = id;
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
    return new ArrayList<>(this.logros);
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
   * Recibe una Tarea como parámetro y la agrega a la base de datos si su nombre, descripción y exp
   * son válidos.
   *
   * @throws TareaInvalidaException si la tarea no es válida.
   */
  public void agregarTarea(Tarea tarea) throws TareaInvalidaException {
    if (tareaExistePorNombre(tarea.getNombre())) {
      throw new TareaInvalidaException(
          "Tarea \"" + tarea.getNombre() + "\" ya existente.",
          tarea.getNombre(),
          tarea.getDescripcion());
    }
    if (tareaExistePorDescripcion(tarea.getDescripcion())) {
      throw new TareaInvalidaException(
          "Tarea con descripción \"" + tarea.getDescripcion() + "\" ya existe.",
          tarea.getNombre(),
          tarea.getDescripcion());
    }
    tarea.setUsuario(this);
    tareas.add(tarea);
    System.out.println("Tarea '" + tarea.getNombre() + "' agregada exitosamente.");
  }

  private boolean tareaExistePorNombre(String nombre) {
    for (Tarea tareaExistente : tareas) {
      if (tareaExistente.getNombre().equalsIgnoreCase(nombre)) {
        return true;
      }
    }
    return false;
  }

  private boolean tareaExistePorDescripcion(String descripcion) {
    for (Tarea tareaExistente : tareas) {
      if (tareaExistente.getDescripcion().equalsIgnoreCase(descripcion)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Marca una tarea como completada, la mueve a la lista de tareas completadas, añade la
   * experiencia al usuario y verifica si sube de nivel.
   *
   * @param nombreTarea El nombre de la tarea a completar.
   * @throws RegistroInvalidoException Si la tarea no se encuentra o ya está completada.
   */
  public void completarTarea(String nombreTarea) throws RegistroInvalidoException {
    // Validar que la tarea a completar realmente existe en la lista.
    // ARREGLO: Renombrada variable
    Tarea tareaAcompletar = buscarTareaPorNombre(nombreTarea);
    if (tareaAcompletar == null) {
      throw new RegistroInvalidoException(
          "La tarea '"
              + nombreTarea
              + "' no se encuentra en la lista de tareas pendientes de este usuario.");
    }

    // Verificar si la tarea ya está completada

    if (tareaAcompletar.getFechaCompletada() != null) {
      throw new RegistroInvalidoException("La tarea '" + nombreTarea + "' ya está completada.");
    }

    // Marcar la tarea como completada, estableciendo la fecha actual
    tareaAcompletar.setFechaCompletada(LocalDateTime.now(ZoneId.systemDefault()));

    // Verifica y actualiza la subida de la racha
    actualizarRacha();

    // Anadir la experiencia de la tarea al total del usuario.
    this.experiencia += tareaAcompletar.getExp();
    System.out.println(
        "¡'"
            + this.nombreUsuario
            + "' ha completado la tarea '"
            + nombreTarea
            + "' y ha ganado "
            + tareaAcompletar.getExp()
            + " de experiencia!");
    System.out.println("Experiencia total: " + this.experiencia);

    // Llamado al método que verificará si el usuario ha subido de nivel.
    verificarSubidaDeNivel();

    // 1. Suma al contador de la Temporada (independiente del reseteo de nivel)
    this.puntosLiga += tareaAcompletar.getExp();
  }

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

  /**
   * Comprueba si la experiencia total del usuario es suficiente para subir al siguiente nivel.
   * Utiliza un bucle por si se ganan múltiples niveles a la vez.
   */
  private void verificarSubidaDeNivel() {
    int expSiguienteNivel = SistemaNiveles.experienciaParaNivel(this.nivelExperiencia + 1);

    // El usuario subira de nivel hasta donde su experiencia le permita
    while (this.experiencia >= expSiguienteNivel) {
      this.nivelExperiencia++;
      this.experiencia -= expSiguienteNivel;
      System.out.println("¡FELICIDADES! ¡Has subido al nivel " + this.nivelExperiencia + "!");
      // Se calcula la experiencia necesaria para el proximo nivel
      expSiguienteNivel = SistemaNiveles.experienciaParaNivel(this.nivelExperiencia + 1);
    }
  }

  /*
   * (Por hacer) Comprueba y desbloquea logros basados en el estado actual del usuario.
   */
  // ARREGLO: Renombrado método
  private void comprobarYdesbloquearLogros() {
    // por hacer
  }

  /** Resetea la racha del usuario a 0 si la última tarea se completó hace más de un día. */
  public void resetRacha() {
    LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
    if (fechaRacha == null) {
      return; // no hacer nada. aplica para usuarios nuevos.
    }
    if (fechaRacha.plusDays(1).isBefore(hoy)) {
      racha = 0; // resetear racha si la última tarea se completo hace más de un día
    }
  }

  /** Aumenta la racha del usuario si la tarea completada es en un nuevo día. */
  public void aumentarRacha() {
    LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
    if (fechaRacha == null || hoy.isAfter(fechaRacha)) {
      fechaRacha = hoy; // si es la primera tarea completada por el usuario
      racha++;
    }
  }

  /** Lógica completa de gestión de rachas (reseteo e incremento). */
  public void cuentarrachas() {
    LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
    if (fechaRacha == null) {
      return; // no hacer nada
    }
    if (fechaRacha.plusDays(1).isBefore(hoy)) {
      racha = 0; // resetear racha si la última tarea se completo hace más de un día
      fechaRacha = hoy;
    } else if (hoy.isAfter(fechaRacha)) {
      fechaRacha = hoy;
      racha++;
    }
  }

  /**
   * Busca una tarea PENDIENTE por su nombre. Una tarea es PENDIENTE si getFechaCompletada()
   * devuelve null.
   *
   * @param nombre Nombre de la tarea a buscar.
   * @return La tarea encontrada o null si no existe.
   */
  public Tarea buscarTareaPorNombre(String nombre) {
    for (Tarea t : this.tareas) {
      if (t.getFechaCompletada() == null && t.getNombre().trim().equals(nombre.trim())) {
        return t;
      }
    }
    return null;
  }

  /**
   * Actualiza una tarea existente en la lista de tareas pendientes. Busca la tarea por su nombre
   * original y reemplaza sus datos con los de la tarea actualizada.
   *
   * @param nombreOriginal El nombre actual de la tarea que se quiere modificar.
   * @param tareaActualizada Un objeto Tarea con los nuevos datos (nombre, descripción, etc.).
   * @throws RegistroInvalidoException Si la tarea original no se encuentra o si el nuevo nombre ya
   *     está en uso por otra tarea.
   * @throws TareaInvalidaException Si los datos de la tarea actualizada son inválidos (lanzado por
   *     los setters).
   */
  public void actualizarTarea(String nombreOriginal, Tarea tareaActualizada)
      throws RegistroInvalidoException, TareaInvalidaException {
    // Buscamos la tarea que queremos actualizar.
    Tarea tareaAactualizar = buscarTareaPorNombre(nombreOriginal);
    if (tareaAactualizar == null) {
      throw new RegistroInvalidoException(
          "Error: No se encontró la tarea '" + nombreOriginal + "' para actualizar.");
    }

    //  Comprobamos si el nuevo nombre de la tarea ya está siendo usado por OTRA tarea.
    //  Es importante asegurarse de que no estamos comparando la tarea consigo misma si el nombre no
    // ha cambiado.
    if (!nombreOriginal.equalsIgnoreCase(tareaActualizada.getNombre())) {
      if (buscarTareaPorNombre(tareaActualizada.getNombre()) != null) {
        throw new RegistroInvalidoException(
            "Ya existe otra tarea con el nombre '"
                + tareaActualizada.getNombre()
                + "'. Elige un nombre diferente.");
      }
    }

    tareaAactualizar.setNombre(tareaActualizada.getNombre());
    tareaAactualizar.setDescripcion(tareaActualizada.getDescripcion());
    tareaAactualizar.setExp(tareaActualizada.getExp());
    tareaAactualizar.setFechaExpiracion(tareaActualizada.getFechaExpiracion());

    System.out.println(
        "LOG: Tarea '"
            + nombreOriginal
            + "' actualizada exitosamente a '"
            + tareaActualizada.getNombre()
            + "'.");
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

  /**
   * Método unificado y robusto para actualizar la racha del usuario. Se encarga de incrementar,
   * reiniciar o mantener la racha según la fecha. Debe ser llamado cada vez que se completa una
   * tarea.
   */
  public void actualizarRacha() {
    LocalDate hoy = LocalDate.now();
    LocalDate fechaUltimaRacha = this.fechaRacha; // Usamos el nombre de tu campo

    // CASO 1: Es la primera tarea que el usuario completa en su vida.
    if (fechaUltimaRacha == null) {
      this.racha = 1;
    } else {
      // Calculamos los días de diferencia entre la última vez y hoy.
      long diasDiferencia = ChronoUnit.DAYS.between(fechaUltimaRacha, hoy);

      // CASO 2: Completó otra tarea hoy. La racha no cambia.
      if (diasDiferencia == 0) {
        // No se hace nada, la racha ya se contó para hoy.
        System.out.println("Racha diaria ya registrada. No se incrementa.");
        return; // Salimos del método para no actualizar la fecha innecesariamente
      }
      // CASO 3: La última tarea fue ayer. ¡La racha continúa!
      else if (diasDiferencia == 1) {
        this.racha++; // Incrementamos la racha existente
      }
      // CASO 4: La racha se rompió (pasó más de 1 día). Se reinicia a 1.
      else {
        this.racha = 1;
      }
    }
    // Actualizamos la fecha de la racha a hoy.
    this.fechaRacha = hoy;
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
