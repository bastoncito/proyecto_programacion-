package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
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

/**
 * Servicio para la lógica de negocio principal relacionada con los Usuarios. Gestiona la creación,
 * validación, actualización y eliminación de usuarios.
 */
@Service
public class UsuarioService {

  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private ConfiguracionService configuracionService;

  /**
   * Obtiene una lista de todos los usuarios registrados.
   *
   * @return Lista de todos los usuarios.
   */
  public List<Usuario> obtenerTodos() {
    return usuarioRepository.findAll();
  }

  /**
   * Obtiene un usuario por su ID.
   *
   * @param id El ID del usuario.
   * @return Un Optional con el usuario si existe.
   */
  public Optional<Usuario> obtenerPorId(Long id) {
    return usuarioRepository.findById(id);
  }

  /**
   * Guarda un nuevo usuario, validando todos sus campos.
   *
   * @param usuario El usuario a guardar.
   * @return El usuario guardado.
   * @throws EdicionInvalidaException Si el nombre de usuario es inválido.
   * @throws RegistroInvalidoException Si la contraseña, nombre o correo ya existen o son inválidos.
   */
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
    if (usuario.getCorreoElectronico() == null || !correoValdo(usuario.getCorreoElectronico())) {
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

  /**
   * Elimina un usuario por su ID.
   *
   * @param id El ID del usuario a eliminar.
   * @throws IllegalArgumentException si el usuario no existe.
   */
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

  /**
   * Obtiene un usuario por su correo electrónico.
   *
   * @param correo El correo del usuario.
   * @return Un Optional con el usuario si existe.
   */
  public Optional<Usuario> obtenerPorCorreo(String correo) {
    return usuarioRepository.findByCorreoElectronico(correo);
  }

  /**
   * Comprueba si un usuario ya existe en la base de datos por su ID.
   *
   * @param usuario El usuario a comprobar.
   * @return true si existe, false si no.
   */
  public boolean existe(Usuario usuario) {
    return usuarioRepository.existsById(usuario.getId());
  }

  // Método para validar el formato del correo electrónico
  private static boolean correoValdo(String correo) {
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

  /**
   * Valida la fortaleza de una contraseña.
   *
   * @param contrasena La contraseña a validar.
   * @return null si la contraseña es válida, o un String con el mensaje de error si no lo es.
   */
  public String validarContrasena(String contrasena) {
    if (contrasena == null || contrasena.trim().isEmpty()) {
      return "La contrasena no puede estar vacía";
    }

    // ARREGLO: 'if' debe usar llaves
    if (contrasena.length() < 8) {
      return "La contrasena debe tener al menos 8 caracteres";
    }

    // ARREGLO: 'if' debe usar llaves
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
      // ARREGLO: 'if' debe usar llaves
      if (!tieneMayuscula) {
        errores.append("- Debe contener al menos una mayúscula\n");
      }
      // ARREGLO: 'if' debe usar llaves
      if (!tieneMinuscula) {
        errores.append("- Debe contener al menos una minúscula\n");
      }
      if (!tieneDigito) {
        errores.append("- Debe contener al menos un dígito\n");
      }
      if (!tieneCaracterEspecial) {
        errores.append("- Debe contener al menos un carácter especial (!@#$%^&* etc.)\n");
      }
      return "La contrasena es demasiado débil. Requisitos:\n" + errores.toString();
    }
    return null; // Contrasena válida
  }

  /**
   * Busca un usuario por correo electrónico.
   *
   * @param correo El correo del usuario.
   * @return El Usuario o null si no se encuentra.
   */
  public Usuario buscarPorCorreo(String correo) {
    return usuarioRepository.findByCorreoElectronico(correo).orElse(null);
  }

  /**
   * Cuenta el número total de usuarios registrados.
   *
   * @return El total de usuarios (long).
   */
  public long contarUsuarios() {
    return usuarioRepository.count();
  }

  /**
   * Guarda un usuario sin validar la contraseña (útil para admin cuando la contraseña ya está
   * hasheada).
   *
   * @param usuario El usuario a guardar.
   * @return El usuario guardado.
   * @throws RegistroInvalidoException Si el nombre de usuario o correo son inválidos o ya existen.
   */
  public Usuario guardarSinValidarContrasena(Usuario usuario) throws RegistroInvalidoException {
    // Valida nombre y correo, pero NO la contrasena (ya está hasheada)
    if (usuario.getNombreUsuario() == null
        || usuario.getNombreUsuario().trim().isEmpty()
        || usuario.getNombreUsuario().length() < 3
        || usuario.getNombreUsuario().length() > 30) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    if (usuario.getCorreoElectronico() == null || !correoValdo(usuario.getCorreoElectronico())) {
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

  /**
   * Guarda una entidad Usuario directamente en la base de datos (BD).
   *
   * @param usuario El usuario a guardar.
   * @throws RegistroInvalidoException Si el correo es nulo o vacío.
   */
  public void guardarEnBd(Usuario usuario) throws RegistroInvalidoException {
    if (usuario.getCorreoElectronico() == null || usuario.getCorreoElectronico().isEmpty()) {
      throw new RegistroInvalidoException("Correo electrónico no válido");
    }
    usuarioRepository.save(usuario);
  }

  /**
   * Actualiza los datos de un usuario (nombre, correo, rol) desde el panel de admin.
   *
   * @param correoOriginal El correo actual del usuario (para buscarlo).
   * @param nuevoNombre El nuevo nombre de usuario.
   * @param nuevoCorreo El nuevo correo electrónico.
   * @param nuevoRol El nuevo rol a asignar.
   * @throws RegistroInvalidoException Si los nuevos datos son inválidos.
   */
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
    if (nuevoCorreo == null || !correoValdo(nuevoCorreo)) {
      throw new IllegalArgumentException("Correo electrónico no válido: " + nuevoCorreo);
    }
    usuario.setNombreUsuario(nuevoNombre);
    usuario.setCorreoElectronico(nuevoCorreo);
    usuario.setRol(nuevoRol);
    usuarioRepository.save(usuario);
  }

  /**
   * Actualiza la contraseña de un usuario desde el panel de admin.
   *
   * @param correo El correo del usuario a modificar.
   * @param nuevaContrasena La nueva contraseña (en texto plano, se hasheará aquí).
   * @throws RegistroInvalidoException Si la nueva contraseña no es válida.
   */
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

  /**
   * Elimina un usuario usando su correo electrónico.
   *
   * @param correoAeliminar El correo del usuario a eliminar. // <-- ARREGLO: Renombrado
   */
  public void eliminarPorCorreo(String correoAeliminar) { // <-- ARREGLO: Renombrado
    Usuario usuario = buscarPorCorreo(correoAeliminar); // <-- ARREGLO: Renombrado
    if (usuario == null) {
      throw new IllegalArgumentException("No se encontró el usuario con el correo proporcionado.");
    }
    usuarioRepository.delete(usuario);
  }

  // Metodos para manejar las tareas de los usuarios
  /**
   * Busca un usuario por correo y fuerza la carga de su lista de tareas.
   *
   * @param correo El correo del usuario.
   * @return El Usuario con sus tareas cargadas, o null.
   */
  @Transactional
  public Usuario buscarPorCorreoConTareas(String correo) {
    Usuario usuario = usuarioRepository.findByCorreoElectronico(correo).orElse(null);
    if (usuario != null) {
      usuario.getTareas(); // Fuerza la carga de tareas
    }
    return usuario;
  }

  /**
   * Agrega una nueva tarea a un usuario específico por su correo.
   *
   * @param correo El correo del usuario.
   * @param tarea La tarea a agregar.
   * @throws TareaInvalidaException Si la tarea ya existe en la lista del usuario.
   */
  @Transactional
  public void agregarTareaAusuario(String correo, Tarea tarea) throws TareaInvalidaException {
    Usuario usuario =
        usuarioRepository
            .findByCorreoElectronico(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    usuario.agregarTarea(tarea);
    usuarioRepository.save(usuario);
  }

  /**
   * Marca una tarea de un usuario como completada.
   *
   * @param correo El correo del usuario.
   * @param nombreTarea El nombre de la tarea a completar.
   * @throws RegistroInvalidoException Si la tarea no se encuentra o ya está completada.
   */
  @Transactional
  public void completarTarea(String correo, String nombreTarea) throws RegistroInvalidoException {
    Usuario usuario =
        usuarioRepository
            .findByCorreoElectronico(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    usuario.completarTarea(nombreTarea); // accedes a la colección dentro de la transacción
    this.actualizarLigaDelUsuario(usuario);
    usuarioRepository.save(usuario);
  }

  /**
   * Elimina una tarea pendiente de la lista de un usuario.
   *
   * @param correo El correo del usuario.
   * @param nombreTarea El nombre de la tarea a eliminar.
   * @throws RegistroInvalidoException Si la tarea no se encuentra.
   */
  @Transactional
  public void eliminarTarea(String correo, String nombreTarea) throws RegistroInvalidoException {
    Usuario usuario =
        usuarioRepository
            .findByCorreoElectronico(correo)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    usuario.cancelarTarea(nombreTarea); // Acceso seguro a la colección
    usuarioRepository.save(usuario);
  }

  /**
   * Guarda un usuario y se asegura de que sus tareas asociadas también se persistan.
   *
   * @param usuario El usuario a guardar.
   * @return El usuario guardado.
   * @throws EdicionInvalidaException Si el nombre es inválido.
   * @throws RegistroInvalidoException Si la contraseña o correo son inválidos.
   */
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
    if (usuario.getCorreoElectronico() == null || !correoValdo(usuario.getCorreoElectronico())) {
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

  /**
   * Método utilizado para obtener una lista de las tareas pendientes de un usuario.
   *
   * <p>A partir del correo, busca al usuario en la base de datos y devuelve su lista de tareas
   * pendientes.
   *
   * @param correo correo electrónico del usuario
   * @return lista de tareas pendientes
   */
  public List<Tarea> obtenerTareasPendientes(String correo) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoElectronico(correo);
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();
      return usuario.getTareasPendientes();
    }
    return new ArrayList<>();
  }

  /**
   * Obtiene una lista paginada de usuarios ordenada por sus puntos de liga.
   *
   * @param limite El número de usuarios a incluir en el "top".
   * @return Una lista de usuarios del top.
   */
  public List<Usuario> getTopUsuarios(int limite) {
    // Crea un "pedido" para la primera página (página 0) con el tamano del límite
    Pageable topN = PageRequest.of(0, limite);

    // Llama al nuevo método del repositorio
    Page<Usuario> paginaDeUsuarios = usuarioRepository.findByOrderByPuntosLigaDesc(topN);

    // Devuelve la lista de usuarios de esa página
    return paginaDeUsuarios.getContent();
  }

  /**
   * Calcula y actualiza la liga de un usuario basándose en sus puntosLiga. Esta lógica ahora usa
   * valores dinámicos del ConfiguracionService.
   */
  private void actualizarLigaDelUsuario(Usuario usuario) {

    // 1. Obtenemos los límites de la base de datos.
    // (Estos métodos 'getLimiteLiga' los crearemos en ConfiguracionService)
    // Usamos valores por defecto (500, 1500...) por si aún no están en la BD.
    int limitePlata = configuracionService.getLimiteLiga("LIGA_PLATA", 500);
    int limiteOro = configuracionService.getLimiteLiga("LIGA_ORO", 1500);
    int limitePlatino = configuracionService.getLimiteLiga("LIGA_PLATINO", 3000);
    int limiteDiamante = configuracionService.getLimiteLiga("LIGA_DIAMANTE", 5000);

    // 2. Obtenemos los puntos del usuario
    int puntos = usuario.getPuntosLiga();
    String nuevaLiga = "Bronce"; // La liga por defecto

    // 3. Comparamos (de mayor a menor)
    if (puntos >= limiteDiamante) {
      nuevaLiga = "Diamante";
    } else if (puntos >= limitePlatino) {
      nuevaLiga = "Platino";
    } else if (puntos >= limiteOro) {
      nuevaLiga = "Oro";
    } else if (puntos >= limitePlata) {
      nuevaLiga = "Plata";
    }

    // 4. Actualizamos el objeto Usuario
    usuario.setLiga(nuevaLiga);
  }

  /**
   * RECALCULA LA LIGA para TODOS los usuarios de la base de datos. Útil cuando un admin cambia los
   * límites de puntos de las ligas.
   */
  @Transactional
  public void recalcularLigasGlobal() {
    System.out.println("LOG: Iniciando recálculo global de ligas...");

    // 1. Obtenemos los límites (¡solo los leemos una vez!)
    int limitePlata = configuracionService.getLimiteLiga("LIGA_PLATA", 500);
    int limiteOro = configuracionService.getLimiteLiga("LIGA_ORO", 1500);
    int limitePlatino = configuracionService.getLimiteLiga("LIGA_PLATINO", 3000);
    int limiteDiamante = configuracionService.getLimiteLiga("LIGA_DIAMANTE", 5000);

    // 2. Buscamos a TODOS los usuarios
    List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

    // 3. Iteramos y recalculamos
    for (Usuario usuario : todosLosUsuarios) {
      int puntos = usuario.getPuntosLiga();
      String nuevaLiga = "Bronce";

      if (puntos >= limiteDiamante) {
        nuevaLiga = "Diamante";
      } else if (puntos >= limitePlatino) {
        nuevaLiga = "Platino";
      } else if (puntos >= limiteOro) {
        nuevaLiga = "Oro";
      } else if (puntos >= limitePlata) {
        nuevaLiga = "Plata";
      }

      // Asignamos la nueva liga al usuario
      usuario.setLiga(nuevaLiga);
    }

    // 4. Guardamos TODOS los cambios en la base de datos
    usuarioRepository.saveAll(todosLosUsuarios);
    System.out.println("LOG: Recálculo global de ligas terminado.");
  }
}
