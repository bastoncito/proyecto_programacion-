package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.data.UsuarioRepository;
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

import org.hibernate.mapping.Index;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public Usuario crearUsuario(@RequestBody UsuarioDTO usuarioDTO) throws EdicionInvalidaException, RegistroInvalidoException {
        Usuario usuario = new Usuario(usuarioDTO.nombreUsuario, usuarioDTO.correoElectronico, usuarioDTO.contrasena);
        return usuarioService.guardar(usuario);
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}")
    public Optional<Usuario> getUsuarioPorId(@PathVariable("idUsuario") long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario);
    }
    
    //Provisorio
    //funciona!
    @PutMapping("/usuarios/{idUsuario}")
    public ResponseEntity<Object> actualizarUsuario(@PathVariable("idUsuario") long idUsuario, @RequestBody UsuarioDTO usuarioDTO) throws EdicionInvalidaException, RegistroInvalidoException {
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
    public void eliminarUsuario(@PathVariable("idUsuario") long idUsuario) {
        usuarioService.eliminar(idUsuario);
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}/tareas")
    public List<Tarea> getTareasPorUsuario(@PathVariable("idUsuario") long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario).map(Usuario::getTareas).orElse(List.of());
    }

    //funciona
    @PostMapping("/usuarios/{idUsuario}/tareas")
    public Tarea crearTareaParaUsuario(@PathVariable("idUsuario") long idUsuario, @RequestBody TareaDTO tareaDTO) throws EdicionInvalidaException, RegistroInvalidoException, TareaInvalidaException {
        Tarea nuevaTarea = new Tarea(tareaDTO.nombre, tareaDTO.descripcion, tareaDTO.dificultad);
        return usuarioService.obtenerPorId(idUsuario).map(usuario -> {
            nuevaTarea.setUsuario(usuario);
            try {
                Tarea tareaGuardada = tareaService.guardar(nuevaTarea);
                usuario.getTareas().add(tareaGuardada);
                usuarioService.guardar(usuario);
                return tareaGuardada;
            } catch (EdicionInvalidaException | RegistroInvalidoException e) {
                throw new RuntimeException(e);
            }
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}/tareas/completadas")
    public List<Tarea> getTareasCompletadasPorUsuario(@PathVariable("idUsuario") long idUsuario) {
        return usuarioService.obtenerPorId(idUsuario).get().getTareas().stream()
                .filter(tarea -> tarea.getFechaCompletada() != null)
                .toList();
    }

    //funciona
    @GetMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
    public Tarea getTareaPorNumYUsuario(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) {
        usuarioService.obtenerPorId(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) throw new RuntimeException("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) throw new RuntimeException("La tarea no pertenece al usuario especificado");
        return t;
    }

    //funciona
    @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/completar")
    public Tarea completarTarea(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) throws EdicionInvalidaException, RegistroInvalidoException{
        Usuario u = usuarioService.obtenerPorId(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) throw new RuntimeException("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) throw new RuntimeException("La tarea no pertenece al usuario especificado");
        if(t.getFechaCompletada() != null) throw new RuntimeException("La tarea ya ha sido completada");
        usuarioService.completarTarea(u.getCorreoElectronico(), t.getNombre());
        return t;
    }

    //funciona
    @PutMapping("/usuarios/{idUsuario}/tareas/{idTarea}/actualizar")
    public Tarea actualizarTareaPorIdYUsuario(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea, @RequestBody TareaDTO tareaDTO) throws EdicionInvalidaException, RegistroInvalidoException, TareaInvalidaException {
        usuarioService.obtenerPorId(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) throw new RuntimeException("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) throw new RuntimeException("La tarea no pertenece al usuario especificado");
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
        return tareaService.guardar(t);
    }
    
    //funciona
    @DeleteMapping("/usuarios/{idUsuario}/tareas/{idTarea}")
    public void borrarTareaPorIdYUsuario(@PathVariable("idUsuario") long idUsuario, @PathVariable("idTarea") long idTarea) throws EdicionInvalidaException, RegistroInvalidoException {
        Usuario u = usuarioService.obtenerPorId(idUsuario).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Tarea t = tareaService.obtenerPorId(idTarea).get();
        if(t == null) throw new RuntimeException("Tarea no encontrada");
        if(t.getUsuario().getId() != idUsuario) throw new RuntimeException("La tarea no pertenece al usuario especificado");
        usuarioService.eliminarTarea(u.getCorreoElectronico(), t.getNombre());
        tareaService.eliminar(idTarea);
    }
    //funciona
    @GetMapping("/tareas")
    public List<Tarea> getTareas(){
        return tareaService.obtenerTodas();
    }
}
