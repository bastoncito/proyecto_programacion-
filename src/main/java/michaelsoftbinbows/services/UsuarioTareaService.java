package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import michaelsoftbinbows.data.TareaRepository;
import michaelsoftbinbows.data.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servicio para coordinar acciones complejas entre Usuarios y Tareas. POR COMPLETAR. */
@Service
@Transactional
public class UsuarioTareaService {
  @Autowired UsuarioRepository usuarioRepository;
  @Autowired TareaRepository tareaRepository;
  /*

  Estas son acciones que requieren coordinacion del usuario y de la tarea,
  por lo que se tienen que ver en un servicio aparte.
  POR COMPLETAR

  public void completarTarea(Long usuarioId, Long tareaId) {
      Usuario u = usuarioRepository.findById(usuarioId).get();
      Tarea tarea = tareaRepository.findById(tareaId).get();
      if(u == null || tarea == null) throw new IllegalArgumentException
      ("Usuario o tarea no encontrados");
      if(tarea.getUsuario() != u) {
          throw new IllegalArgumentException("La tarea no pertenece al usuario");
      }
      tarea.setFechaCompletada(java.time.LocalDateTime.now(ZoneId.systemDefault()));
      u.completarTarea(null);
  }

  public void eliminarTarea(Long usuarioId, Long tareaId) {
      Usuario u = usuarioRepository.findById(usuarioId).
  */

}
