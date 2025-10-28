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
import java.util.List;

@Entity
public class Usuario {
  /*@NotBlank
  private String nombreUsuario;

  @NotBlank
  private String contraseña;

  @NotBlank
  @Email
  private String correoElectronico;*/

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

  /*private List<Tarea> tareas;
  private List<Tarea> tareasCompletadas;
  private List<Logro> logros;*/

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Tarea> tareas = new ArrayList<>();

  @Transient // Temporalmente, hasta adaptar Tarea y Logro como entidades
  private List<Tarea> tareasCompletadas = new ArrayList<>();

  @Transient private List<Logro> logros = new ArrayList<>();

  // Constructor vacío requerido por JPA
  public Usuario() {}

  public Usuario(String nombre_usuario, String correo_electronico, String contraseña)
      throws RegistroInvalidoException {
    setNombreUsuario(nombre_usuario);
    setCorreoElectronico(correo_electronico);
    setContraseña(contraseña);
    this.tareas = new ArrayList<>();
    this.tareasCompletadas = new ArrayList<>();
    this.logros = new ArrayList<>();
    this.experiencia = 0;
    this.nivelExperiencia = 1;
    this.racha = 0;
    this.rol = Rol.USUARIO;
    this.fechaRegistro = LocalDateTime.now(); // Fecha actual
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

  public int getNumeroCompletadas() {
    return tareasCompletadas.size();
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

  /*
   * Devuelve una copia de la lista de logros que el usuario ha desbloqueado.
   * @return Una lista nueva(para evitar alterar la original) de objetos Logro.
   */
  public List<Logro> getLogros() {
    return new ArrayList<>(this.logros);
  }

  /*
   * Devuelve una copia de la lista de tareas que el usuario ha completado.
   * @return Una lista nueva (para evitar alterar la original) de objetos Tarea.
   */
  public List<Tarea> getTareasCompletadas() {
    return new ArrayList<>(this.tareasCompletadas);
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

  /**
   * Recibe una Tarea como parametro la agrega a la base de datos si su nombre, descricpcion y exp
   * son validos
   *
   * @throws RegistroInvalidoException
   */
  public void agregarTarea(Tarea tarea)
      throws TareaInvalidaException { // TRABAJAR AQUI---->>>Validar si la tarea se puede agregar
    // Si pasa las validaciones, la agregamos
    if (tareaExistePorNombre(tarea.getNombre())) {
      throw new TareaInvalidaException(
          "Tarea \"" + tarea.getNombre() + "\" ya existente.",
          tarea.getNombre(),
          tarea.getDescripcion());
    }
    if (tareaExistePorDescripcion(tarea.getDescripcion())) {
      throw new TareaInvalidaException(
          "Tarea con descripción \"" + tarea.getNombre() + "\" ya existe.",
          tarea.getNombre(),
          tarea.getDescripcion());
    }
    tarea.setUsuario(this); // Asocia la tarea al usuario
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

    tareaACompletar.setFechaCompletada(
        LocalDateTime.now()); // Marca la fecha de completado como la actual.

    // Mover la tarea de la lista la lista de completadas.
    tareas.remove(tareaACompletar);
    tareasCompletadas.add(tareaACompletar);
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
    System.out.println("La tarea '" + nombreTarea + "' ha sido eliminada. No has ganado puntos.");
  }

  /*
   * Comprueba si la experiencia total del usuario es suficiente para subir al siguiente nivel.
   * Utiliza un bucle por si se ganan múltiples niveles a la vez.
   */
  private void verificarSubidaDeNivel() {
    // Calculamos la experiencia necesaria para el SIGUIENTE nivel.
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

  public Tarea buscarTareaPorNombre(String nombre) {
    for (Tarea t : this.tareas) {
      if (t.getNombre().trim().equals(nombre.trim())) {
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

    //  Si todas las validaciones pasan, actualizamos los campos de la tarea original
    //  con los nuevos valores. Usamos los 'setters' de la clase Tarea para que
    //  se apliquen sus propias validaciones (ej. longitud del nombre, etc.).
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
        this.tareas.size(),
        this.tareasCompletadas.size());
  }
}
