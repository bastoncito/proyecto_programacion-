package Michaelsoft_Binbows.util;

public class SistemaNiveles {
    /*
     * Recibe el nivel que se desea alcanzar 
     * Devuelve la experiencia necesaria para llegar a ese nivel
     * Dependiendo del nivel la experiencia necesaria es mayor (primeros niveles se subiran rapido, despues se llegara a un punto mas lento)
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
