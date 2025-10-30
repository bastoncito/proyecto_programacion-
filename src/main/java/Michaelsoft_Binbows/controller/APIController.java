package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.dto.TareaDTO;
import Michaelsoft_Binbows.dto.UsuarioDTO;
import Michaelsoft_Binbows.entities.*;
import Michaelsoft_Binbows.exceptions.EdicionInvalidaException;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.services.*;
import Michaelsoft_Binbows.util.Dificultad;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class APIController {
  private final UsuarioService usuarioService;
  private final TareaService tareaService;

  public APIController(UsuarioService usuarioService, TareaService tareaService) {
    this.usuarioService = usuarioService;
    this.tareaService = tareaService;
  }

  // funciona
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

  // funciona
  @GetMapping("/usuarios")
  public List<Usuario> getUsuarios() {
    return usuarioService.obtenerTodos();
  }

  @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @PostMapping("/usuarios")
  public ResponseEntity<Object> crearUsuario(@RequestBody UsuarioDTO usuarioDTO)
      throws EdicionInvalidaException, RegistroInvalidoException {
    String contraseña = usuarioDTO.contrasena;
    String validacion = usuarioService.validarContrasena(contraseña);
    if (validacion != null) {
      return ResponseEntity.status(400).body(validacion);
    }

    Usuario usuario =
        new Usuario(usuarioDTO.nombreUsuario, usuarioDTO.correoElectronico, usuarioDTO.contrasena);
    // Encriptar la contraseña antes de guardar
    usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
    try {
      usuarioService.guardarSinValidarContraseña(usuario);
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
    return ResponseEntity.status(201).body(usuario);
  }

  // funciona
  @GetMapping("/usuarios/{idUsuario}")
  public Optional<Usuario> getUsuarioPorId(@PathVariable("idUsuario") long idUsuario) {
    return usuarioService.obtenerPorId(idUsuario);
  }

  // Provisorio
  // funciona!
  @PutMapping("/usuarios/{idUsuario}")
  public ResponseEntity<Object> actualizarUsuario(
      @PathVariable("idUsuario") long idUsuario, @RequestBody UsuarioDTO usuarioDTO) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    Usuario u = usuarioOpt.get();
    try {
      usuarioService.actualizarUsuario(
          u.getCorreoElectronico(),
          usuarioDTO.nombreUsuario,
          usuarioDTO.correoElectronico,
          usuarioDTO.rol);
      if (usuarioDTO.contrasena != null && !usuarioDTO.contrasena.trim().isEmpty()) {
        usuarioService.actualizarContraseñaUsuario(u.getCorreoElectronico(), usuarioDTO.contrasena);
      }
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
    return ResponseEntity.ok().body("Usuario actualizado correctamente");
  }

  // funciona
  @DeleteMapping("/usuarios/{idUsuario}")
  public ResponseEntity<Object> eliminarUsuario(@PathVariable("idUsuario") long idUsuario) {
    usuarioService.eliminar(idUsuario);
    return ResponseEntity.ok().body("Usuario eliminado correctamente");
  }

  // funciona
  @GetMapping("/usuarios/{idUsuario}/tareas")
  public List<Tarea> getTareasPorUsuario(@PathVariable("idUsuario") long idUsuario) {
    return usuarioService.obtenerPorId(idUsuario).map(Usuario::getTareas).orElse(List.of());
  }

  // funciona
  @PostMapping("/usuarios/{idUsuario}/tareas")
  public ResponseEntity<Object> crearTareaParaUsuario(
      @PathVariable("idUsuario") long idUsuario, @RequestBody TareaDTO tareaDTO) {
    Optional<Usuario> usuarioOpt = usuarioService.obtenerPorId(idUsuario);
    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(404).body("Usuario no encontrado");
    }
    try {
      Tarea nuevaTarea = tareaService.crear(tareaDTO, idUsuario);
      return ResponseEntity.status(201).body(nuevaTarea);
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  // funciona
  @GetMapping("/usuarios/{idUsuario}/tareas/completadas")
  public ResponseEntity<Object> getTareasCompletadasPorUsuario(
      @PathVariable("idUsuario") long idUsuario) {
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

  // funciona
  @GetMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
  public ResponseEntity<Object> getTareaPorNumYUsuario(
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

  // funciona
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

  // funciona
  @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/actualizar")
  public ResponseEntity<Object> actualizarTareaPorIdYUsuario(
      @PathVariable("idUsuario") long idUsuario,
      @PathVariable("idTarea") long idTarea,
      @RequestBody TareaDTO tareaDTO) {
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
      String currentDificultad = tareaDTO.dificultad;
      if (currentDificultad == null) {
        currentDificultad = Dificultad.obtenerDificultadPorExp(t.getExp());
      }

      Tarea tareaActualizada =
          new Tarea(
              tareaDTO.nombre != null ? tareaDTO.nombre : t.getNombre(),
              tareaDTO.descripcion != null ? tareaDTO.descripcion : t.getDescripcion(),
              currentDificultad);
      u.actualizarTarea(t.getNombre(), tareaActualizada);
      return ResponseEntity.ok().body(tareaService.obtenerPorId(idTarea).get());
    } catch (Exception e) {
      return ResponseEntity.status(400).body(e.getMessage());
    }
  }

  // funciona
  @DeleteMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
  public ResponseEntity<Object> borrarTareaPorIdYUsuario(
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

  // funciona
  @GetMapping("/tareas")
  public List<Tarea> getTareas() {
    return tareaService.obtenerTodas();
  }
}
