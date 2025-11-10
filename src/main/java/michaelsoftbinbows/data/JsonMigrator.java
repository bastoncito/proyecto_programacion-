package michaelsoftbinbows.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Lógica para migrar datos desde un JSON a la base de datos en PostgreSQL
 *
 * <p>Se ejecuta cada vez que se echa a andar la aplicación.
 *
 * <p>Si la base de datos ya contiene información, no hace nada.
 */
@Component
public class JsonMigrator implements CommandLineRunner {

  @Autowired private UsuarioService usuarioService;

  @Override
  public void run(String... args) throws Exception {
    // Solo migra si la base está vacía
    if (usuarioService.contarUsuarios() == 0) {
      Gson gson =
          new GsonBuilder()
              .registerTypeAdapter(
                  LocalDateTime.class,
                  (JsonDeserializer<LocalDateTime>)
                      (json, type, context) -> LocalDateTime.parse(json.getAsString()))
              .registerTypeAdapter(
                  LocalDateTime.class,
                  (JsonSerializer<LocalDateTime>)
                      (src, type, context) -> new com.google.gson.JsonPrimitive(src.toString()))
              .registerTypeAdapter(
                  LocalDate.class,
                  (JsonDeserializer<LocalDate>)
                      (json, type, context) -> LocalDate.parse(json.getAsString()))
              .registerTypeAdapter(
                  LocalDate.class,
                  (JsonSerializer<LocalDate>)
                      (src, type, context) -> new com.google.gson.JsonPrimitive(src.toString()))
              .create();

      try (Reader reader =
          Files.newBufferedReader(Paths.get("base_datos.json"), Charset.defaultCharset())) {
        List<Usuario> usuarios = gson.fromJson(reader, new TypeToken<List<Usuario>>() {}.getType());
        for (Usuario usuario : usuarios) {
          // Verifica si ya existe un usuario con ese correo
          if (usuarioService.obtenerPorCorreo(usuario.getCorreoElectronico()).isEmpty()) {
            usuario.setId(null); // Deja que JPA genere el id

            if (usuario.getTareas() != null) {
              usuario.getTareas().forEach(t -> t.setUsuario(usuario));
            }
            if (usuario.getTareasCompletadas() != null) {
              usuario.getTareasCompletadas().forEach(t -> t.setUsuario(usuario));
            }

            usuarioService.guardarConTareas(usuario);
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
