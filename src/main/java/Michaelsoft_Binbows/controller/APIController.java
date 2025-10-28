package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.dto.TareaDTO;
import Michaelsoft_Binbows.dto.UsuarioDTO;
import Michaelsoft_Binbows.entities.*;
import Michaelsoft_Binbows.exceptions.EdicionInvalidaException;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import Michaelsoft_Binbows.services.*;
import Michaelsoft_Binbows.util.Dificultad;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/* 
 * EXCEPCIONES QUE FALTAN
 * FALTA INICIALIZAR ROL Y FECHA DE REGISTRO 
 * MOVER TAREAS A COMPLETADAS
 * USUARIO/TAREA NO ENCONTRADA
 * USUARIO YA EXISTE
 * TAREA YA EXISTE
 * TAREA YA COMPLETADA
 * CREDENCIALES ACTUALIZADAS NO VALIDAS
 * NUMERO TAREA FUERA DE RANGO
 * NO HAY USUARIOS
 * NO HAY TAREAS
 * NO HAY TAREAS COMPLETADAS
*/

//USUARIO SE PUEDE CREAR CON EL MISMO NOMBRE SI ES QUE TIENE DISTINTO CORREO ELECTRONICO
//CORREGIRRRRR

@RestController
@RequestMapping("/api")
public class APIController {
    private final UsuarioService usuarioService;
    private final TareaService tareaService;

    public APIController(UsuarioService usuarioService, TareaService tareaService) {
        this.usuarioService = usuarioService;
        this.tareaService = tareaService;
    }

