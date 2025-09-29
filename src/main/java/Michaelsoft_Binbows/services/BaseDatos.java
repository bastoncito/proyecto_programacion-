//POR HACER: Manejo centralizado de validaciones (eventualmente migrar los métodos de validaciones a una clase aparte o algo así)
package Michaelsoft_Binbows.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import Michaelsoft_Binbows.data.*;

//Con esta línea, volvemos a esta clase un Spring Bean, que será gestionado por el framework e inyectado en los controladores.
//De esta manera nos evitamos crear nuevas instancias en cada controlador, y los conflictos que surjan a partir de esto.
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
        return new ArrayList<>(usuarios); // Devuelve una copia para evitar modificaciones externas
    }

    public void eliminarUsuario(Usuario usuario){
        if(usuario == null || !usuarioExistePorNombre(usuario.getNombreUsuario())){
            throw new IllegalArgumentException("El usuario no existe en la base de datos");
        }
        usuarios.remove(usuario);
        System.out.println("El usuario '" + usuario.getNombreUsuario() + "' ha sido eliminado.");
        guardarBaseDatos();
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
        if (usuarioExistePorNombre(usuario.getNombreUsuario())) {
            return "Ya existe un usuario con el nombre: " + usuario.getNombreUsuario();
        }

        // Validar correo
        if (usuarioExistePorCorreo(usuario.getCorreoElectronico())) {
            return "Ya existe un usuario con el correo: " + usuario.getCorreoElectronico();
        }
        // Si pasa las validaciones, lo agregamos
        return null;
    }

    public void agregarUsuario(Usuario usuario){
        String valido = validarUsuario(usuario);
        if(valido == null){
            usuarios.add(usuario);
            System.out.println("Usuario '" + usuario.getNombreUsuario() + "' agregado exitosamente.");
            guardarBaseDatos();
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
            if (usuarioExistente.getCorreoElectronico().equalsIgnoreCase(correo)) {
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
            if (usuarioExistente.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }
    /**
 * Busca un usuario en la base de datos por su dirección de correo electrónico.
 * La búsqueda no distingue entre mayúsculas y minúsculas.
 *
 * @param correo El correo electrónico del usuario a buscar.
 * @return El objeto Usuario si se encuentra, o 'null' si no existe un usuario con ese correo.
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
