package michaelsoftbinbows.util;

/**
 * Clase de utilidad que maneja la lógica de cálculo de experiencia (EXP)
 * necesaria para subir de nivel.
 */
public class SistemaNiveles {
  
  /**
   * Recibe el nivel que se desea alcanzar y devuelve la experiencia necesaria para ello.
   * La fórmula cambia para que los primeros niveles sean más rápidos
   * y los niveles posteriores requieran más esfuerzo.
   *
   * @param nivelObjetivo El nivel que se desea alcanzar.
   * @return La cantidad de experiencia (EXP) necesaria para ese nivel.
   */
  public static int experienciaParaNivel(int nivelObjetivo) {
    if (nivelObjetivo <= 15) {
      return 40 * nivelObjetivo + 50;
    } else if (nivelObjetivo <= 30) {
      return 80 * nivelObjetivo - 200;
    } else {
      return 100 * nivelObjetivo - 500;
    }
  }
}
