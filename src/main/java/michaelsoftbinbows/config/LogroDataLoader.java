package michaelsoftbinbows.config;

import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.services.GestorLogrosService;
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
    @Autowired
    private GestorLogrosService gestorLogrosService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("LOG: Sincronizando logros de GestorLogros con la BD...");
        
        List<Logro> logrosEstaticos = gestorLogrosService.getLogrosDisponibles();
        int logrosNuevos = 0;
        int logrosActualizados = 0;

        for (Logro logroEstatico : logrosEstaticos) {
            Optional<Logro> logroEnBD_Opt = logroService.obtenerPorId(logroEstatico.getId());

            if (logroEnBD_Opt.isEmpty()) {
                // El logro no existe en la BD, lo guardamos
                logroService.guardar(logroEstatico);
                logrosNuevos++;
            } else {
                // El logro ya existe, actualizamos sus datos por si cambiaron en el código
                // PERO mantenemos su estado 'activo' (que es lo que maneja el admin)
                Logro logroEnBD = logroEnBD_Opt.get();
                boolean estadoActivo = logroEnBD.isActivo(); // Guardamos el estado del admin
                String imagenUrlAdmin = logroEnBD.getImagenUrl();
                
                // Actualizamos desde el código estático
                logroEnBD.setNombre(logroEstatico.getNombre());
                logroEnBD.setDescripcion(logroEstatico.getDescripcion());
                // ... (cualquier otro campo de GestorLogros)
                
                logroEnBD.setActivo(estadoActivo); // Restauramos el estado del admin
                logroEnBD.setImagenUrl(imagenUrlAdmin);
                logroService.guardar(logroEnBD);
                logrosActualizados++;
            }
        }

        System.out.println(
            "LOG: Sincronización de logros completada. " + 
            logrosNuevos + " nuevos, " + 
            logrosActualizados + " actualizados."
        );
    }
}