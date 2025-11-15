package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.data.TareaRepository;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.dto.TareaDto;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.util.Dificultad;
import michaelsoftbinbows.util.TareaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar la lógica de negocio de las Tareas. Proporciona métodos CRUD y lógica
 * para Tareas recomendadas.
 */
@Service
public class TareaService {

  @Autowired private TareaRepository tareaRepository;
  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private GestorLogrosService gestorLogrosService;
  private TareaValidator tareaValidator = new TareaValidator();

  /**
   * Obtiene todas las tareas de la base de datos.
   *
   * @return Lista de todas las tareas.
   */
  public List<Tarea> obtenerTodas() {
    return tareaRepository.findAll();
  }

  /**
   * Obtiene una tarea específica por su ID.
   *
   * @param id El ID de la tarea.
   * @return Un Optional con la tarea si se encuentra.
   */
  public Optional<Tarea> obtenerPorId(Long id) {
    return tareaRepository.findById(id);
  }

  /**
   * Obtiene una tarea por su nombre y el ID del usuario.
   *
   * @param nombre nombre de la Tarea
   * @param usuarioId Id del usuario asociado
   * @return Optional con la tarea si se encuentra
   */
  public Optional<Tarea> obtenerPorNombreYUsuarioId(String nombre, Long usuarioId) {
    return tareaRepository.findByNombreAndUsuarioId(nombre, usuarioId);
  }

  /**
   * Obtiene la tarea semanal activa para un usuario específico.
   *
   * @param usuarioId El ID del usuario.
   * @return Un Optional que contiene la tarea semanal si existe.
   */
  public Optional<Tarea> obtenerTareaSemanal(Long usuarioId) {
    return tareaRepository.findByUsuarioIdAndEsSemanalTrue(usuarioId);
  }

  /**
   * Crea una nueva tarea y la asocia a un usuario.
   *
   * @param tareaDto DTO con la información de la nueva tarea.
   * @param userId ID del usuario al que se asociará la tarea.
   * @return La tarea creada y guardada.
   * @throws TareaInvalidaException si la tarea no es válida.
   */
  @Transactional
  public Tarea crear(TareaDto tareaDto, long userId) throws TareaInvalidaException {
    var usuario = usuarioRepository.findById(userId).get();
    String error = tareaValidator.nombreTareaValido(tareaDto.nombre);
    if (error != null) {
      throw new TareaInvalidaException(error, tareaDto.nombre, tareaDto.descripcion);
    }
    error = tareaValidator.descripcionTareaValida(tareaDto.descripcion);
    if (error != null) {
      throw new TareaInvalidaException(error, tareaDto.nombre, tareaDto.descripcion);
    }
    error = tareaValidator.dificultadValida(tareaDto.dificultad);
    if (error != null) {
      throw new TareaInvalidaException(error, tareaDto.nombre, tareaDto.descripcion);
    }
    Tarea tarea = new Tarea(tareaDto.nombre, tareaDto.descripcion, tareaDto.dificultad);
    // This will handle duplicate validation and bidirectional relationship
    if (tareaRepository.existsByNombreAndUsuarioId(tarea.getNombre(), userId)) {
      throw new TareaInvalidaException(
          "Tarea \"" + tarea.getNombre() + "\" ya existente.",
          tarea.getNombre(),
          tarea.getDescripcion());
    }
    if (tareaRepository.existsByDescripcionAndUsuarioId(tarea.getDescripcion(), userId)) {
      throw new TareaInvalidaException(
          "Tarea con descripción \"" + tarea.getDescripcion() + "\" ya existe.",
          tarea.getNombre(),
          tarea.getDescripcion());
    }
    tarea.setUsuario(usuario);
    // usuario.agregarTarea(tarea);
    System.out.println("Tarea '" + tarea.getNombre() + "' agregada exitosamente.");
    return guardar(tarea);
  }

