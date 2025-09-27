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
        this.usuarios = this.persistencia.cargarUsuarios();
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
            this.persistencia.guardarBaseDatos(this.usuarios);
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
