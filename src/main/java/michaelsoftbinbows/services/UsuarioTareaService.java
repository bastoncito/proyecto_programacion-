package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.ZoneId;
import java.time.LocalDateTime; // Importar LocalDateTime
import java.util.List; // Importar List
import michaelsoftbinbows.entities.Logro; // Importar Logro
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

    usuarioService.actualizarRacha(u);
    usuarioService.sumarExperienciaTarea(u, expTarea);
    u.setPuntosLiga(u.getPuntosLiga() + expTarea);

    // Recalcular stats por primera vez
    usuarioService.verificarSubidaDeNivel(u);
    usuarioService.actualizarLigaDelUsuario(u);

    // --- 2. ¡NUEVO! BUCLE DE REACCIÓN EN CADENA DE LOGROS ---
    while (true) {
      // Revisa si hay logros nuevos con los stats actuales
      // (Aquí usamos 'verificarLogros' en lugar de 'actualizarLogros', 
      //  ya que 'actualizarLogros' como lo hicimos antes causaría un bucle infinito)
      
      // Corregimos el plan anterior: Usamos verificarLogros.
      List<Logro> nuevosLogros = gestorLogrosService.verificarLogros(u);

      if (nuevosLogros.isEmpty()) {
        // Si no hay logros nuevos, la cadena se detiene. Salir del bucle.
        break;
      }

      // Si hay logros nuevos, aplicamos sus recompensas
      for (Logro nuevoLogro : nuevosLogros) {
        System.out.println("LOG: ¡'" + u.getNombreUsuario() + 
                         "' desbloqueó el logro: " + nuevoLogro.getNombre() + "!");
                         
        u.getLogros().add(nuevoLogro); // Añadir a la lista persistente
        
        // Añadimos la XP del logro
        usuarioService.sumarExperienciaTarea(u, nuevoLogro.getPuntosRecompensa());
      }

      // Como la XP cambió, debemos RE-VERIFICAR el nivel y la liga
      // antes de que el bucle se repita.
      usuarioService.verificarSubidaDeNivel(u);
      usuarioService.actualizarLigaDelUsuario(u);

      // El bucle 'while(true)' se repite para ver si este NUEVO nivel/liga desbloquea OTRO logro.
    }
    
    // --- 3. GUARDADO FINAL ---
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
    usuarioService.guardarEnBd(u); // Guarda todo: XP, nivel, liga Y todos los logros
  }
}