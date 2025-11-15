package michaelsoftbinbows.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.temporal.ChronoUnit;

import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona la lógica de negocio de los logros.
 * Mantiene la definición estática de los logros y proporciona métodos
 * para verificar el progreso del usuario contra esa lógica.
 */
@Service
public class GestorLogrosService {

  private static final List<Logro> LOGROS_DISPONIBLES = new ArrayList<>();

  static {
    // --- Logros de Tareas ---
    LOGROS_DISPONIBLES.add(new Logro("JOIN_APP", "El primero es gratis", "Únete a Good Time", 0));
    LOGROS_DISPONIBLES.add(
        new Logro("COMPLETE_1_TASK", "Primeros Pasos", "Completa tu primera tarea.", 50));
    LOGROS_DISPONIBLES.add(
        new Logro("COMPLETE_10_TASKS", "Trabajador Incansable", "Completa 10 tareas.", 150));
    LOGROS_DISPONIBLES.add(
        new Logro("COMPLETE_50_TASKS", "Maestro de la Productividad", "Completa 50 tareas.", 300));
    
    // --- Logros de Nivel ---
    LOGROS_DISPONIBLES.add(new Logro("REACH_LEVEL_5", "Aprendiz", "Alcanza el nivel 5.", 100));
    LOGROS_DISPONIBLES.add(new Logro("REACH_LEVEL_20", "Veterano", "Alcanza el nivel 20.", 200));
    LOGROS_DISPONIBLES.add(new Logro("REACH_LEVEL_35", "Maestro", "Alcanza el nivel 35.", 500));
    
    // --- Logros de Racha ---
    LOGROS_DISPONIBLES.add(
        new Logro("7_DAY_STREAK", "Dedicación", "Mantén una racha por una semana", 150));
    LOGROS_DISPONIBLES.add(
        new Logro("30_DAY_STREAK", "Maratón", "Mantén una racha por un mes", 500));
    
    // --- Logros de Tareas (Especiales) ---
    LOGROS_DISPONIBLES.add(
        new Logro("MORNING_TASK", "Madrugador", "Completa una tarea entre 6:00-8:00 AM", 50));
    LOGROS_DISPONIBLES.add(
        new Logro("CLOSE_CALL", "Uy.", "Termina una tarea 5 minutos antes de que expire", 50));
    
    // --- Logros de Ranking ---
    LOGROS_DISPONIBLES.add(
        new Logro(
            "TOP_10_RANKING", "Ejemplo a seguir", "Quédate en el top 10 por al menos un día", 500));
    
    // --- Logros de Liga ---
    LOGROS_DISPONIBLES.add(new Logro("REACH_GOLD", "Ascendido", "Alcanza la liga de Oro", 200));
    LOGROS_DISPONIBLES.add(new Logro("REACH_PLATINUM", "Élite", "Alcanza la liga de Platino", 350));
    LOGROS_DISPONIBLES.add(new Logro("REACH_DIAMOND", "Leyenda", "Alcanza la liga de Diamante", 500));

    verificarUnicidadDeIds();
  }

  /**
   * Verifica que todos los IDs de logros en la lista sean únicos.
   */
  private static void verificarUnicidadDeIds() {
    final Set<String> idsVistos = new HashSet<>();
    for (Logro logro : LOGROS_DISPONIBLES) {
      if (!idsVistos.add(logro.getId())) {
        throw new IllegalStateException(
            "Error de configuración: ID de logro duplicado -> " + logro.getId());
      }
    }
    System.out.println(
        "LOG: Verificación de logros completada. "
            + LOGROS_DISPONIBLES.size()
            + " logros únicos cargados.");
  }

  /**
   * Evalúa si un usuario cumple con las condiciones para desbloquear logros pendientes.
   */
  public List<Logro> verificarLogros(Usuario usuario) {
    List<Logro> nuevosLogrosDesbloqueados = new ArrayList<>();

    for (Logro logroPotencial : LOGROS_DISPONIBLES) {
      // Usamos la lista persistente (@ManyToMany)
      if (usuario.getLogros().contains(logroPotencial)) {
        continue;
      }

      if (condicionCumplida(usuario, logroPotencial)) {
        nuevosLogrosDesbloqueados.add(logroPotencial);
      }
    }
    return nuevosLogrosDesbloqueados;
  }

