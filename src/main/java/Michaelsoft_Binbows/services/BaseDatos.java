package Michaelsoft_Binbows.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import Michaelsoft_Binbows.data.*;

@Service
public class BaseDatos {
    private List<Usuario> usuarios;
    private PersistenciaJSON persistencia;

    public BaseDatos() {
        this.persistencia = new PersistenciaJSON();
        cargarBaseDatos();
    }

    public void guardarBaseDatos() {
        this.persistencia.guardarBaseDatos(this.usuarios);
    }

    public void cargarBaseDatos() {
        this.usuarios = this.persistencia.cargarUsuarios();
    }

    public List<Usuario> getUsuarios() { 
        return new ArrayList<>(usuarios);
    }

    public void eliminarUsuario(Usuario usuario){
        // por si se intenta eliminar un usuario que no está en la lista.
        boolean fueEliminado = usuarios.removeIf(u -> u.getCorreoElectronico().equalsIgnoreCase(usuario.getCorreoElectronico()));
        if (fueEliminado) {
            System.out.println("El usuario '" + usuario.getNombreUsuario() + "' ha sido eliminado.");
            guardarBaseDatos();
        } else {
             throw new IllegalArgumentException("El usuario a eliminar no fue encontrado en la base de datos.");
        }
    }
    
    private String validarUsuario(Usuario usuario) {
        if (usuario == null) {
            return "Error: El usuario no puede ser nulo.";
        }
        if (usuarioExistePorNombre(usuario.getNombreUsuario())) {
            return "Ya existe un usuario con el nombre: " + usuario.getNombreUsuario();
        }
        if (usuarioExistePorCorreo(usuario.getCorreoElectronico())) {
            return "Ya existe un usuario con el correo: " + usuario.getCorreoElectronico();
        }
        return null;
    }

    public void agregarUsuario(Usuario usuario){
        String valido = validarUsuario(usuario);
        if(valido == null){
            usuarios.add(usuario);
            System.out.println("Usuario '" + usuario.getNombreUsuario() + "' agregado exitosamente.");
            guardarBaseDatos();
        }else{
            // En lugar de imprimir, lanzamos una excepción para que el controlador la atrape
            throw new IllegalStateException(valido);
        }
    }

    public boolean usuarioExistePorCorreo(String correo) {
        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getCorreoElectronico().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    /*
    * Recibe un String nombre
    * Devuelve true si el nombre existe en la base de datos, false en caso opuesto
    */
    public boolean usuarioExistePorNombre(String nombre){
        for (Usuario usuarioExistente : usuarios) {
            if (usuarioExistente.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    
    /*
    Busca un usuario en la base de datos por su dirección de correo electrónico.
    La búsqueda no distingue entre mayúsculas y minúsculas.
    @param correo El correo electrónico del usuario a buscar.
    @return El objeto Usuario si se encuentra, o 'null' si no existe un usuario con ese correo.
    */
    public Usuario buscarUsuarioPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
        return null; // No buscamos si el correo es nulo o vacío.
        }
        // Recorremos la lista de todos los usuarios.
        for (Usuario usuario : this.usuarios) {
        // Comparamos los correos ignorando mayúsculas/minúsculas.
            if (usuario.getCorreoElectronico().equalsIgnoreCase(correo)) {
            // Se encontro el usuario asi que retorono el objeto usuario completo
            return usuario;
            }
        }
        // No se encontro usuario se retorna null
        return null;
    }

    /*
    * Comprueba si un nombre ya está en uso por OTRO usuario.
    * Es la versión sobrecargada de 'usuarioExistePorNombre' para usarla al editar.
    */
    public boolean usuarioExistePorNombre(String nombre, String correoExcluido) {
        for (Usuario usuarioExistente : usuarios) {
            // Si el usuario que estamos revisando NO es el que estamos editando...
            if (!usuarioExistente.getCorreoElectronico().equalsIgnoreCase(correoExcluido)) {
                // ...comprobamos si su nombre coincide.
                if (usuarioExistente.getNombreUsuario().equalsIgnoreCase(nombre)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
    * Centraliza la lógica para actualizar un usuario.
    * Valida los datos y luego guarda los cambios.
    */
    public void actualizarUsuario(String correoOriginal, String nuevoNombre, Rol nuevoRol) {
        // 1. Validación de unicidad
        if (usuarioExistePorNombre(nuevoNombre, correoOriginal)) {
            throw new IllegalStateException("El nombre '" + nuevoNombre + "' ya está en uso por otro usuario.");
        }

        // 2. Búsqueda
        Usuario usuarioAActualizar = buscarUsuarioPorCorreo(correoOriginal);
        if (usuarioAActualizar == null) {
            throw new IllegalStateException("No se pudo encontrar al usuario para actualizar.");
        }

        // 3. Actualización (los setters del Usuario validan el formato)
        usuarioAActualizar.setNombreUsuario(nuevoNombre);
        usuarioAActualizar.setRol(nuevoRol);

        // 4. Guardado
        guardarBaseDatos();
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
            System.out.println("  Nombre: " + u.getNombreUsuario());
            System.out.println("  Email: " + u.getCorreoElectronico());
            
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