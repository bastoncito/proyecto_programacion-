package michaelsoftbinbows.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import michaelsoftbinbows.dto.TareaDto;
import michaelsoftbinbows.dto.UsuarioDto;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.services.TareaService;
import michaelsoftbinbows.services.UsuarioService;
import michaelsoftbinbows.util.Dificultad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para funcionamiento de API REST.
 *
 * <p>Debería ser posible acceder a todo el CRUD desde aquí.
 */
@RestController
@RequestMapping("/api")
public class ApiController {
  private final UsuarioService usuarioService;
  private final TareaService tareaService;

  /**
   * Constructor de clase.
   *
   * <p>Inyecta los servicios necesarios para funcionamiento.
   *
   * @param usuarioService para manejar acciones de Usuario
   * @param tareaService para manejar acciones de Tarea
   */
  public ApiController(UsuarioService usuarioService, TareaService tareaService) {
    this.usuarioService = usuarioService;
    this.tareaService = tareaService;
  }

  /**
   * Muestra info. de la API, con todos los endpoints disponibles.
   *
   * @return información API
   */
  @GetMapping
  public Map<String, Object> apiRoot() {
    Map<String, Object> info = new LinkedHashMap<>();
    info.put("mensaje", "Good Time API");
    info.put("version", "v1.0");
    info.put(
        "available_endpoints",
        List.of(
            "GET/POST /api/usuarios )",
            "GET/PUT/DELETE /api/usuarios/{idUsuario}",
            "GET/POST /api/usuarios/{idUsuario}/tareas",
            "DELETE /api/usuarios/{idUsuario}/tareas/{idTarea}",
            "PUT /api/usuarios/{idUsuario}/tareas/{idTarea}/completar",
            "PUT /api/usuarios/{idUsuario}/tareas/{idTarea}/actualizar",
            "GET /api/usuarios/{idUsuario}/tareas/completadas",
            "GET /api/tareas"));
    return info;
  }

  /**
   * Permite ver a todos los usuarios registrados.
   *
   * @return lista de usuarios
   */
  @GetMapping("/usuarios")
  public List<Usuario> getUsuarios() {
    return usuarioService.obtenerTodos();
  }

  @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  /**
   * Permite crear a un usuario nuevo.
   *
   * @param usuarioDto DTO para recibir datos nuevos desde Postman
   * @return estado de la acción (ok con usuario nuevo/error)
   * @throws RegistroInvalidoException si el usuario no es válido
   */
  @PostMapping("/usuarios")
  public ResponseEntity<Object> crearUsuario(@RequestBody UsuarioDto usuarioDto)
      throws RegistroInvalidoException {
    String contrasena = usuarioDto.contrasena;
    String validacion = usuarioService.validarContrasena(contrasena);
    if (validacion != null) {
      return ResponseEntity.status(400).body(validacion);
    }

    Usuario usuario =
        new Usuario(usuarioDto.nombreUsuario, usuarioDto.correoElectronico, usuarioDto.contrasena);
    // Encriptar la contrasena antes de guardar
    usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
    try {
      usuarioService.guardarSinValidarContrasena(usuario);
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
    return ResponseEntity.status(201).body(usuario);
  }

  /**
   * Permite ver los datos de un usuario.
   *
   * @param idUsuario usuario en sí
   * @return datos de usuario
   */
  @GetMapping("/usuarios/{idUsuario}")
  public Optional<Usuario> getUsuario(@PathVariable("idUsuario") long idUsuario) {
    return usuarioService.obtenerPorId(idUsuario);
  }

  /**
   * Actualiza la información de un usuario.
   *
   * @param idUsuario usuario a actualizar
   * @param usuarioDto DTO para recibir datos actualizados desde Postman
   * @return estado de la acción (ok/error)
   */
  @PutMapping("/usuarios/{idUsuario}")
  public ResponseEntity<Object> actualizarUsuario(
      @PathVariable("idUsuario") long idUsuario, @RequestBody UsuarioDto usuarioDto) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    Usuario u = usuarioOpt.get();
    try {
      usuarioService.actualizarUsuario(
          u.getCorreoElectronico(),
          usuarioDto.nombreUsuario,
          usuarioDto.correoElectronico,
          usuarioDto.rol);
      if (usuarioDto.contrasena != null && !usuarioDto.contrasena.trim().isEmpty()) {
        usuarioService.actualizarContrasenaUsuario(u.getCorreoElectronico(), usuarioDto.contrasena);
      }
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
    return ResponseEntity.ok().body("Usuario actualizado correctamente");
  }

  /**
   * Elimina a un usuario.
   *
   * @param idUsuario usuario a eliminar
   * @return estado de la acción (ok/error)
   */
  @DeleteMapping("/usuarios/{idUsuario}")
  public ResponseEntity<Object> eliminarUsuario(@PathVariable("idUsuario") long idUsuario) {
    usuarioService.eliminar(idUsuario);
    return ResponseEntity.ok().body("Usuario eliminado correctamente");
  }

  /**
   * Permite ver todas las tareas de un usuario.
   *
   * @param idUsuario usuario en sí
   * @return estado de la acción (ok/error)
   */
  @GetMapping("/usuarios/{idUsuario}/tareas")
  public List<Tarea> tareasUsuario(@PathVariable("idUsuario") long idUsuario) {
    return usuarioService.obtenerPorId(idUsuario).map(Usuario::getTareas).orElse(List.of());
  }

  /**
   * Crea una nueva tarea para un usuario.
   *
   * @param idUsuario usuario que recibirá la tarea
   * @param tareaDto DTO para obtener datos de tarea desde Postman
   * @return estado de la acción (ok/error)
   */
  @PostMapping("/usuarios/{idUsuario}/tareas")
  public ResponseEntity<Object> crearTarea(
      @PathVariable("idUsuario") long idUsuario, @RequestBody TareaDto tareaDto) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    try {
      Tarea nuevaTarea = tareaService.crear(tareaDto, idUsuario);
      return ResponseEntity.status(201).body(nuevaTarea);
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  /**
   * Permite ver las tareas completadas de un usuario.
   *
   * @param idUsuario usuario en sí
   * @return estado de la acción (ok con lista/error)
   */
  @GetMapping("/usuarios/{idUsuario}/tareas/completadas")
  public ResponseEntity<Object> getCompletadas(@PathVariable("idUsuario") long idUsuario) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    List<Tarea> tareasCompletadas =
        usuarioOpt.get().getTareas().stream()
            .filter(tarea -> tarea.getFechaCompletada() != null)
            .toList();
    return ResponseEntity.ok().body(tareasCompletadas);
  }

  /**
   * Permite ver las tareas pendientes de un usuario.
   *
   * @param idUsuario usuario en sí
   * @return estado de la acción (ok con lista/error)
   */
  @GetMapping("/usuarios/{idUsuario}/tareas/pendientes")
  public ResponseEntity<Object> getPendientes(@PathVariable("idUsuario") long idUsuario) {

    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);

    // 1. Comprueba si el usuario existe
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }

