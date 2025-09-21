//POR HACER: Manejo centralizado de validaciones (eventualmente migrar los métodos de validaciones a una clase aparte o algo así)
package Michaelsoft_Binbows.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

//Con esta línea, volvemos a esta clase un Spring Bean, que será gestionado por el framework e inyectado en los controladores.
//De esta manera nos evitamos crear nuevas instancias en cada controlador, y los conflictos que surjan a partir de esto.
@Service
public class BaseDatos {
    private List<Usuario> usuarios;
    private static final String ARCHIVO_JSON = "base_datos.json";
    private Gson gson;

    public BaseDatos() {
        // Creamos una instancia de Gson. setPrettyPrinting() hace que el archivo JSON sea legible. (lo hara con tabulaciones, en vez de una sola linea)
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.usuarios = cargarUsuarios();
    }

    /**
     * Carga la lista de usuarios desde el archivo JSON.
     * Si el archivo no existe, devuelve una lista vacía.
     */
public List<Usuario> cargarUsuarios() {
    // InputStream es la forma correcta de leer un archivo desde los recursos.
    // ClassLoader es el "cargador" de la aplicación, le pedimos que encuentre el archivo en su "mochila".
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(ARCHIVO_JSON)) {

        // Si getResourceAsStream devuelve null, el archivo NO existe en los recursos.
        if (inputStream == null) {
            System.out.println("LOG: Archivo '" + ARCHIVO_JSON + "' no encontrado en src/main/resources.");
            return new ArrayList<>(); // Devolvemos una lista vacía.
        }

        // Convertimos el flujo de bytes (InputStream) en un flujo de caracteres (Reader) que Gson puede usar.
        try (Reader reader = new InputStreamReader(inputStream)) {
            Type tipoListaUsuarios = new TypeToken<ArrayList<Usuario>>() {}.getType();
            List<Usuario> usuariosCargados = gson.fromJson(reader, tipoListaUsuarios);

            // Si el archivo JSON está vacío, fromJson puede devolver null.
            if (usuariosCargados != null) {
                System.out.println("LOG: " + usuariosCargados.size() + " usuarios cargados exitosamente desde el archivo de recursos.");
                return usuariosCargados;
            }
        }

    } catch (IOException e) {
        // Este error ocurriría si hay un problema leyendo el flujo de datos.
        System.err.println("ERROR: No se pudo leer el archivo de recursos '" + ARCHIVO_JSON + "'. Causa: " + e.getMessage());
    } catch (Exception e) {
        // Captura otros errores, como un JSON malformado.
        System.err.println("ERROR: Hubo un problema al parsear el archivo JSON. Causa: " + e.getMessage());
    }

    // Si algo sale mal o el archivo está vacío, devolvemos una lista vacía.
    return new ArrayList<>();
}

    /**
     * Guarda la lista actual de usuarios en el archivo JSON.
     * Sobrescribe el archivo si ya existe.
     */
    public void guardarBaseDatos() {
        try (Writer writer = new FileWriter(ARCHIVO_JSON)) {
            // Gson se encarga de convertir la lista de objetos Java a formato JSON y escribirla.
            gson.toJson(usuarios, writer);
            System.out.println("Base de datos guardada correctamente en " + ARCHIVO_JSON);
        } catch (IOException e) {
            System.err.println("Error al guardar la base de datos: " + e.getMessage());
        }
    }

    public List<Usuario> getUsuarios() { 
        return new ArrayList<>(usuarios); // Devuelve una copia para evitar modificaciones externas
    }
    
    /**
     * Recibe un usuario como parametro
     * lo agrega a la base de datos si su nombre y correo no estan registrados
     */
    private String validarUsuario(Usuario usuario) {//Trabajar aqui--->>>Hacer restricciones para agregar usuarios----->>>CAMBIOS REALIZADOS
        if (usuario == null) {
            return "Error: El usuario no puede ser nulo.";
        }
        // Validar nombre
        if (usuarioExistePorNombre(usuario.getNombre_usuario())) {
            return "Ya existe un usuario con el nombre: " + usuario.getNombre_usuario();
        }

        // Validar correo
        if (usuarioExistePorCorreo(usuario.getCorreo_electronico())) {
            return "Ya existe un usuario con el correo: " + usuario.getCorreo_electronico();
        }
        // Si pasa las validaciones, lo agregamos
        return null;
    }

    public void agregarUsuario(Usuario usuario){
        String valido = validarUsuario(usuario);
        if(valido == null){
            usuarios.add(usuario);
            System.out.println("Usuario '" + usuario.getNombre_usuario() + "' agregado exitosamente.");
        }else{
            System.err.println(valido);
        }
    }

    /**
     * Recibe un String correo
     * Devuelve si el correo existe en la base de datos
     */
    public boolean usuarioExistePorCorreo(String correo) {
        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getCorreo_electronico().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Recibe un String nombre
     * Devuelve true si el nombre existe en la base de datos, false en caso opuesto
     */
    public boolean usuarioExistePorNombre(String nombre){
        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getNombre_usuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    public void imprimirTodosUsuarios() {
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios en la base de datos.");
            return;
        }
        
        System.out.println("\n--- USUARIOS EN BASE DE DATOS ---");
        
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            System.out.println("\nUSUARIO #" + (i + 1));
            System.out.println("  Nombre: " + u.getNombre_usuario());
            System.out.println("  Email: " + u.getCorreo_electronico());
            
            if (u.getTareas() == null || u.getTareas().isEmpty()) {
                System.out.println("  Tareas: 0");
            } else {
                System.out.println("  Tareas (" + u.getTareas().size() + "):");
                for (Tarea t : u.getTareas()) {
                    System.out.println("    - " + t.getNombre() + " (EXP: " + t.getExp() + ")");
                }
            }
        }
        System.out.println("--------------------------");
    }
}
