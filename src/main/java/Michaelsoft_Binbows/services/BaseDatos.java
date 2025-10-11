package Michaelsoft_Binbows.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Michaelsoft_Binbows.exceptions.EdicionInvalidaException;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * CLASE ADAPTADOR: BaseDatos ahora usa SQLite en lugar de JSON.
 * Mantiene la misma interfaz pública para no romper los controllers existentes.
 * Internamente usa los Repositorios JPA.
 */
@Service
public class BaseDatos {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    private final PasswordEncoder passwordEncoder;

    public BaseDatos(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        // YA NO necesitamos PersistenciaJSON ni cargar/guardar manualmente
        System.out.println("LOG: BaseDatos inicializado con SQLite.");
    }

    /**
     * YA NO SE USA - SQLite guarda automáticamente
     * Mantenemos el método por compatibilidad pero no hace nada
     */
    @Deprecated
    public void guardarBaseDatos() {
        // JPA guarda automáticamente con @Transactional
        // Este método ya no hace nada, pero lo mantenemos para no romper el código existente
        System.out.println("LOG: guardarBaseDatos() llamado (ya no necesario con SQLite)");
    }

    /**
     * YA NO SE USA - SQLite carga automáticamente
     * Mantenemos el método por compatibilidad pero no hace nada
     */
    @Deprecated
    public void cargarBaseDatos() {
        // JPA carga automáticamente desde la BD
        System.out.println("LOG: cargarBaseDatos() llamado (ya no necesario con SQLite)");
    }

    /**
     * Obtiene todos los usuarios de la base de datos
     */
    public List<Usuario> getUsuarios() { 
        return new ArrayList<>(usuarioRepository.findAll());
    }

    /**
     * Elimina un usuario de la base de datos
     */
    @Transactional
    public void eliminarUsuario(Usuario usuario){
        Optional<Usuario> usuarioEnBD = usuarioRepository.findByCorreoElectronico(usuario.getCorreoElectronico());
        
        if (usuarioEnBD.isPresent()) {
            usuarioRepository.delete(usuarioEnBD.get());
            System.out.println("El usuario '" + usuario.getNombreUsuario() + "' ha sido eliminado.");
        } else {
            throw new IllegalArgumentException("El usuario a eliminar no fue encontrado en la base de datos.");
        }
    }
    
    /**
     * Valida si un usuario puede ser agregado
     */
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

    /**
     * Agrega un nuevo usuario a la base de datos
     */
@Transactional
public void agregarUsuario(Usuario usuario) throws RegistroInvalidoException{
    System.out.println(">>> BaseDatos.agregarUsuario() iniciado");
    System.out.println("    Usuario: " + usuario.getNombreUsuario());
    System.out.println("    Email: " + usuario.getCorreoElectronico());
    
    String valido = validarUsuario(usuario);
    System.out.println("    Validación: " + (valido == null ? "OK" : valido));
    
    if(valido == null){
        System.out.println("    Guardando en repository...");
        usuarioRepository.save(usuario);
        System.out.println("    ✓ save() ejecutado");
        System.out.println("Usuario '" + usuario.getNombreUsuario() + "' agregado exitosamente.");
    } else {
        System.out.println("    ✗ Validación falló");
        throw new RegistroInvalidoException(valido);
    }
}

    /**
     * Verifica si existe un usuario con el correo dado
     */
    public boolean usuarioExistePorCorreo(String correo) {
        return usuarioRepository.existsByCorreoElectronico(correo);
    }

    /**
     * Verifica si existe un usuario con el nombre dado
     */
    public boolean usuarioExistePorNombre(String nombre){
        return usuarioRepository.existsByNombreUsuario(nombre);
    }