    Usuario u = usuarioOpt.get();

    // 2. Usa el método de la entidad que se encuentra
    List<Tarea> tareasPendientes = u.getTareasPendientes();

    // 3. Devuelve la lista con un OK
    return ResponseEntity.ok().body(tareasPendientes);
  }

  /**
   * Permite ver los datos de una tarea específica.
   *
   * @param idUsuario usuario al que pertenece
   * @param idTarea tarea en sí
   * @return estado de la acción (ok con tarea/error)
   */
  @GetMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
  public ResponseEntity<Object> getTarea(
      @PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    Optional<Tarea> tareaOpt = tareaService.obtenerPorId(idTarea);
    if (tareaOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Tarea no encontrada");
    }
    Tarea t = tareaOpt.get();
    if (t.getUsuario().getId() != idUsuario) {
      return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
    }
    return ResponseEntity.ok().body(t);
  }

  /**
   * Marca una tarea como completada.
   *
   * @param idUsuario usuario al que pertenece
   * @param idTarea tarea a completar
   * @return estado de la acción (ok/error)
   */
  @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/completar")
  public ResponseEntity<Object> completarTarea(
      @PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    Usuario u = usuarioOpt.get();

    Optional<Tarea> tareaOpt = tareaService.obtenerPorId(idTarea);
    if (tareaOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Tarea no encontrada");
    }
    Tarea t = tareaOpt.get();

    if (t.getUsuario().getId() != idUsuario) {
      return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
    }
    if (t.getFechaCompletada() != null) {
      return ResponseEntity.status(400).body("La tarea ya ha sido completada");
    }
    try {
      usuarioService.completarTarea(u.getCorreoElectronico(), t.getNombre());
    } catch (RegistroInvalidoException e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
    return ResponseEntity.ok().body("Tarea completada correctamente");
  }

  /**
   * Actualiza los datos de una tarea específica.
   *
   * @param idUsuario usuario al que pertenece la tarea
   * @param idTarea tarea a actualizar
   * @param tareaDto DTO para pasar datos actualizados desde Postman
   * @return estado de la acción (ok/error)
   */
  @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/actualizar")
  public ResponseEntity<Object> actualizarTarea(
      @PathVariable("idUsuario") long idUsuario,
      @PathVariable("idTarea") long idTarea,
      @RequestBody TareaDto tareaDto) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    Usuario u = usuarioOpt.get();

    Optional<Tarea> tareaOpt = tareaService.obtenerPorId(idTarea);
    if (tareaOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Tarea no encontrada");
    }
    Tarea t = tareaOpt.get();

    if (t.getUsuario().getId() != idUsuario) {
      return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
    }
    try {
      String currentDificultad = tareaDto.dificultad;
      if (currentDificultad == null) {
        currentDificultad = Dificultad.obtenerDificultadPorExp(t.getExp());
      }

      Tarea tareaActualizada =
          new Tarea(
              tareaDto.nombre != null ? tareaDto.nombre : t.getNombre(),
              tareaDto.descripcion != null ? tareaDto.descripcion : t.getDescripcion(),
              currentDificultad);
      u.actualizarTarea(t.getNombre(), tareaActualizada);
      return ResponseEntity.ok().body(tareaService.obtenerPorId(idTarea).get());
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  /**
   * Borra una tarea específica.
   *
   * @param idUsuario usuario al que pertenece la tarea
   * @param idTarea tarea en sí
   * @return estado de la acción (ok/error)
   */
  @DeleteMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
  public ResponseEntity<Object> borrarTarea(
      @PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) {
    Optional<Tarea> tareaOpt = tareaService.obtenerPorId(idTarea);
    if (tareaOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Tarea no encontrada");
    }
    Tarea tarea = tareaOpt.get();

    if (tarea.getUsuario().getId() != idUsuario) {
      return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
    }

    try {
      tareaService.eliminar(idTarea);
      return ResponseEntity.ok().body("Tarea eliminada correctamente");
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  /**
   * Para obtener todas las tareas registradas.
   *
   * @return lista de tareas
   */
  @GetMapping("/tareas")
  public List<Tarea> getTareas() {
    return tareaService.obtenerTodas();
  }
}
