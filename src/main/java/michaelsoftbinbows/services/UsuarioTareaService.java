package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioTareaService {
  @Autowired UsuarioService usuarioService;
  @Autowired TareaService tareaService;
  @Autowired GestorLogrosService gestorLogrosService;

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

    // --- 1. APLICAR CAMBIOS INICIALES DE LA TAREA ---
    tarea.setFechaCompletada(LocalDateTime.now(ZoneId.systemDefault()));
    int expTarea = tarea.getExp();

    // (La racha ya no se actualiza aquí, ¡correcto!)
    usuarioService.sumarExperienciaTarea(u, expTarea);
    u.setPuntosLiga(u.getPuntosLiga() + expTarea);

    // --- 2. RECALCULAR STATS (PRIMERA PASADA) ---
    usuarioService.verificarSubidaDeNivel(u);
    usuarioService.actualizarLigaDelUsuario(u);

    // --- 3. ¡BUCLE "while" ELIMINADO! ---
    // Llamamos al gatillo de logros UNA SOLA VEZ.
    // Este método (actualizarLogrosParaUsuario) revisará y añadirá
    // todos los logros que el usuario cumpla en este momento
    // (incluyendo Tareas, Nivel y Liga).
    gestorLogrosService.actualizarLogrosParaUsuario(u);

    // --- 4. RECALCULAR STATS (SEGUNDA PASADA) ---
    // ¡IMPORTANTE! Volvemos a verificar la subida de nivel
    // DESPUÉS de que los logros hayan dado su propia XP.
    usuarioService.verificarSubidaDeNivel(u);
    // También recalculamos la liga, por si la XP del logro
    // fue suficiente para subir de liga (aunque los puntos no cuenten).
    usuarioService.actualizarLigaDelUsuario(u);

    // --- 5. GUARDADO FINAL ---
    System.out.println(
        "¡'"
            + u.getNombreUsuario()
            + "' ha completado la tarea '"
            + tarea.getNombre()
            + "' y ha ganado "
            + expTarea
            + " de experiencia!");
    System.out.println("Experiencia total: " + u.getExperiencia());

    tareaService.guardar(tarea);
    // Guarda al usuario con su nueva XP, Nivel, Liga Y todos los logros desbloqueados
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