    /**
     * Busca un usuario por su correo electrónico
     */
    public Usuario buscarUsuarioPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return usuarioRepository.findByCorreoElectronico(correo).orElse(null);
    }

    /**
     * Busca un usuario por su nombre de usuario
     */
    public Usuario buscarUsuarioPorNombre(String nombreUsuario){
        return usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
    }

    /**
     * Comprueba si un nombre ya está en uso por OTRO usuario
     * (usado al editar)
     */
    public boolean usuarioExistePorNombre(String nombre, String correoExcluido) {
        Optional<Usuario> usuarioConNombre = usuarioRepository.findByNombreUsuario(nombre);
        
        if (usuarioConNombre.isEmpty()) {
            return false; // El nombre no existe, está disponible
        }
        
        // Si existe, verificamos que no sea el usuario que estamos editando
        return !usuarioConNombre.get().getCorreoElectronico().equalsIgnoreCase(correoExcluido);
    }

    /**
     * Comprueba si un correo ya está en uso por OTRO usuario
     * (usado al editar)
     */
    public boolean usuarioExistePorCorreo(String correo, String correoExcluido) {
        if (correo.equalsIgnoreCase(correoExcluido)) {
            return false; // Es el mismo usuario
        }
        return usuarioRepository.existsByCorreoElectronico(correo);
    }

    /**
     * Actualiza los datos de un usuario
     */
    @Transactional
    public void actualizarUsuario(String correoOriginal, String nuevoNombre, String nuevoCorreo, Rol nuevoRol) 
            throws EdicionInvalidaException {
        
        // Validaciones
        if (usuarioExistePorNombre(nuevoNombre, correoOriginal)) {
            throw new EdicionInvalidaException(
                "El nombre '" + nuevoNombre + "' ya está en uso por otro usuario.", 
                correoOriginal
            );
        }

        if (usuarioExistePorCorreo(nuevoCorreo, correoOriginal)) {
            throw new EdicionInvalidaException(
                "El correo '" + nuevoCorreo + "' ya está registrado por otro usuario.", 
                correoOriginal
            );
        }
    
        // Buscar el usuario a actualizar
        Usuario usuarioAActualizar = buscarUsuarioPorCorreo(correoOriginal);
        if (usuarioAActualizar == null) {
            throw new EdicionInvalidaException(
                "Error crítico: No se pudo encontrar al usuario para actualizar.", 
                correoOriginal
            );
        }

        // Actualizar datos
        try{
            usuarioAActualizar.setNombreUsuario(nuevoNombre);
            usuarioAActualizar.setCorreoElectronico(nuevoCorreo);
            usuarioAActualizar.setRol(nuevoRol);
            
            // JPA guarda automáticamente dentro de @Transactional
            usuarioRepository.save(usuarioAActualizar);
            
        } catch(RegistroInvalidoException e){
            throw new EdicionInvalidaException(e.getMessage(), correoOriginal);
        }
    }

    /**
     * Actualiza la contraseña de un usuario
     */
    @Transactional
    public void actualizarContraseñaUsuario(String correoUsuario, String nuevaContraseñaSinEncriptar) 
            throws RegistroInvalidoException {
        
        Usuario usuario = buscarUsuarioPorCorreo(correoUsuario);
        if (usuario == null) {
            throw new IllegalArgumentException("No se encontró un usuario con el correo: " + correoUsuario);
        }
        
        // 1. Validar la contraseña
        usuario.setContraseña(nuevaContraseñaSinEncriptar);
        
        // 2. Encriptar
        String contraseñaEncriptada = passwordEncoder.encode(nuevaContraseñaSinEncriptar);
        
        // 3. Guardar
        usuario.setContraseña(contraseñaEncriptada);
        usuarioRepository.save(usuario);
        
        System.out.println("LOG: La contraseña del usuario '" + usuario.getNombreUsuario() + "' ha sido actualizada y encriptada.");
    }

    /**
     * Imprime todos los usuarios (útil para debugging)
     */
    public void imprimirTodosUsuarios() {
        List<Usuario> usuarios = getUsuarios();
        
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios en la base de datos.");
            return;
        }
        
        System.out.println("\n--- USUARIOS EN BASE DE DATOS (SQLite) ---");
        
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            System.out.println("\nUSUARIO #" + (i + 1));
            System.out.println("  ID: " + u.getId());
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