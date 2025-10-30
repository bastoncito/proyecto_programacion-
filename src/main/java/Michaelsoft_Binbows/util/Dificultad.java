package Michaelsoft_Binbows.util;

import java.time.LocalDateTime;

public class Dificultad {
  /*
   * 1 - 10
   * 2 - 25
   * 3 - 50
   * 4 - 100
   * 5 - 150
   * 7 - 250
   */
  private static int obtenerIDPorDificultad(String dificultad) {
    return switch (dificultad) {
      case "Muy fácil" -> 1;
      case "Fácil" -> 2;
      case "Medio" -> 3;
      case "Difícil" -> 4;
      case "Muy difícil" -> 5;
      default -> throw new IllegalArgumentException("Dificultad no válida: " + dificultad);
    };
  }

  public static int obtenerExpPorDificultad(String dificultad) {
    int ID = obtenerIDPorDificultad(dificultad);
    return switch (ID) {
      case 1 -> 10;
      case 2 -> 25;
      case 3 -> 50;
      case 4 -> 100;
      case 5 -> 150;
      default -> 0;
    };
  }

  public static LocalDateTime obtenerDíasPorDificultad(String dificultad) {
    int ID = obtenerIDPorDificultad(dificultad);
    LocalDateTime ahora = LocalDateTime.now();
    return ahora.plusDays(ID);
  }

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
