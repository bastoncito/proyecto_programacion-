//POR HACER: Manejo centralizado de validaciones (eventualmente migrar los métodos de validaciones a una clase aparte o algo así)

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        try (Reader reader = new FileReader(ARCHIVO_JSON)) {
            // TypeToken para decirle a Gson que queremos deserializar una lista de Usuarios (ArrayList<Usurario>)
            Type tipoListaUsuarios = new TypeToken<ArrayList<Usuario>>(){}.getType();
            
            List<Usuario> usuariosCargados = gson.fromJson(reader, tipoListaUsuarios);

            if (usuariosCargados != null) {
                System.out.println(usuariosCargados.size() + " usuarios cargados desde " + ARCHIVO_JSON);
                return usuariosCargados;
            }
        } catch (FileNotFoundException e) {
            // No existe json 
            System.out.println("Archivo " + ARCHIVO_JSON + " no encontrado. Se creará uno nuevo al guardar.");
        } catch (IOException e) {
            // No se pudo leer o escribir
            System.err.println("Error de E/S al leer el archivo: " + e.getMessage());
        } catch (Exception e) {
            // Json corrupto
            System.err.println("Error al parsear el archivo JSON: " + e.getMessage());
        }
        
        // Si pasa alguna de las excepciones se crea un Array List vacio
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
        if (usuarioExistePorNombre(usuario.getNombre_usuario()) != null) {
            return "Ya existe un usuario con el nombre: " + usuario.getNombre_usuario();
        }

        // Validar correo
        if (usuarioExistePorCorreo(usuario.getCorreo_electronico()) != null) {
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
    public Usuario usuarioExistePorCorreo(String correo) {
        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getCorreo_electronico().equalsIgnoreCase(correo)) {
                return usuarioExistente;
            }
        }
        return null;
    }
    /**
     * Recibe un String nombre
     * Devuelve true si el nombre existe en la base de datos, false en caso opuesto
     */
    public Usuario usuarioExistePorNombre(String nombre){
        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getNombre_usuario().equalsIgnoreCase(nombre)) {
                return usuarioExistente;
            }
        }
        return null;
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