  /**
   * Lógica de condición (privada, usada por este servicio).
   */
  private boolean condicionCumplida(Usuario usuario, Logro logro) {
    final String idLogro = logro.getId();

    switch (idLogro) {
      // --- Lógica de Tareas (Conteo) ---
      case "JOIN_APP":
        return true;
      case "COMPLETE_1_TASK":
        return usuario.getTareasCompletadas().size() >= 1;
      case "COMPLETE_10_TASKS":
        return usuario.getTareasCompletadas().size() >= 10;
      case "COMPLETE_50_TASKS":
        return usuario.getTareasCompletadas().size() >= 50;
      
      // --- Lógica de Nivel ---
      case "REACH_LEVEL_5":
        return usuario.getNivelExperiencia() >= 5;
      case "REACH_LEVEL_20":
        return usuario.getNivelExperiencia() >= 20;
      case "REACH_LEVEL_35":
        return usuario.getNivelExperiencia() >= 35;
      
      // --- ¡LÓGICA IMPLEMENTADA! (Racha) ---
      case "7_DAY_STREAK":
        return usuario.getRacha() >= 7;
      case "30_DAY_STREAK":
        return usuario.getRacha() >= 30;
      
      // --- ¡LÓGICA IMPLEMENTADA! (Liga) ---
      case "REACH_GOLD":
        return usuario.getLiga().equals("Oro") 
            || usuario.getLiga().equals("Platino") 
            || usuario.getLiga().equals("Diamante");
      case "REACH_PLATINUM":
        return usuario.getLiga().equals("Platino") 
            || usuario.getLiga().equals("Diamante");
      case "REACH_DIAMOND":
        return usuario.getLiga().equals("Diamante");

      // --- ¡LÓGICA IMPLEMENTADA! (Tareas Especiales) ---
      case "MORNING_TASK":
        // Revisa si CUALQUIER tarea completada cumple la condición.
        return usuario.getTareasCompletadas().stream().anyMatch(t -> {
            if (t.getFechaCompletada() == null) return false;
            int hora = t.getFechaCompletada().getHour();
            // "Entre 6:00 y 8:00" = Hora 6 y Hora 7
            return hora >= 6 && hora < 8; 
        });

      case "CLOSE_CALL":
        // Revisa si CUALQUIER tarea completada cumple la condición.
        return usuario.getTareasCompletadas().stream().anyMatch(t -> {
            // Necesitamos ambas fechas para comparar
            if (t.getFechaCompletada() == null || t.getFechaExpiracion() == null) {
                return false;
            }
            // Calcula los minutos entre la completación y la expiración
            long minutosRestantes = ChronoUnit.MINUTES.between(
                t.getFechaCompletada(),
                t.getFechaExpiracion()
            );
            // El logro se da si los minutos restantes son 5, 4, 3, 2, 1, o 0.
            return minutosRestantes >= 0 && minutosRestantes <= 5;
        });

      // --- Lógica Pendiente ---
      case "TOP_10_RANKING":
        // Esta lógica es más compleja y probablemente requiera
        // guardar un historial de rankings.
        return false; // Implementación pendiente

      default:
        System.err.println("LOG: ID de logro no reconocido: " + idLogro);
        return false;
    }
  }

  /**
   * Devuelve una copia de la lista de todos los logros definidos en el sistema.
   */
  public List<Logro> getLogrosDisponibles() {
    return new ArrayList<>(LOGROS_DISPONIBLES);
  }

  /**
   * Calcula y devuelve TODOS los logros que un usuario ha cumplido.
   * Lo usaremos para contar cuántos logros tiene cada usuario para el Top 5.
   *
   * @param usuario El objeto Usuario a evaluar
   * @return Lista de todos los logros que cumplen la condición
   */
  public List<Logro> getTodosLogrosCumplidos(Usuario usuario) {
    List<Logro> logrosCumplidos = new ArrayList<>();
    // Iteramos sobre la lista estática
    for (Logro logroPotencial : LOGROS_DISPONIBLES) {
      // Verificamos la lógica de cada uno
      if (condicionCumplida(usuario, logroPotencial)) {
        logrosCumplidos.add(logroPotencial);
      }
    }
    return logrosCumplidos;
  }

  /**
   * Comprueba el estado de un usuario contra todos los logros
   * y desbloquea los que haya ganado.
   *
   * @param usuario El usuario que debe ser revisado.
   * @return true si se desbloqueó al menos un logro, false si no.
   */
  public boolean actualizarLogrosParaUsuario(Usuario usuario) {
    // 1. Llama a tu método existente para ver qué logros NUEVOS ha ganado
    List<Logro> nuevosLogrosDesbloqueados = this.verificarLogros(usuario);

    if (nuevosLogrosDesbloqueados.isEmpty()) {
      // No hay nada que hacer
      return false;
    }

    // 2. Si hay logros nuevos, los añadimos al usuario
    for (Logro nuevoLogro : nuevosLogrosDesbloqueados) {
      // 2a. Añadir a la lista persistente (@ManyToMany)
      usuario.getLogros().add(nuevoLogro);

      // 2b. Añadir la recompensa de XP del logro
      int xpActual = usuario.getExperiencia();
      int xpLogro = nuevoLogro.getPuntosRecompensa();
      usuario.setExperiencia(xpActual + xpLogro);
      
      // (Aquí también podrías recalcular el nivel si la XP subió)
      // SistemaNiveles.actualizarNivel(usuario);

      System.out.println("LOG: ¡'" + usuario.getNombreUsuario() + 
                         "' desbloqueó el logro: " + nuevoLogro.getNombre() + "!");
    }

    // 3. Devolvemos 'true' para que el servicio que llamó
    //    sepa que debe guardar al usuario.
    return true;
  }
}