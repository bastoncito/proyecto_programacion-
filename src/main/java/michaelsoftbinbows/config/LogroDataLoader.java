package michaelsoftbinbows.config;

import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.services.GestorLogrosService; // Importado
import michaelsoftbinbows.services.LogroService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * Esta clase se ejecuta una vez al iniciar la aplicación.
 * Su misión es sincronizar la lista estática de logros de GestorLogros
 * con la base de datos.
 */
@Component
public class LogroDataLoader implements CommandLineRunner {

    @Autowired
    private LogroService logroService;

    // ¡Inyectamos el Gestor de Logros!
    @Autowired
    private GestorLogrosService gestorLogrosService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("LOG: Sincronizando logros de GestorLogros con la BD...");
        
        // Obtenemos la lista de logros definidos en el código
        List<Logro> logrosEstaticos = gestorLogrosService.getLogrosDisponibles();
        
        int logrosNuevos = 0;
        int logrosActualizados = 0; // Cambiaremos esto, ya no actualizamos

        for (Logro logroEstatico : logrosEstaticos) {
            Optional<Logro> logroEnBD_Opt = logroService.obtenerPorId(logroEstatico.getId());

            // --- ¡LÓGICA CORREGIDA! ---

            // CASO 1: El logro NO existe en la BD.
            if (logroEnBD_Opt.isEmpty()) {
                // Lo creamos por primera vez.
                // Usamos el constructor de Logro para crear una nueva instancia
                // por si la de la lista estática es compartida.
                Logro nuevoLogro = new Logro(
                    logroEstatico.getId(),
                    logroEstatico.getNombre(),
                    logroEstatico.getDescripcion(),
                    logroEstatico.getPuntosRecompensa()
                );
                // (Este nuevo logro tendrá 'activo = true' e 'imagenUrl = null' por defecto)
                
                logroService.guardar(nuevoLogro);
                logrosNuevos++;
            } 
            
            // CASO 2: El logro SÍ existe en la BD.
            else {
                // NO HACEMOS NADA.
                // La versión en la BD (logroEnBD_Opt.get()) ya existe
                // y contiene las ediciones del admin (nombre, XP, activo, imagenUrl).
                // No la sobrescribimos.
            }
        }

        System.out.println(
            "LOG: Sincronización de logros completada. " + 
            logrosNuevos + " nuevos logros añadidos a la BD."
        );
    }
}