  @Transactional
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
  public void actualizarTarea(Long usuarioId, String nombreOriginal, TareaDto tareaActualizada)
      throws RegistroInvalidoException, TareaInvalidaException {
    // Buscamos la tarea que queremos actualizar.
    if (tareaActualizada == null) {
      throw new RegistroInvalidoException(
          "Error: Los datos de la tarea actualizada no pueden ser nulos.");
    }
    if (tareaRepository.existsByNombreAndUsuarioId(nombreOriginal, usuarioId) == false) {
      throw new RegistroInvalidoException(
          "Error: No se encontró la tarea '" + nombreOriginal + "' para actualizar.");
    }
    if (tareaRepository.existsByNombreAndUsuarioId(tareaActualizada.nombre, usuarioId)
        && !nombreOriginal.equalsIgnoreCase(tareaActualizada.nombre)) {
      throw new RegistroInvalidoException(
          "Ya existe otra tarea con el nombre '"
              + tareaActualizada.nombre
              + "'. Elige un nombre diferente.");
    }
    String error = tareaValidator.nombreTareaValido(tareaActualizada.nombre);
    if (error != null) {
      throw new TareaInvalidaException(
          error, tareaActualizada.nombre, tareaActualizada.descripcion);
    }
    error = tareaValidator.descripcionTareaValida(tareaActualizada.descripcion);
    if (error != null) {
      throw new TareaInvalidaException(
          error, tareaActualizada.nombre, tareaActualizada.descripcion);
    }
    error = tareaValidator.dificultadValida(tareaActualizada.dificultad);
    if (error != null) {
      throw new TareaInvalidaException(
          error, tareaActualizada.nombre, tareaActualizada.descripcion);
    }
    Tarea tareaAactualizar =
        tareaRepository.findByNombreAndUsuarioId(nombreOriginal, usuarioId).get();
    tareaAactualizar.setNombre(tareaActualizada.nombre);
    tareaAactualizar.setDescripcion(tareaActualizada.descripcion);
    tareaAactualizar.setExp(Dificultad.obtenerExpPorDificultad(tareaActualizada.dificultad));
    tareaAactualizar.setFechaExpiracion(
        Dificultad.obtenerDiasPorDificultad(tareaActualizada.dificultad));

    System.out.println(
        "LOG: Tarea '"
            + nombreOriginal
            + "' del usuario '"
            + tareaAactualizar.getUsuario().getNombreUsuario()
            + "' actualizada exitosamente");
  }

  @Transactional
  /**
   * Guarda una entidad Tarea en la base de datos.
   *
   * @param tarea La tarea a guardar.
   * @return La tarea guardada.
   */
  public Tarea guardar(Tarea tarea) {
    return tareaRepository.save(tarea);
  }

  /**
   * Elimina una tarea por su ID, asegurando la desvinculación con el usuario.
   *
   * @param id El ID de la tarea a eliminar.
   */
  @Transactional
  public void eliminar(Long id) {
    if (tareaRepository.existsById(id) == false) {
      throw new IllegalArgumentException("Tarea con ID " + id + " no encontrada.");
    }
    tareaRepository.deleteById(id);
    System.out.println("Tarea con ID " + id + " eliminada exitosamente.");
  }

  /**
   * Elimina una tarea por el ID del usuario y el nombre de la tarea.
   *
   * @param usuarioId Id del usuario al que pertenece la tarea
   * @param nombreTarea Nombre de la tarea a eliminar
   */
  @Transactional
  public void eliminarPorUsuarioYNombreTarea(Long usuarioId, String nombreTarea) {
    if (tareaRepository.existsByNombreAndUsuarioId(nombreTarea, usuarioId) == false) {
      throw new IllegalArgumentException(
          "Tarea '" + nombreTarea + "' no encontrada para el usuario con ID " + usuarioId + ".");
    }
    Optional<Tarea> tareaOpt = tareaRepository.findByNombreAndUsuarioId(nombreTarea, usuarioId);
    String nombreUsuario = tareaOpt.get().getUsuario().getNombreUsuario();
    tareaRepository.delete(tareaOpt.get());
    System.out.println(
        "Tarea '" + nombreTarea + "' del usuario '" + nombreUsuario + "'' eliminada exitosamente.");
  }

  private Tarea crearTareaBase(String nombre, String descripcion, String dificultad, String clima) {
    try {
      Tarea t = new Tarea(nombre, descripcion, dificultad);
      t.setClimaCompatible(clima);
      return t;
    } catch (TareaInvalidaException e) {
      return null;
    }
  }

  /**
   * Obtiene una lista de tareas base recomendadas según el clima.
   *
   * @param clima El clima actual (ej. "Soleado", "Lluvia").
   * @return Una lista de tareas compatibles con ese clima.
   */
  public List<Tarea> obtenerTareasRecomendadasPorClima(String clima) {
    List<Tarea> base = new ArrayList<>();
    Tarea t1 = crearTareaBase("Salir a trotar", "Haz ejercicio al aire libre", "Medio", "Soleado");
    Tarea t2 =
        crearTareaBase("Leer un libro", "Disfruta de una lectura en casa", "Fácil", "Lluvia");
    Tarea t3 = crearTareaBase("Ir al parque", "Pasea y relájate", "Fácil", "Nublado");

    if (t1 != null) {
      base.add(t1);
    }
    if (t2 != null) {
      base.add(t2);
    }
    if (t3 != null) {
      base.add(t3);
    }

    // ARREGLO: Declaración de t4 movida más cerca de su uso.
    Tarea t4 =
        crearTareaBase("Ver una película", "Relájate viendo una película", "Fácil", "Lluvia");
    if (t4 != null) {
      base.add(t4);
    }

    // ARREGLO: Declaración de t5 movida más cerca de su uso.
    Tarea t5 = crearTareaBase("Jardinería", "Cuida tus plantas", "Medio", "Soleado");
    if (t5 != null) {
      base.add(t5);
    }

    if (clima == null) {
      return new ArrayList<>(); // <-- Evita el error si clima es null
    }

    return base.stream().filter(t -> t.getClimaCompatible().equalsIgnoreCase(clima)).toList();
  }
}
