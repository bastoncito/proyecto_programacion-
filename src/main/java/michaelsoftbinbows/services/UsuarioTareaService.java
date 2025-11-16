package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.List;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UsuarioTareaService {
  @Autowired UsuarioService usuarioService;
  @Autowired TareaService tareaService;
  @Autowired GestorLogrosService gestorLogrosService;

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
}