package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Servicio para coordinar acciones complejas entre Usuarios y Tareas. POR COMPLETAR. */
@Service
public class UsuarioTareaService {
  @Autowired UsuarioService usuarioService;
  @Autowired TareaService tareaService;

  @Transactional
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

  /**
   * Verifica y elimina las tareas expiradas de un usuario.
   *
   * @param usuario El usuario cuyas tareas se van a verificar.
   */
  @Transactional
  public void verificarTareasExpiradas(Long idUsuario) {
    LocalDateTime hoy = LocalDateTime.now(ZoneId.systemDefault());
    Usuario usuario = usuarioService.obtenerPorId(idUsuario).orElse(null);
    if (usuario == null) {
      System.out.println("LOG: Usuario con id " + idUsuario + " no encontrado.");
      return;
    }

    // Iteramos directamente sobre la lista de tareas del usuario usando un iterador
    // para poder eliminar elementos de forma segura. Con `orphanRemoval = true` en la
    // entidad `Usuario.tareas`, eliminar de la colección y guardar el usuario provocará
    // que JPA borre las tareas huérfanas de la base de datos.
    var iterator = usuario.getTareas().iterator();
    boolean cambios = false;
    while (iterator.hasNext()) {
      Tarea tarea = iterator.next();
      // Solo consideramos tareas pendientes con fecha de expiración
      if (tarea.getFechaCompletada() == null
          && tarea.getFechaExpiracion() != null
          && tarea.getFechaExpiracion().isBefore(hoy)) {
        System.out.println(
            "LOG: La tarea '"
                + tarea.getNombre()
                + "' del usuario '"
                + usuario.getNombreUsuario()
                + "' ha expirado y será eliminada.");
        // Eliminamos de la colección; JPA eliminará la fila al guardar el usuario
        iterator.remove();
        // Rompemos la relación bidireccional por seguridad
        tarea.setUsuario(null);
        cambios = true;
      }
    }

    if (cambios) {
      try {
        usuarioService.guardarEnBd(usuario);
      } catch (Exception e) {
        // Evitamos que una excepción detenga el proceso global; registramos para diagnóstico
        System.out.println(
            "ERROR al guardar usuario tras eliminar tareas expiradas: " + e.getMessage());
        // Re-throw if you want the transaction to roll back. Por ahora solo logueamos.
      }
    }
  }
}
