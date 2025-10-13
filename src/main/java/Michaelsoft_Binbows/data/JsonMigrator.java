package Michaelsoft_Binbows.data;

import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.services.UsuarioService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JsonMigrator implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        // Solo migra si la base está vacía
        if (usuarioService.contarUsuarios() == 0) {
            Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                    LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, context) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                    LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, context) ->
                    new com.google.gson.JsonPrimitive(src.toString()))
                .create();

            try (FileReader reader = new FileReader("base_datos.json")) {
                List<Usuario> usuarios = gson.fromJson(reader, new TypeToken<List<Usuario>>(){}.getType());
                for (Usuario usuario : usuarios) {
                    //Verifica si ya existe un usuario con ese correo
                    if (usuarioService.obtenerPorCorreo(usuario.getCorreoElectronico()).isEmpty()) {
                        usuario.setId(null); //Deja que JPA genere el id
                        usuarioService.guardar(usuario);
                    }
                }
                System.out.println("Migración desde JSON completada.");
            } catch (Exception e) {
                System.out.println("Error leyendo o deserializando el archivo JSON: " + e.getMessage());
            }
        } else {
            System.out.println("La base de datos ya contiene usuarios. No se migra desde JSON.");
        }
    }
}
