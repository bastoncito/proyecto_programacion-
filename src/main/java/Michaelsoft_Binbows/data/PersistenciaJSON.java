package Michaelsoft_Binbows.data;

import Michaelsoft_Binbows.entities.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaJSON {
  private static final String ARCHIVO_JSON = "base_datos.json";
  private Gson gson;

  public PersistenciaJSON() {
    // Se define el formato
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // Serializador y deserializador para LocalDateTime
    // Cuando src es null, devuelve null. Si no, lo formatea como un JsonPrimitive (String JSON
    // simple)
    JsonSerializer<LocalDateTime> serializer1 =
        (LocalDateTime src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) -> {
          return src == null ? null : new JsonPrimitive(src.format(dateTimeFormatter));
        };
    JsonSerializer<LocalDate> serializer2 =
        (LocalDate src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) -> {
          return src == null ? null : new JsonPrimitive(src.format(dateFormatter));
        };
    // Cuando json es null, devuelve null. Si no, intenta parsear el String a un LocalDateTime
    // usando el formateador definido.
    JsonDeserializer<LocalDateTime> deserializer1 =
        (JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) -> {
          try {
            return json == null ? null : LocalDateTime.parse(json.getAsString(), dateTimeFormatter);
          } catch (Exception e) {
            throw new JsonParseException(e);
          }
        };
    JsonDeserializer<LocalDate> deserializer2 =
        (JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) -> {
          try {
            return json == null ? null : LocalDate.parse(json.getAsString(), dateFormatter);
          } catch (Exception e) {
            throw new JsonParseException(e);
          }
        };
    // Creamos una instancia de Gson adaptada para el uso de LocalDateTime.
    this.gson =
        new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, serializer1)
            .registerTypeAdapter(LocalDateTime.class, deserializer1)
            .registerTypeAdapter(LocalDate.class, serializer2)
            .registerTypeAdapter(LocalDate.class, deserializer2)
            .setPrettyPrinting()
            .create();
  }

  /** Guarda la lista actual de usuarios en el archivo JSON. Sobrescribe el archivo si ya existe. */
  public void guardarBaseDatos(List<Usuario> usuarios) {
    try (Writer writer = new FileWriter(ARCHIVO_JSON)) {
      // Gson se encarga de convertir la lista de objetos Java a formato JSON y escribirla.
      gson.toJson(usuarios, writer);
      System.out.println("Base de datos guardada correctamente en " + ARCHIVO_JSON);
    } catch (IOException e) {
      System.err.println("Error al guardar la base de datos: " + e.getMessage());
    }
  }

  /**
   * Carga la lista de usuarios desde el archivo JSON. Si el archivo no existe, devuelve una lista
   * vacía.
   */
  public List<Usuario> cargarUsuarios() {
    // Se utiliza FileReader en vez de getResourceAsStream para evitar problemas con rutas
    // relativas.
    File archivo = new File(ARCHIVO_JSON);
    if (!archivo.exists()) {
      System.out.println("LOG: Archivo '" + ARCHIVO_JSON + "' no encontrado.");
      return new ArrayList<>(); // Devolvemos una lista vacía.
    }
    try (Reader reader = new FileReader(ARCHIVO_JSON)) {
      Type tipoListaUsuarios = new TypeToken<ArrayList<Usuario>>() {}.getType();
      List<Usuario> usuariosCargados = gson.fromJson(reader, tipoListaUsuarios);

      // Si el archivo JSON está vacío, fromJson puede devolver null.
      if (usuariosCargados != null) {
        System.out.println(
            "LOG: "
                + usuariosCargados.size()
                + " usuarios cargados exitosamente desde el archivo de recursos.");
        return usuariosCargados;
      }
    } catch (IOException e) {
      // Este error ocurriría si hay un problema leyendo el flujo de datos.
      System.err.println(
          "ERROR: No se pudo leer el archivo de recursos '"
              + ARCHIVO_JSON
              + "'. Causa: "
              + e.getMessage());
    } catch (Exception e) {
      // Captura otros errores, como un JSON malformado.
      System.err.println(
          "ERROR: Hubo un problema al parsear el archivo JSON. Causa: " + e.getMessage());
    }
    // Si algo sale mal o el archivo está vacío, devolvemos una lista vacía.
    return new ArrayList<>();
  }
}
