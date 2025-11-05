package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.EdicionInvalidaException;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.model.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  @Autowired private UsuarioRepository usuarioRepository;

  public List<Usuario> obtenerTodos() {
    return usuarioRepository.findAll();
  }

  public Optional<Usuario> obtenerPorId(Long id) {
    return usuarioRepository.findById(id);
  }

  public Usuario guardar(Usuario usuario)
      throws EdicionInvalidaException, RegistroInvalidoException {
    // Validación nombre de usuario
    if (usuario.getNombreUsuario() == null
        || usuario.getNombreUsuario().trim().isEmpty()
        || usuario.getNombreUsuario().length() < 3
        || usuario.getNombreUsuario().length() > 30) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    // Validación correo electrónico
    if (usuario.getCorreoElectronico() == null || !correoValido(usuario.getCorreoElectronico())) {
      throw new IllegalArgumentException(
          "Correo electrónico no válido: " + usuario.getCorreoElectronico());
    }
    // Validación contrasena
    String resultado = validarContrasena(usuario.getContrasena());
    if (resultado != null) {
      throw new IllegalArgumentException(resultado);
    }
    if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty()) {
      throw new EdicionInvalidaException("Error", usuario.getCorreoElectronico());
    }
    if (usuario.getContrasena() == null || usuario.getContrasena().length() < 8) {
      throw new RegistroInvalidoException("La contrasena debe tener al menos 8 caracteres.");
    }

    // Verificar unicidad de nombre de usuario y correo
    if (usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
      throw new RegistroInvalidoException(
          "El nombre de usuario ya existe: " + usuario.getNombreUsuario());
    }
    if (usuarioRepository.findByCorreoElectronico(usuario.getCorreoElectronico()).isPresent()) {
      throw new RegistroInvalidoException(
          "El correo electrónico ya está registrado: " + usuario.getCorreoElectronico());
    }

    return usuarioRepository.save(usuario);
  }

  public void eliminar(Long id) {
    System.out.println("LOG: Servicio eliminar llamado con ID: " + id);
    if (usuarioRepository.existsById(id)) {
      usuarioRepository.deleteById(id);
      System.out.println("LOG: Usuario borrado en la base de datos.");
    } else {
      System.out.println("LOG: Usuario no existe en la base de datos.");
      throw new IllegalArgumentException("El usuario no existe.");
    }
  }

  public Optional<Usuario> obtenerPorCorreo(String correo) {
    return usuarioRepository.findByCorreoElectronico(correo);
  }

  public boolean existe(Usuario usuario) {
    return usuarioRepository.existsById(usuario.getId());
  }

  // Método para validar el formato del correo electrónico
  private static boolean correoValido(String correo) {
    if (correo == null || correo.trim().isEmpty()) {
      return false;
    }
    String regex =
        "[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}";
    // [a-zA-Z0-9_]+ UNO O MAS, caracter (letras o numeros o '_')
    // ([.][a-zA-Z0-9_]+)* CERO O MAS, punto '.' seguido de almenos un caracter
    // @ UN simbolo arroba
    // [a-zA-Z0-9_]+ UNO O MAS, caracter (letras o numeros o '_')
    // ([.][a-zA-Z0-9_]+)* CERO O MAS, punto '.' seguido de almenos un caracter
    // [.][a-zA-Z]{2,5} UN punto '.', seguido de DOS A CINCO letras
    return correo.matches(regex);
  }

  // Método para validar la contrasena
  public String validarContrasena(String contrasena) {
    if (contrasena == null || contrasena.trim().isEmpty()) {
      return "La contrasena no puede estar vacía";
    }

    if (contrasena.length() < 8) {
      return "La contrasena debe tener al menos 8 caracteres";
    }

    if (contrasena.contains(" ")) {
      return "La contrasena no puede contener espacios";
    }

    boolean tieneMayuscula = Pattern.compile("[A-Z]").matcher(contrasena).find();
    boolean tieneMinuscula = Pattern.compile("[a-z]").matcher(contrasena).find();
    boolean tieneDigito = Pattern.compile("\\d").matcher(contrasena).find();
    boolean tieneCaracterEspecial =
        Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?]").matcher(contrasena).find();

    if (!tieneMayuscula || !tieneMinuscula || !tieneDigito || !tieneCaracterEspecial) {
      StringBuilder errores = new StringBuilder();
      if (!tieneMayuscula) errores.append("- Debe contener al menos una mayúscula\n");
      if (!tieneMinuscula) errores.append("- Debe contener al menos una minúscula\n");
      if (!tieneDigito) errores.append("- Debe contener al menos un dígito\n");
      if (!tieneCaracterEspecial)
        errores.append("- Debe contener al menos un carácter especial (!@#$%^&* etc.)\n");
      return "La contrasena es demasiado débil. Requisitos:\n" + errores.toString();
    }
    return null; // Contrasena válida
  }

  public Usuario buscarPorCorreo(String correo) {
    return usuarioRepository.findByCorreoElectronico(correo).orElse(null);
  }

  public long contarUsuarios() {
    return usuarioRepository.count();
  }

  public Usuario guardarSinValidarContrasena(Usuario usuario) throws RegistroInvalidoException {
    // Valida nombre y correo, pero NO la contrasena (ya está hasheada)
    if (usuario.getNombreUsuario() == null
        || usuario.getNombreUsuario().trim().isEmpty()
        || usuario.getNombreUsuario().length() < 3
        || usuario.getNombreUsuario().length() > 30) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    if (usuario.getCorreoElectronico() == null || !correoValido(usuario.getCorreoElectronico())) {
      throw new IllegalArgumentException(
          "Correo electrónico no válido: " + usuario.getCorreoElectronico());
    }
    // Verificar unicidad de nombre de usuario y correo
    if (usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario()).isPresent()) {
      throw new michaelsoftbinbows.exceptions.RegistroInvalidoException(
          "Nombre de usuario ya existe: " + usuario.getNombreUsuario());
    }
    if (usuarioRepository.findByCorreoElectronico(usuario.getCorreoElectronico()).isPresent()) {
      throw new michaelsoftbinbows.exceptions.RegistroInvalidoException(
          "Correo electrónico ya registrado: " + usuario.getCorreoElectronico());
    }
    return usuarioRepository.save(usuario);
  }

  public void guardarEnBD(Usuario usuario) throws RegistroInvalidoException {
    if (usuario.getCorreoElectronico() == null || usuario.getCorreoElectronico().isEmpty()) {
      throw new RegistroInvalidoException("Correo electrónico no válido");
    }
    usuarioRepository.save(usuario);
  }

  public void actualizarUsuario(
      String correoOriginal, String nuevoNombre, String nuevoCorreo, Rol nuevoRol)
      throws RegistroInvalidoException {
    Usuario usuario = buscarPorCorreo(correoOriginal);
    if (usuario == null) {
      throw new IllegalArgumentException("No se encontró el usuario con el correo original.");
    }
    // Validaciones básicas
    if (nuevoNombre == null
        || nuevoNombre.trim().isEmpty()
        || nuevoNombre.length() < 3
        || nuevoNombre.length() > 30) {
      throw new IllegalArgumentException("Nombre de usuario no válido: " + nuevoNombre);
    }
    if (nuevoCorreo == null || !correoValido(nuevoCorreo)) {
      throw new IllegalArgumentException("Correo electrónico no válido: " + nuevoCorreo);
    }
    usuario.setNombreUsuario(nuevoNombre);
    usuario.setCorreoElectronico(nuevoCorreo);
    usuario.setRol(nuevoRol);
    usuarioRepository.save(usuario);
  }

  public void actualizarContrasenaUsuario(String correo, String nuevaContrasena)
      throws RegistroInvalidoException {
    Usuario usuario = buscarPorCorreo(correo);
    if (usuario == null) {
      throw new IllegalArgumentException("No se encontró el usuario con el correo proporcionado.");
    }
    String resultado = validarContrasena(nuevaContrasena);
    if (resultado != null) {
      throw new RegistroInvalidoException(resultado);
    }
    // Hashea la nueva contrasena antes de guardar
    org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    usuario.setContrasena(encoder.encode(nuevaContrasena));
    usuarioRepository.save(usuario);
  }

  public void eliminarPorCorreo(String correoAEliminar) {
    Usuario usuario = buscarPorCorreo(correoAEliminar);
    if (usuario == null) {
      throw new IllegalArgumentException("No se encontró el usuario con el correo proporcionado.");
    }
    usuarioRepository.delete(usuario);
  }

  // Metodos para manejar las tareas de los usuarios
  @Transactional
  public Usuario buscarPorCorreoConTareas(String correo) {
    Usuario usuario = usuarioRepository.findByCorreoElectronico(correo).orElse(null);
    if (usuario != null) {
      usuario.getTareas(); // Fuerza la carga de tareas
    }
    return usuario;
  }

  @Transactional
  public void agregarTareaAUsuario(String correo, Tarea tarea) throws TareaInvalidaException {
    Usuario usuario =
        usuarioRepository
            .findByCorreoElectronico(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    usuario.agregarTarea(tarea);
    usuarioRepository.save(usuario);
  }

  @Transactional
  public void completarTarea(String correo, String nombreTarea) throws RegistroInvalidoException {
    Usuario usuario =
        usuarioRepository
            .findByCorreoElectronico(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    usuario.completarTarea(nombreTarea); // accedes a la colección dentro de la transacción
    usuarioRepository.save(usuario);
  }

  @Transactional
  public void eliminarTarea(String correo, String nombreTarea) throws RegistroInvalidoException {
    Usuario usuario =
        usuarioRepository
            .findByCorreoElectronico(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    usuario.cancelarTarea(nombreTarea); // Acceso seguro a la colección
    usuarioRepository.save(usuario);
  }

  @Transactional
  public Usuario guardarConTareas(Usuario usuario)
      throws EdicionInvalidaException, RegistroInvalidoException {
    // Validaciones básicas
    if (usuario.getNombreUsuario() == null
        || usuario.getNombreUsuario().trim().isEmpty()
        || usuario.getNombreUsuario().length() < 3
        || usuario.getNombreUsuario().length() > 30) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    if (usuario.getCorreoElectronico() == null || !correoValido(usuario.getCorreoElectronico())) {
      throw new IllegalArgumentException(
          "Correo electrónico no válido: " + usuario.getCorreoElectronico());
    }
    String resultado = validarContrasena(usuario.getContrasena());
    if (resultado != null) {
      throw new IllegalArgumentException(resultado);
    }
    if (usuario.getNombreUsuario() == null || usuario.getNombreUsuario().trim().isEmpty()) {
      throw new EdicionInvalidaException("Error", usuario.getCorreoElectronico());
    }
    if (usuario.getContrasena() == null || usuario.getContrasena().length() < 8) {
      throw new RegistroInvalidoException("La contrasena debe tener al menos 8 caracteres.");
    }

    // Asocia las tareas pendientes y completadas al usuario antes de guardar
    if (usuario.getTareas() != null) {
      usuario.getTareas().forEach(t -> t.setUsuario(usuario));
    }
    if (usuario.getTareasCompletadas() != null) {
      usuario.getTareasCompletadas().forEach(t -> t.setUsuario(usuario));
    }

    return usuarioRepository.save(usuario);
  }

  public List<Usuario> getTopUsuarios(int limite) {
    // Crea un "pedido" para la primera página (página 0) con el tamano del límite
    Pageable topN = PageRequest.of(0, limite);

    // Llama al nuevo método del repositorio
    Page<Usuario> paginaDeUsuarios = usuarioRepository.findByOrderByPuntosLigaDesc(topN);

    // Devuelve la lista de usuarios de esa página
    return paginaDeUsuarios.getContent();
  }
}
