package Michaelsoft_Binbows.entities;

import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import Michaelsoft_Binbows.model.Rol;
import Michaelsoft_Binbows.util.SistemaNiveles;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Usuario {

  @NonNull
  @Column(nullable = false, unique = true)
  private String nombreUsuario;

  @NonNull
  @Column(nullable = false)
  private String contraseña;

  @NonNull
  @Column(nullable = false, unique = true)
  private String correoElectronico;

  private int experiencia, nivelExperiencia, racha;
  private Rol rol;
  private LocalDateTime fechaRegistro;
  private LocalDate fechaRacha;
  //Testing sistema de ligas y puntos ------------
  @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
  private int puntosLiga;

  @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'Bronce'")
  private String liga;
  //Fin testing ---------
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private List<Tarea> tareas = new ArrayList<>();

  @Transient private List<Logro> logros = new ArrayList<>();

  private String ciudad;

  // Constructor vacío requerido por JPA
  public Usuario() {}

  public Usuario(String nombre_usuario, String correo_electronico, String contraseña)
      throws RegistroInvalidoException {
    setNombreUsuario(nombre_usuario);
    setCorreoElectronico(correo_electronico);
    setContraseña(contraseña);
    this.tareas = new ArrayList<>();
    this.logros = new ArrayList<>();
    this.experiencia = 0;
    this.nivelExperiencia = 1;
    this.racha = 0;
    // --- Ligas ---
    this.puntosLiga = 0;
    this.liga = "Bronce";
    // --- FIN Ligas ---
    this.rol = Rol.USUARIO;
    this.fechaRegistro = LocalDateTime.now();
    this.fechaRacha = null;
  }

  /** Getters/Setters */
  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public String getCorreoElectronico() {
    return correoElectronico;
  }

  public String getContraseña() {
    return contraseña;
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

  public void setContraseña(String contraseña) {
    this.contraseña = contraseña;
  }

  public String getCiudad() {
    return ciudad;
  }

  public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
  }
  
  /**
   * Resetea los puntos de liga del usuario al inicio de una nueva temporada.
   */
  public void resetearPuntosLiga() {
    this.puntosLiga = 0;
    this.liga = "Bronce";
  }

  /**
   * Actualiza la liga del usuario basado en sus puntosLiga.
   */
  private void actualizarLiga() {
    // Rango de puntos
    if (this.puntosLiga >= 5000) { 
      this.liga = "Diamante";
    } else if (this.puntosLiga >= 3000) { 
      this.liga = "Platino";
    } else if (this.puntosLiga >= 1500) { 
      this.liga = "Oro";
    } else if (this.puntosLiga >= 500) { 
      this.liga = "Plata";
    } else { 
      this.liga = "Bronce";
    }
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
   * Devuelve solo las tareas PENDIENTES (no completadas) La tarea es PENDIENTE si
   * getFechaCompletada() devuelve null.
   *
   * @return Lista de tareas pendientes
   */
  public List<Tarea> getTareasPendientes() {
    return tareas.stream()
        // Si la fecha de completado es null, la tarea está PENDIENTE
        .filter(t -> t.getFechaCompletada() == null)
        .collect(Collectors.toList());
  }

  /**
   * Devuelve solo las tareas COMPLETADAS ordenadas por fecha de completado (más recientes primero)
   * La tarea es COMPLETADA si getFechaCompletada() NO es null.
   *
   * @return Lista de tareas completadas para el historial
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
   * Devuelve el número de tareas completadas La tarea es COMPLETADA si getFechaCompletada() NO es
   * null.
   *
   * @return Cantidad de tareas completadas
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
   * son válidos
   *
   * @throws TareaInvalidaException si la tarea no es válida
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

  /*
   * Recibe la tarea que se completo por el usuario
   * Marca una tarea como completada, la mueve a la lista de tareas completadas,
   * añade la experiencia al usuario y verifica si sube de nivel.
   */
  public void completarTarea(String nombreTarea) throws RegistroInvalidoException {
    // Validar que la tarea a completar realmente existe en la lista.
    Tarea tareaACompletar = buscarTareaPorNombre(nombreTarea);
    if (tareaACompletar == null) {
      throw new RegistroInvalidoException(
          "La tarea '"
              + nombreTarea
              + "' no se encuentra en la lista de tareas pendientes de este usuario.");
    }

    // Verificar si la tarea ya está completada

    if (tareaACompletar.getFechaCompletada() != null) {
      throw new RegistroInvalidoException("La tarea '" + nombreTarea + "' ya está completada.");
    }

    // Marcar la tarea como completada, estableciendo la fecha actual
    tareaACompletar.setFechaCompletada(LocalDateTime.now());

    // Verificar la subida de la racha
    aumentarRacha();

    // Añadir la experiencia de la tarea al total del usuario.
    this.experiencia += tareaACompletar.getExp();
    System.out.println(
        "¡'"
            + this.nombreUsuario
            + "' ha completado la tarea '"
            + nombreTarea
            + "' y ha ganado "
            + tareaACompletar.getExp()
            + " de experiencia!");
    System.out.println("Experiencia total: " + this.experiencia);

    // Llamado al método que verificará si el usuario ha subido de nivel.
    verificarSubidaDeNivel();

    // --- INICIO LÓGICA DE LIGAS ---
    
    // 1. Suma al contador de la Temporada (independiente del reseteo de nivel)
    this.puntosLiga += tareaACompletar.getExp(); 
    
    // 2. Actualiza el string de la Liga (Bronce, Plata, etc.)
    actualizarLiga(); 
    
    // --- FIN LÓGICA DE LIGAS ---
  }

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
   *
   */
  private void comprobarYDesbloquearLogros() {
    // por hacer
  }

  public void resetRacha() {
    LocalDate hoy = LocalDate.now();
    if (fechaRacha == null) {
      return; // no hacer nada. aplica para usuarios nuevos.
    }
    if (fechaRacha.plusDays(1).isBefore(hoy)) {
      racha = 0; // resetear racha si la última tarea se completo hace más de un día
    }
  }

  public void aumentarRacha() {
    LocalDate hoy = LocalDate.now();
    if (fechaRacha == null || hoy.isAfter(fechaRacha)) {
      fechaRacha = hoy; // si es la primera tarea completada por el usuario
      racha++;
    }
  }

  public void cuentarrachas() {
    LocalDate hoy = LocalDate.now();
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
   * Busca una tarea PENDIENTE por su nombre Una tarea es PENDIENTE si getFechaCompletada() devuelve
   * null.
   *
   * @param nombre Nombre de la tarea a buscar
   * @return La tarea encontrada o null si no existe
   */
  public Tarea buscarTareaPorNombre(String nombre) {
    for (Tarea t : this.tareas) {
      if (t.getFechaCompletada() == null && t.getNombre().trim().equals(nombre.trim())) {
        return t;
      }
    }
    return null;
  }

  /*
   * Actualiza una tarea existente en la lista de tareas pendientes.
   * Busca la tarea por su nombre original y reemplaza sus datos con los de la tarea actualizada.
   *
   * @param nombreOriginal El nombre actual de la tarea que se quiere modificar.
   * @param tareaActualizada Un objeto Tarea con los nuevos datos (nombre, descripción, etc.).
   * @throws RegistroInvalidoException Si la tarea original no se encuentra o si el nuevo nombre ya está en uso por otra tarea.
   * @throws TareaInvalidaException Si los datos de la tarea actualizada son inválidos (lanzado por los setters).
   */
  public void actualizarTarea(String nombreOriginal, Tarea tareaActualizada)
      throws RegistroInvalidoException, TareaInvalidaException {
    // Buscamos la tarea que queremos actualizar.
    Tarea tareaAActualizar = buscarTareaPorNombre(nombreOriginal);
    if (tareaAActualizar == null) {
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

    tareaAActualizar.setNombre(tareaActualizada.getNombre());
    tareaAActualizar.setDescripcion(tareaActualizada.getDescripcion());
    tareaAActualizar.setExp(tareaActualizada.getExp());
    tareaAActualizar.setFechaExpiracion(tareaActualizada.getFechaExpiracion());

    System.out.println(
        "LOG: Tarea '"
            + nombreOriginal
            + "' actualizada exitosamente a '"
            + tareaActualizada.getNombre()
            + "'.");
  }

  /**
   * Calcula la experiencia TOTAL ganada sumando todas las tareas completadas Una tarea está
   * COMPLETADA si getFechaCompletada() NO es null.
   *
   * @return La suma de experiencia de todas las tareas completadas
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
