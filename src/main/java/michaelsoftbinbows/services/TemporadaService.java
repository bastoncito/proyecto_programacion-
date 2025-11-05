package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.util.List;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TemporadaService {

  @Autowired private UsuarioRepository usuarioRepository;

  /**
   * El reseteo automático. El 'cron' está puesto para ejecutarse a las 00:00 del día 1 de cada mes.
   * * Para testear, cambiar temporalmente a: "@Scheduled(cron = "0 * * * * ?")" <-- (se ejecuta
   * CADA MINUTO)
   */
  @Transactional
  @Scheduled(cron = "0 0 0 1 * ?")
  public void gestionarReseteoTemporada() {
    System.out.println("--- INICIANDO RESETEO DE TEMPORADA AUTOMÁTICO ---");

    // 1. Buscamos a TODOS los usuarios
    List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

    // 2. Reseteamos los puntos
    for (Usuario usuario : todosLosUsuarios) {
      usuario.resetearPuntosLiga();
    }

    // 3. Guardamos todos los cambios en la base de datos
    usuarioRepository.saveAll(todosLosUsuarios);

    System.out.println("--- RESETEO DE TEMPORADA COMPLETADO ---");
  }

  /**
   * Esta es la función que llamará el Admin para probar. Simplemente llama al mismo método de
   * reseteo.
   */
  @Transactional
  public void forzarReseteoManual() {
    System.out.println("--- INICIANDO RESETEO DE TEMPORADA MANUAL (ADMIN) ---");
    // Reutilizamos la misma lógica
    gestionarReseteoTemporada();
  }
}
