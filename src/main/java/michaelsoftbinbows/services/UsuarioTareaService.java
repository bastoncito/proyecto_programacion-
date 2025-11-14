package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.ZoneId;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servicio para coordinar acciones complejas entre Usuarios y Tareas. POR COMPLETAR. */
@Service
@Transactional
public class UsuarioTareaService {
  @Autowired UsuarioService usuarioService;
  @Autowired TareaService tareaService;

  public void completarTarea(Long usuarioId, Long tareaId) throws RegistroInvalidoException {
    Usuario u = usuarioService.obtenerPorId(usuarioId).get();
    Tarea tarea = tareaService.obtenerPorId(tareaId).get();
    if (!tarea.getUsuario().getId().equals(u.getId())) {
      throw new IllegalArgumentException("La tarea no pertenece al usuario");
    }
    if (tarea.getFechaCompletada() != null) {
      throw new RegistroInvalidoException("La tarea ya ha sido completada");
    }
    tarea.setFechaCompletada(java.time.LocalDateTime.now(ZoneId.systemDefault()));
    int expTarea = tarea.getExp();
    usuarioService.actualizarRacha(u);
    usuarioService.sumarExperienciaTarea(u, expTarea);
    usuarioService.verificarSubidaDeNivel(u);
    usuarioService.actualizarLigaDelUsuario(u);
    System.out.println(
        "¡'"
            + u.getNombreUsuario()
            + "' ha completado la tarea '"
            + tarea.getNombre()
            + "' y ha ganado "
            + expTarea
            + " de experiencia!");
    System.out.println("Experiencia total: " + u.getExperiencia());

    u.setPuntosLiga(u.getPuntosLiga() + expTarea);
    tareaService.guardar(tarea);
    // Persistimos también los cambios en el usuario (racha, experiencia, puntosLiga)
    usuarioService.guardarEnBd(u);
  }
}