    //funciona
    @GetMapping
    public Map<String, Object> apiRoot() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("mensaje", "Good Time API");
        info.put("version", "v1.0");
        info.put("available_endpoints", List.of(
            "GET/POST /api/usuarios )",
            "GET/PUT/DELETE /api/usuarios/{idUsuario}",
            "GET/POST /api/usuarios/{idUsuario}/tareas",
            "DELETE /api/usuarios/{idUsuario}/tareas/{idTarea}",
            "PUT /api/usuarios/{idUsuario}/tareas/{idTarea}/completar",
            "PUT /api/usuarios/{idUsuario}/tareas/{idTarea}/actualizar",
            "GET /api/usuarios/{idUsuario}/tareas/completadas",
            "GET /api/tareas"
        ));
        return info;
    }

    //funciona
    @GetMapping("/usuarios")
    public List<Usuario> getUsuarios() {
        return usuarioService.obtenerTodos();
    }

    //funciona
    @PostMapping("/usuarios")
    public ResponseEntity<Object> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) throws EdicionInvalidaException, RegistroInvalidoException {
        Usuario usuario = new Usuario(usuarioDTO.nombreUsuario, usuarioDTO.correoElectronico, usuarioDTO.contrasena);
        try{
            usuarioService.guardar(usuario);
        }catch(Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.status(201).body(usuario);
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}")
    public Optional<Usuario> getUsuarioPorId(@PathVariable("idUsuario") long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario);
    }
    
    //Provisorio
    //funciona!
    @PutMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Object> actualizarUsuario(@PathVariable("idUsuario") long idUsuario, @RequestBody UsuarioDTO usuarioDTO){
        Usuario u = usuarioService.obtenerPorId(idUsuario).get();
        if(u == null) return ResponseEntity.status(404).body("Usuario no encontrado");
        try{
            usuarioService.actualizarUsuario(u.getCorreoElectronico(), usuarioDTO.nombreUsuario, usuarioDTO.correoElectronico, usuarioDTO.rol);
            usuarioService.actualizarContraseñaUsuario(u.getCorreoElectronico(), usuarioDTO.contrasena);
            usuarioService.guardar(u);
        }catch(Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok().body("Usuario actualizado correctamente");
    }

    //funciona
    @DeleteMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Object> eliminarUsuario(@PathVariable("idUsuario") long idUsuario) {
        usuarioService.eliminar(idUsuario);
        return ResponseEntity.ok().body("Usuario eliminado correctamente");
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}/tareas")
    public List<Tarea> getTareasPorUsuario(@PathVariable("idUsuario") long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario).map(Usuario::getTareas).orElse(List.of());
    }

    //funciona
    @PostMapping("/usuarios/{idUsuario}/tareas")
    public ResponseEntity<Object> crearTareaParaUsuario(@PathVariable("idUsuario") long idUsuario, @RequestBody TareaDTO tareaDTO){
        Usuario usuario = usuarioService.obtenerPorId(idUsuario).get();
        if(usuario == null){
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }
        try {
            Tarea nuevaTarea = new Tarea(tareaDTO.nombre, tareaDTO.descripcion, tareaDTO.dificultad);
            nuevaTarea.setUsuario(usuario);
            Tarea tareaGuardada = tareaService.guardar(nuevaTarea);
            usuario.getTareas().add(tareaGuardada);
            usuarioService.guardar(usuario);
            return ResponseEntity.status(201).body(tareaGuardada);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    //funciona
    //de momento se hace así porque no me funciona tareasCompletadas
    @GetMapping("/usuarios/{idUsuario}/tareas/completadas")
    public List<Tarea> getTareasCompletadasPorUsuario(@PathVariable("idUsuario") long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario).get().getTareas().stream()
                .filter(tarea -> tarea.getFechaCompletada() != null)
                .toList();
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
    public ResponseEntity<Object> getTareaPorNumYUsuario(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) {
        Usuario usuario = usuarioService.obtenerPorId(idUsuario).get();
        if(usuario == null) return ResponseEntity.status(404).body("Usuario no encontrado");
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) return ResponseEntity.status(404).body("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
        return ResponseEntity.ok().body(t);
    }

    //funciona
    @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/completar")
    public ResponseEntity<Object> completarTarea(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea){
        Usuario u = usuarioService.obtenerPorId(idUsuario).get();
        if(u == null) return ResponseEntity.status(404).body("Usuario no encontrado");
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) return ResponseEntity.status(404).body("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
        if(t.getFechaCompletada() != null) return ResponseEntity.status(400).body("La tarea ya ha sido completada");
        try{
            usuarioService.completarTarea(u.getCorreoElectronico(), t.getNombre());
        }catch(RegistroInvalidoException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
        return ResponseEntity.ok().body("Tarea completada correctamente");
    }

    //funciona
    @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/actualizar")
    public ResponseEntity<Object> actualizarTareaPorIdYUsuario(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea, @RequestBody TareaDTO tareaDTO){
        Usuario u = usuarioService.obtenerPorId(idUsuario).get();
        if(u == null) return ResponseEntity.status(404).body("Usuario no encontrado");
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) return ResponseEntity.status(404).body("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
        try{
            if(tareaDTO.nombre != null){
            t.setNombre(tareaDTO.nombre);
            }
            if(tareaDTO.descripcion != null){
                t.setDescripcion(tareaDTO.descripcion);
            }
            if(tareaDTO.dificultad != null){
                t.setExp(Dificultad.obtenerExpPorDificultad(tareaDTO.dificultad));
                t.setFechaExpiracion(Dificultad.obtenerDíasPorDificultad(tareaDTO.dificultad));
            }
        }catch(Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
        Tarea actualizada = tareaService.guardar(t);
        return ResponseEntity.ok().body(actualizada);
    }
    
    //funciona
    @DeleteMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
    public ResponseEntity<Object> borrarTareaPorIdYUsuario(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) throws RegistroInvalidoException {
        Usuario u = usuarioService.obtenerPorId(idUsuario).get();
        if(u == null) return ResponseEntity.status(404).body("Usuario no encontrado");
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) return ResponseEntity.status(404).body("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) return ResponseEntity.status(400).body("La tarea no pertenece al usuario especificado");
        try{
            usuarioService.eliminarTarea(u.getCorreoElectronico(), t.getNombre());
        }catch(RegistroInvalidoException e){
            return ResponseEntity.status(400).body(e.getMessage());
        }

        tareaService.eliminar(idTarea);
        return ResponseEntity.ok().body("Tarea eliminada correctamente");
    }
    //funciona
    @GetMapping("/tareas")
    public List<Tarea> getTareas(){
        return tareaService.obtenerTodas();
    }
}
