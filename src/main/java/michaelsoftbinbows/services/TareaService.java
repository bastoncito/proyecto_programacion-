package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.data.TareaRepository;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.dto.TareaDto;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
  * Servicio para gestionar la lógica de negocio de las Tareas.
  * Proporciona métodos CRUD y lógica para Tareas recomendadas.
  */
@Service
public class TareaService {

  @Autowired private TareaRepository tareaRepository;
  @Autowired private UsuarioRepository usuarioRepository;

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
    Tarea tarea = new Tarea(tareaDto.nombre, tareaDto.descripcion, tareaDto.dificultad);
    // This will handle duplicate validation and bidirectional relationship
    usuario.agregarTarea(tarea); 
    return guardar(tarea);
  }

  /**
    * Actualiza una tarea existente.
    *
    * @param id El ID de la tarea a actualizar.
    * @param tareaDto DTO con la nueva información.
    * @return La tarea actualizada.
    * @throws TareaInvalidaException si los nuevos datos no son válidos.
    */
  @Transactional
  public Tarea actualizar(Long id, TareaDto tareaDto) throws TareaInvalidaException {
    Tarea tarea = tareaRepository.findById(id).get();
    tarea.setNombre(tareaDto.nombre);
    tarea.setDescripcion(tareaDto.descripcion);
    // Actualizar otros campos según sea necesario
    return guardar(tarea);
  }

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
    Optional<Tarea> tareaOpt = tareaRepository.findById(id);
    if (tareaOpt.isPresent()) {
      Tarea tarea = tareaOpt.get();
      // Remove the task from the user's list first
      var usuario = tarea.getUsuario();
      usuario.getTareas().remove(tarea);
      usuarioRepository.save(usuario);
      // Then delete from repository
      tareaRepository.deleteById(id);
    }
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