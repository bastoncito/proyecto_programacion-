package michaelsoftbinbows.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.entities.Usuario;

/**
 * Clase de utilidad para gestionar la lógica y la definición de los Logros. Mantiene una lista
 * estática de todos los logros disponibles en el sistema.
 */
public class GestorLogros {

  private static final List<Logro> LOGROS_DISPONIBLES = new ArrayList<>();

  static {
    LOGROS_DISPONIBLES.add(new Logro("JOIN_APP", "El primero es gratis", "Únete a Good Time", 0));
    LOGROS_DISPONIBLES.add(
        new Logro("COMPLETE_1_TASK", "Primeros Pasos", "Completa tu primera tarea.", 50));
    LOGROS_DISPONIBLES.add(
        new Logro("COMPLETE_10_TASKS", "Trabajador Incansable", "Completa 10 tareas.", 150));
    LOGROS_DISPONIBLES.add(
        new Logro("COMPLETE_50_TASKS", "Maestro de la Productividad", "Completa 50 tareas.", 300));
    LOGROS_DISPONIBLES.add(new Logro("REACH_LEVEL_5", "Aprendiz", "Alcanza el nivel 5.", 100));
    LOGROS_DISPONIBLES.add(new Logro("REACH_LEVEL_20", "Veterano", "Alcanza el nivel 20.", 200));
    LOGROS_DISPONIBLES.add(new Logro("REACH_LEVEL_35", "Maestro", "Alcanza el nivel 35.", 500));
    LOGROS_DISPONIBLES.add(
        new Logro("7_DAY_STREAK", "Dedicación", "Mantén una racha por una semana", 150));
    LOGROS_DISPONIBLES.add(
        new Logro("30_DAY_STREAK", "Maratón", "Mantén una racha por un mes", 500));
    LOGROS_DISPONIBLES.add(
        new Logro("MORNING_TASK", "Madrugador", "Completa una tarea entre 6:00-8:00 AM", 50));
    LOGROS_DISPONIBLES.add(
        new Logro("CLOSE_CALL", "Uy.", "Termina una tarea 5 minutos antes de que expire", 50));
    LOGROS_DISPONIBLES.add(
        new Logro(
            "TOP_10_RANKING", "Ejemplo a seguir", "Quédate en el top 10 por al menos un día", 500));

    verificarUnicidadDeIds();
  }

  /**
   * Verifica que todos los IDs de logros en la lista sean únicos. Este método se ejecuta una vez
   * durante la inicialización de la clase. Si se detectan IDs duplicados, lanza una excepción para
   * prevenir inconsistencias.
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
   * Evalúa si un usuario cumple con las condiciones para desbloquear logros pendientes. Compara el
   * progreso del usuario contra todos los logros disponibles y retorna aquellos que el usuario ha
   * desbloqueado recientemente.
   *
   * @param usuario El objeto Usuario cuyo progreso se va a evaluar
   * @return Lista de logros recién desbloqueados (puede estar vacía si no hay nuevos)
   */
  public List<Logro> verificarLogros(Usuario usuario) {
    List<Logro> nuevosLogrosDesbloqueados = new ArrayList<>();

    for (Logro logroPotencial : LOGROS_DISPONIBLES) {
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
   * Determina si un usuario específico cumple los requisitos para un logro dado. Este método
   * centraliza la lógica de evaluación de condiciones para mantener el código modular y extensible.
   *
   * @param usuario El usuario cuya progresión se evalúa
   * @param logro El logro cuyas condiciones se verifican
   * @return true si el usuario cumple todas las condiciones del logro, false en caso contrario
   */
  private boolean condicionCumplida(Usuario usuario, Logro logro) {
    final String idLogro = logro.getId();

    switch (idLogro) {
      case "JOIN_APP":
        return true; // Siempre se cumple al unirse
      case "COMPLETE_1_TASK":
        return usuario.getTareasCompletadas().size() >= 1;
      case "COMPLETE_10_TASKS":
        return usuario.getTareasCompletadas().size() >= 10;
      case "COMPLETE_50_TASKS":
        return usuario.getTareasCompletadas().size() >= 50;
      case "REACH_LEVEL_5":
        return usuario.getNivelExperiencia() >= 5;
      case "REACH_LEVEL_20":
        return usuario.getNivelExperiencia() >= 20;
      case "REACH_LEVEL_35":
        return usuario.getNivelExperiencia() >= 35;
      case "7_DAY_STREAK":
        // return si usuario tiene racha de 7+ dias
      case "30_DAY_STREAK":
        // return si usuario tiene racha de 30+ dias
      case "MORNING_TASK":
        // return si se completó una tarea entre las 6 y 8
      case "CLOSE_CALL":
        // return si una tarea fue completada 5 minutos antes de expirar
      case "TOP_10_RANKING":
        // return si usuario estuvo en top 10 al menos un dia
      default:
        System.err.println("LOG: ID de logro no reconocido: " + idLogro);
        return false;
    }
  }
}
