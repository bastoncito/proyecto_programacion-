package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.dto.TopJugadorLogrosDto;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.EdicionInvalidaException;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.model.Rol;
import michaelsoftbinbows.util.SistemaNiveles;
import michaelsoftbinbows.util.UsuarioValidator;
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
  @Autowired private GestorLogrosService gestorLogrosService;
  private UsuarioValidator usuarioValidator = new UsuarioValidator();

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
    if (id == null) {
      return Optional.empty();
    }
    return usuarioRepository.findById(id);
  }

  @Transactional
  /**
   * Guarda un nuevo usuario, validando todos sus campos.
   *
   * @param usuario El usuario a guardar.
   * @return El usuario guardado.
   * @throws EdicionInvalidaException Si el nombre de usuario es inválido.
   * @throws RegistroInvalidoException Si la contraseña, nombre o correo ya existen o son inválidos.
   */
  public Usuario guardarUsuario(Usuario usuario)
      throws EdicionInvalidaException, RegistroInvalidoException {
    // Delegar validaciones sintácticas a UsuarioValidator
    if (!usuarioValidator.nombreUsuarioValido(usuario.getNombreUsuario())) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    if (!usuarioValidator.correoValido(usuario.getCorreoElectronico())) {
      throw new IllegalArgumentException(
          "Correo electrónico no válido: " + usuario.getCorreoElectronico());
    }
    // validarContrasena devuelve null cuando está bien, o un mensaje de error
    String resultado = usuarioValidator.validarContrasena(usuario.getContrasena());
    if (resultado != null) {
      // Usamos RegistroInvalidoException para problemas con la contraseña
      throw new RegistroInvalidoException(resultado);
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

  @Transactional
  /**
   * Elimina un usuario por su ID.
   *
   * @param id El ID del usuario a eliminar.
   * @throws IllegalArgumentException si el usuario no existe.
   */
  public void eliminarUsuario(Long id) {
    System.out.println("LOG: Servicio eliminar llamado con ID: " + id);
    if (id == null) {
      throw new IllegalArgumentException("El ID no puede ser nulo.");
    }
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

  @Transactional
  /**
   * Guarda un usuario sin validar la contraseña (útil para admin cuando la contraseña ya está
   * hasheada).
   *
   * @param usuario El usuario a guardar.
   * @return El usuario guardado.
   * @throws RegistroInvalidoException Si el nombre de usuario o correo son inválidos o ya existen.
   */
  public Usuario guardarSinValidarContrasena(Usuario usuario) throws RegistroInvalidoException {
    // Valida nombre y correo (NO la contraseña, ya está hasheada)
    if (!usuarioValidator.nombreUsuarioValido(usuario.getNombreUsuario())) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    if (!usuarioValidator.correoValido(usuario.getCorreoElectronico())) {
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
   * Comprueba si la experiencia total del usuario es suficiente para subir al siguiente nivel.
   * Utiliza un bucle por si se ganan múltiples niveles a la vez.
   */
  public void verificarSubidaDeNivel(Usuario usuario) {
    int nivelExperiencia = usuario.getNivelExperiencia();
    int experiencia = usuario.getExperiencia();
    int expSiguienteNivel = SistemaNiveles.experienciaParaNivel(nivelExperiencia + 1);

    // El usuario subira de nivel hasta donde su experiencia le permita
    while (experiencia >= expSiguienteNivel) {
      nivelExperiencia++;
      usuario.setNivelExperiencia(nivelExperiencia);
      experiencia -= expSiguienteNivel;
      usuario.setExperiencia(experiencia);
      System.out.println("¡FELICIDADES! ¡Has subido al nivel " + nivelExperiencia + "!");
      // Se calcula la experiencia necesaria para el proximo nivel
      expSiguienteNivel = SistemaNiveles.experienciaParaNivel(nivelExperiencia + 1);
    }
  }

  @Transactional
  /**
   * Guarda una entidad Usuario directamente en la base de datos (BD).
   *
   * @param usuario El usuario a guardar.
   * @throws RegistroInvalidoException Si el correo es nulo o vacío.
   */
  public void guardarEnBd(Usuario usuario) {
    usuarioRepository.save(usuario);
  }

  @Transactional
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
      String correoOriginal, String nuevoNombre, String nuevoCorreo, Rol nuevoRol, String ciudad)
      throws RegistroInvalidoException {
    Usuario usuario = buscarPorCorreo(correoOriginal);
    if (usuario == null) {
      throw new IllegalArgumentException("No se encontró el usuario con el correo original.");
    }
    // Validaciones básicas
    if (!usuarioValidator.nombreUsuarioValido(nuevoNombre)) {
      throw new IllegalArgumentException("Nombre de usuario no válido: " + nuevoNombre);
    }
    if (!usuarioValidator.correoValido(nuevoCorreo)) {
      throw new IllegalArgumentException("Correo electrónico no válido: " + nuevoCorreo);
    }
    if (usuarioRepository.findByNombreUsuario(nuevoNombre).isPresent()
        && !usuario.getNombreUsuario().equals(nuevoNombre)) {
      throw new RegistroInvalidoException("El nombre de usuario ya existe: " + nuevoNombre);
    }
    if (usuarioRepository.findByCorreoElectronico(nuevoCorreo).isPresent()
        && !usuario.getCorreoElectronico().equals(nuevoCorreo)) {
      throw new RegistroInvalidoException(
          "El correo electrónico ya está registrado: " + nuevoCorreo);
    }
    usuario.setNombreUsuario(nuevoNombre);
    usuario.setCorreoElectronico(nuevoCorreo);
    usuario.setRol(nuevoRol);
    usuario.setCiudad(ciudad);
    usuarioRepository.save(usuario);
  }

  @Transactional
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
    String resultado = usuarioValidator.validarContrasena(nuevaContrasena);
    if (resultado != null) {
      throw new RegistroInvalidoException(resultado);
    }
    // Hashea la nueva contrasena antes de guardar
    org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    usuario.setContrasena(encoder.encode(nuevaContrasena));
    usuarioRepository.save(usuario);
  }

  @Transactional
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
   * Busca un usuario por correo y fuerza la carga de su lista de tareas dentro de un contexto
   * transaccional.
   *
   * @param correo El correo del usuario.
   * @return El Usuario con sus tareas cargadas, o null.
   */
  @Transactional
  public Usuario buscarPorCorreoConTareas(String correo) {
    Usuario usuario = usuarioRepository.findByCorreoElectronico(correo).orElse(null);
    if (usuario != null) {
      // Fuerza la inicialización de las colecciones dentro del contexto transaccional
      var unused = usuario.getTareas().size();
      unused = usuario.getTareasCompletadas().size();
    }
    return usuario;
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
    // Delegar validaciones sintácticas a UsuarioValidator
    if (!usuarioValidator.nombreUsuarioValido(usuario.getNombreUsuario())) {
      throw new IllegalArgumentException(
          "Nombre de usuario no válido: " + usuario.getNombreUsuario());
    }
    if (!usuarioValidator.correoValido(usuario.getCorreoElectronico())) {
      throw new IllegalArgumentException(
          "Correo electrónico no válido: " + usuario.getCorreoElectronico());
    }
    String resultado = usuarioValidator.validarContrasena(usuario.getContrasena());
    if (resultado != null) {
      throw new RegistroInvalidoException(resultado);
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
  @Transactional
  public List<Tarea> obtenerTareasPendientes(String correo) {
    // Usamos buscarPorCorreoConTareas para asegurarnos de que las colecciones LAZY
    // estén inicializadas dentro del contexto transaccional.
    Usuario usuario = buscarPorCorreoConTareas(correo);
    if (usuario != null) {
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
  public void actualizarLigaDelUsuario(Usuario usuario) {

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

  /**
   * Método unificado y robusto para actualizar la racha del usuario. Se encarga de incrementar,
   * reiniciar o mantener la racha según la fecha. Debe ser llamado cada vez que se completa una
   * tarea.
   *
   * @param usuario El usuario cuya racha se va a actualizar.
   */
  public void actualizarRacha(Usuario usuario) {
    LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
    LocalDate fechaUltimaRacha = usuario.getFechaRacha(); // Usamos el nombre de tu campo

    // CASO 1: Es la primera tarea que el usuario completa en su vida O la racha es 0.
    if (fechaUltimaRacha == null || usuario.getRacha() == 0) {
      usuario.setRacha(1);
    } else {
      // Calculamos los días de diferencia entre la última vez y hoy.
      long diasDiferencia = ChronoUnit.DAYS.between(fechaUltimaRacha, hoy);
      // CASO 2: Completó otra tarea hoy. La racha no cambia.
      if (diasDiferencia == 0) {
        // No se hace nada, la racha ya se contó para hoy.
        System.out.println("Racha diaria ya registrada. No se incrementa.");
        return; // Salimos del método para no actualizar la fecha innecesariamente
      } // CASO 3: La última tarea fue ayer. ¡La racha continúa!
      else if (diasDiferencia == 1) {
        System.out.println(
            "LOG: La racha del Usuario " + usuario.getNombreUsuario() + " ha aumentado.");
        usuario.setRacha(usuario.getRacha() + 1); // Incrementamos la racha existente
      } // CASO 4: La racha se rompió (pasó más de 1 día). Se reinicia a 1.
    }
    // Actualizamos la fecha de la racha a hoy.
    usuario.setFechaRacha(hoy);
  }

  @Transactional
  /**
   * Verifica si un usuario ha perdido su racha debido a inactividad y la reinicia si es necesario.
   * Utilizado específicamente en el Home.
   *
   * @param usuario Usuario para verificar la racha
   */
  public void verificarPerdidaRacha(Usuario usuario) {
    LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
    LocalDate fechaUltimaRacha = usuario.getFechaRacha();

    if (fechaUltimaRacha != null && usuario.getRacha() > 0) {
      long diasDiferencia = ChronoUnit.DAYS.between(fechaUltimaRacha, hoy);
      if (diasDiferencia > 1) {
        System.out.println("LOG: " + usuario.getNombreUsuario() + " ha perdido su racha.");
        usuario.setRacha(0);
      }
    }
  }

  public void sumarExperienciaTarea(Usuario usuario, int expTarea) {
    usuario.setExperiencia(usuario.getExperiencia() + expTarea);
  }

  /**
   * Obtiene el Top 5 de jugadores ordenados por la cantidad de logros completados. Este método
   * calcula el conteo en Java, ya que los logros son @Transient.
   *
   * @return Una lista de DTOs con el nombre y el conteo de logros.
   */
  public List<TopJugadorLogrosDto> getTop5JugadoresPorLogros() {
    // 1. Obtenemos TODOS los usuarios de la BD
    List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

    // 2. Creamos una lista para guardar los resultados (DTOs)
    List<TopJugadorLogrosDto> dtos = new ArrayList<>();

    // 3. Iteramos y calculamos el conteo para cada uno
    for (Usuario u : todosLosUsuarios) {
      // ¡Aquí usamos el GestorLogrosService!
      // Contamos cuántos logros ha cumplido este usuario según la lógica
      int conteo = gestorLogrosService.getTodosLogrosCumplidos(u).size();

      // Creamos el DTO con el resultado
      dtos.add(new TopJugadorLogrosDto(u.getNombreUsuario(), conteo));
    }

    // 4. Ordenamos la lista de DTOs en Java (de mayor a menor)
    dtos.sort((dto1, dto2) -> Long.compare(dto2.getTotalLogros(), dto1.getTotalLogros()));

    // 5. Devolvemos solo los primeros 5
    return dtos.stream().limit(5).collect(Collectors.toList());
  }

  // --- MÉTODO PARA LA TARJETA DE CONTEO ---
  /**
   * Obtiene el conteo total de todos los logros completados por todos los usuarios. (Calculado en
   * Java para la tarjeta de estadísticas).
   *
   * @return un long con el conteo total.
   */
  public long getConteoTotalLogrosCompletados() {
    long conteoTotal = 0;
    // Obtenemos todos los usuarios
    List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

    // Iteramos, calculamos el conteo de cada uno y lo sumamos
    for (Usuario u : todosLosUsuarios) {
      conteoTotal += gestorLogrosService.getTodosLogrosCumplidos(u).size();
    }
    return conteoTotal;
  }

  /**
   * Maneja toda la lógica que debe ocurrir al cargar un usuario (login). Al ser @Transactional,
   * mantiene la sesión de BD abierta y previene LazyInitializationException.
   *
   * @param correo El correo del usuario que acaba de iniciar sesión.
   */
  @Transactional
  public void manejarLogicaDeLogin(String correo) {
    // Volvemos a cargar el usuario DESDE DENTRO de la transacción.
    // Esto nos da un objeto "managed" (conectado) y previene el error.
    Usuario usuario = this.buscarPorCorreo(correo);
    if (usuario == null) {
      System.err.println(
          "Error en manejarLogicaDeLogin: No se encontró usuario con correo " + correo);
      return;
    }

    // 2. Actualiza la racha
    this.actualizarRacha(usuario);

    // 3. Dispara el gatillo de logros (ahora PUEDE acceder a usuario.getTareas)
    gestorLogrosService.actualizarLogrosParaUsuario(usuario);
    this.verificarSubidaDeNivel(usuario);
    // 4. Guarda todos los cambios
    this.guardarEnBd(usuario);
  }
}
