package michaelsoftbinbows.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Clase de utilidad para gestionar la lógica relacionada con la dificultad de las tareas, como la
 * experiencia (exp) que otorgan o las fechas de expiración.
 */
public class Dificultad {
  /*
   * 1 - 10
   * 2 - 25
   * 3 - 50
   * 4 - 100
   * 5 - 150
   * 7 - 250
   */
  // ARREGLO: Renombrado de obtenerIDPorDificultad a obtenerIdPorDificultad
  public static int obtenerIdPorDificultad(String dificultad) {
    return switch (dificultad) {
      case "Muy fácil" -> 1;
      case "Fácil" -> 2;
      case "Medio" -> 3;
      case "Difícil" -> 4;
      case "Muy difícil" -> 5;
      default -> -1;
    };
  }

  /**
   * Obtiene la cantidad de experiencia (EXP) basada en la dificultad de una tarea.
   *
   * @param dificultad El nombre de la dificultad (ej. "Fácil").
   * @return La cantidad de EXP correspondiente.
   */
  public static int obtenerExpPorDificultad(String dificultad) {
    // ARREGLO: Renombrado de ID a id y llamada al método corregido.
    int id = obtenerIdPorDificultad(dificultad);
    return switch (id) { // ARREGLO: uso de 'id'
      case 1 -> 10;
      case 2 -> 25;
      case 3 -> 50;
      case 4 -> 100;
      case 5 -> 150;
      default -> 0;
    };
  }

  /**
   * Calcula la fecha de expiración de una tarea sumando días según su dificultad.
   *
   * @param dificultad El nombre de la dificultad.
   * @return La fecha y hora de expiración (LocalDateTime).
   */
  public static LocalDateTime obtenerDiasPorDificultad(String dificultad) {
    // ARREGLO: Renombrado de ID a id y llamada al método corregido.
    int id = obtenerIdPorDificultad(dificultad);
    LocalDateTime ahora = LocalDateTime.now(ZoneId.systemDefault());
    return ahora.plusDays(id); // ARREGLO: uso de 'id'
  }

  /**
   * Obtiene el nombre de la dificultad (String) basado en la cantidad de EXP.
   *
   * @param exp La cantidad de experiencia.
   * @return El nombre de la dificultad correspondiente.
   */
  public static String obtenerDificultadPorExp(int exp) {
    return switch (exp) {
      case 10 -> "Muy fácil";
      case 25 -> "Fácil";
      case 50 -> "Medio";
      case 100 -> "Difícil";
      case 150 -> "Muy difícil";
      default -> throw new IllegalArgumentException(
          "Experiencia no corresponde a ninguna dificultad: " + exp);
    };
  }
}
