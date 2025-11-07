package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import michaelsoftbinbows.data.SalonFamaRepository;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.entities.SalonFama;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar la lógica de las temporadas, incluyendo el reseteo automático
 * mensual de puntos de liga.
 */
@Service
public class TemporadaService {

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private SalonFamaRepository salonFamaRepository;

  /**
   * El reseteo automático. El 'cron' está puesto para ejecutarse a las 00:00 del día 1 de cada mes.
   * * Para testear, cambiar temporalmente a: "@Scheduled(cron = "0 * * * * ?")" <-- (se ejecuta
   * CADA MINUTO)
   */
  @Transactional
  @Scheduled(cron = "0 0 0 1 * ?")
  public void gestionarReseteoTemporada() {
    System.out.println("--- INICIANDO RESETEO DE TEMPORADA AUTOMÁTICO ---");

    // Borramos a los ganadores del mes.
    salonFamaRepository.deleteAll();

    // Buscamos a los 3 mejores jugadores de ESTA temporada
    List<Usuario> ganadores = usuarioRepository.findTop3ByOrderByPuntosLigaDesc();

    // Generamos el nombre del mes que ACABA de terminar (ej. "Octubre 2025")
    LocalDate mesPasado = LocalDate.now().minusMonths(1);
    // Locale("es", "ES") es para que ponga "Octubre" y no "October"
    String temporadaNombre =
        mesPasado.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    // Capitalizar: "octubre" -> "Octubre"
    temporadaNombre =
        temporadaNombre.substring(0, 1).toUpperCase()
            + temporadaNombre.substring(1)
            + " "
            + mesPasado.getYear();

    // Guardamos a los 3 ganadores en la nueva tabla 'SalonFama'
    int puesto = 1;
    for (Usuario ganador : ganadores) {
      SalonFama registro =
          new SalonFama(
              puesto,
              temporadaNombre,
              ganador.getNombreUsuario(),
              ganador.getPuntosLiga(), // Sus puntos de este mes
              ganador.getLiga());
      salonFamaRepository.save(registro);
      puesto++;
    }
    System.out.println("--- SALÓN DE LA FAMA GUARDADO (" + ganadores.size() + " jugadores) ---");

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
