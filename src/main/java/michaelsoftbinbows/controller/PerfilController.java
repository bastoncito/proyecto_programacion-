package michaelsoftbinbows.controller;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.entities.UsuarioLogro;
import michaelsoftbinbows.exceptions.CambioContrasenaException;
import michaelsoftbinbows.exceptions.EdicionUsuarioException;
import michaelsoftbinbows.exceptions.PerfilActualizacionException;
import michaelsoftbinbows.services.AuthService;
import michaelsoftbinbows.services.LogroService;
import michaelsoftbinbows.services.UsuarioService;
import michaelsoftbinbows.util.UsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Controller para el apartado de perfil. */
@Controller
public class PerfilController {

  private final UsuarioService usuarioService;
  private final PasswordEncoder passwordEncoder;
  private UsuarioValidator usuarioValidator = new UsuarioValidator();

  private static final String UPLOAD_DIR = "uploads/";

  @Autowired private AuthService authService;

  @Autowired private LogroService logroService;

  /**
   * Constructor de clase.
   *
   * <p>Inyecta los servicios y objetos necesarios para su funcionamiento.
   *
   * @param usuarioService Service para acciones de Usuario
   * @param passwordEncoder encriptador de contraseñas BCrypt
   */
  public PerfilController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
    this.usuarioService = usuarioService;
    this.passwordEncoder = passwordEncoder;
  }

  /** Muestra la página de perfil del usuario logueado. */
  @GetMapping("/perfil")
  public String mostrarPerfil(Model model) {

    String correo = authService.getCurrentUser().getCorreoElectronico();

    // 2. Volvemos a cargar el usuario con todos sus datos actualizados (incluye tareas)
    Usuario usuario = usuarioService.buscarPorCorreoConTareas(correo);

    // --- PREPARAR DATOS PARA LA VISTA ---

    // 1. Obtenemos TODOS los logros que existen en la BD (para la cuadrícula)
    //    Nos aseguramos de que solo sean los "activos"
    List<Logro> allAchievements = logroService.obtenerTodosActivos();

    // 2. Obtenemos las asociaciones de logros del usuario (UsuarioLogro)
    List<UsuarioLogro> unlockedAssociations = usuario.getUsuarioLogros();

    // 3. Creamos un Map (diccionario) de las asociaciones desbloqueadas
    Map<String, UsuarioLogro> unlockedMap =
        unlockedAssociations.stream()
            .collect(
                Collectors.toMap(
                    ul -> ul.getLogro().getId(), // La clave (ej. "REACH_GOLD")
                    ul -> ul, // El valor (el objeto UsuarioLogro)
                    (existing, replacement) -> existing // ¡LA SOLUCIÓN!
                    ));

    // 4. Creamos un Set (conjunto) solo con los IDs de los logros desbloqueados

    Set<String> unlockedIds = unlockedMap.keySet();

    // 5. Calculamos los contadores de logros
    int logrosObtenidosCount = unlockedIds.size();
    int logrosTotalesCount = allAchievements.size();

    // 6. Añadimos todo al modelo
    model.addAttribute("usuarioLogueado", usuario); // Para los formularios
    model.addAttribute("allAchievements", allAchievements); // Para el bucle th:each
    model.addAttribute("unlockedAchievementsIds", unlockedIds); // Para la clase .locked
    model.addAttribute("unlockedMap", unlockedMap); // ¡Para poder buscar la fecha!
    model.addAttribute("activePage", "perfil"); // Para la navbar
    // Para mostrar logros obtenidos/totales
    model.addAttribute("logrosObtenidosCount", logrosObtenidosCount);
    model.addAttribute("logrosTotalesCount", logrosTotalesCount);
    return "user_profile";
  }

  /**
   * Procesa intentos de actualización de usuario desde su perfil.
   *
   * @param nuevoUsuario nuevo nombre de usuario
   * @param nuevoCorreo nuevo correo
   * @param nuevaCiudad nueva ciudad
   * @param redirectAttributes para pasar mensajes a través de la redirección
   * @return redirección al template de perfil
   * @throws PerfilActualizacionException si hay error en la actualización
   */
  @PostMapping("/perfil/actualizar")
  public String actualizarPerfil(
      @RequestParam("usuario") String nuevoUsuario,
      @RequestParam("correo") String nuevoCorreo,
      @RequestParam("ciudad") String nuevaCiudad,
      RedirectAttributes redirectAttributes)
      throws PerfilActualizacionException {

    System.out.println("LOG: Intentando actualizar perfil.");
    Usuario usuarioActual = authService.getCurrentUser();

    if (usuarioActual == null) {
      redirectAttributes.addFlashAttribute(
          "errorInfo", "Error crítico: No se encontró el usuario.");
      return "redirect:/perfil";
    }

    try {
      usuarioService.actualizarUsuario(
          usuarioActual.getCorreoElectronico(),
          nuevoUsuario,
          nuevoCorreo,
          usuarioActual.getRol(),
          nuevaCiudad);
    } catch (EdicionUsuarioException e) {
      throw new PerfilActualizacionException(e.getMessage());
    }

    redirectAttributes.addFlashAttribute("exitoInfo", "Información actualizada correctamente.");

    // Actualizar el contexto de seguridad
    authService.actualizarSesion(usuarioActual.getId());

    return "redirect:/perfil";
  }

  /**
   * Procesa intentos de cambio de contraseña desde perfil.
   *
   * @param contrasenaActual contraseña actual del usuario
   * @param contrasenaNueva contraseña nueva del usuario
   * @param contrasenaRepetida confirmación de la contraseña nueva
   * @param redirectAttributes para pasar mensajes a través de la redirección
   * @return redirección al template de perfil
   * @throws CambioContrasenaException si hay error en el cambio de contraseña
   */
  @PostMapping("/perfil/cambiar-contrasena")
  public String cambiarContrasena(
      @RequestParam("contrasenaActual") String contrasenaActual,
      @RequestParam("contrasenaNueva") String contrasenaNueva,
      @RequestParam("contrasenaRepetida") String contrasenaRepetida,
      RedirectAttributes redirectAttributes)
      throws CambioContrasenaException {

    System.out.println("LOG: Intentando cambiar contrasena.");
    Usuario usuarioActual = authService.getCurrentUser();

    if (usuarioActual == null) {
      throw new CambioContrasenaException("Error crítico: No se encontró el usuario.");
    }

    if (!passwordEncoder.matches(contrasenaActual, usuarioActual.getContrasena())) {
      throw new CambioContrasenaException("La contrasena actual es incorrecta.");
    }
    if (!contrasenaNueva.equals(contrasenaRepetida)) {
      throw new CambioContrasenaException("Las contrasenas nuevas no coinciden.");
    }
    String resultado = usuarioValidator.validarContrasena(contrasenaNueva);
    if (resultado != null) {
      throw new CambioContrasenaException(resultado);
    }

    usuarioActual.setContrasena(passwordEncoder.encode(contrasenaNueva));
    usuarioService.guardarEnBd(usuarioActual);
    authService.actualizarSesion(usuarioActual.getId());
    redirectAttributes.addFlashAttribute("exitoPassword", "Contrasena actualizada correctamente.");

    return "redirect:/perfil";
  }

  /**
   * Elimina la cuenta del usuario.
   *
   * @param session sesión HTTP
   * @return redirect al login (logout) o a perfil si hay un error
   */
  @PostMapping("/perfil/borrar-cuenta")
  public String borrarCuenta(HttpSession session) throws Exception {
    Usuario usuarioActual = authService.getCurrentUser();

    if (usuarioActual != null) {
      usuarioService.eliminarUsuario(usuarioActual.getId());
      SecurityContextHolder.clearContext();
      session.invalidate();
    }
    return "redirect:/login?logout";
  }

  /**
   * Procesa la carga de un nuevo avatar para el usuario.
   *
   * @param archivo El archivo de imagen a subir.
   * @param redirectAttributes Para pasar mensajes a través de la redirección.
   * @return Redirección al perfil del usuario.
   */
  @PostMapping("/perfil/avatar/subir")
  public String subirAvatar(
      @RequestParam("archivo") MultipartFile archivo, RedirectAttributes redirectAttributes) {
    Usuario usuarioActual = authService.getCurrentUser();

    // 1. Validaciones básicas
    if (archivo.isEmpty()) {
      redirectAttributes.addFlashAttribute("errorInfo", "Por favor selecciona un archivo.");
      return "redirect:/perfil";
    }

    // Validar formato (solo imágenes)
    String contentType = archivo.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      redirectAttributes.addFlashAttribute(
          "errorInfo", "Solo se permiten archivos de imagen (JPG, PNG).");
      return "redirect:/perfil";
    }

    try {
      // 2. Crear el directorio si no existe
      Path uploadPath = Paths.get(UPLOAD_DIR);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      // Antes de subir el nuevo, limpiamos la casa
      if (usuarioActual.getAvatarUrl() != null) {
        borrarFotoDelDisco(usuarioActual.getAvatarUrl());
      }

      // 3. Generar nombre único para evitar colisiones (ej: usuario123_uuid.png)
      // Usamos UUID para que el navegador no cachee la imagen vieja si subes una nueva
      String extension = StringUtils.getFilenameExtension(archivo.getOriginalFilename());
      String nombreArchivo =
          "avatar_" + usuarioActual.getId() + "_" + UUID.randomUUID().toString() + "." + extension;

      // 4. Guardar el archivo en el disco
      Path rutaCompleta = uploadPath.resolve(nombreArchivo);
      Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

      // 5. Borrar avatar anterior si existía (opcional, para no llenar el disco)
      // (Lógica simple: si tenía avatar, podrías borrar el archivo viejo aquí)

      // 6. Guardar la URL en la base de datos
      // IMPORTANTE: La URL debe ser accesible desde el navegador.
      // Configuraremos Spring para que "/uploads/**" apunte a la carpeta física.
      String urlPublica = "/uploads/" + nombreArchivo;

      usuarioActual.setAvatarUrl(urlPublica);
      usuarioService.guardarEnBd(usuarioActual);

      // Actualizar sesión
      authService.actualizarSesion(usuarioActual.getId());

      redirectAttributes.addFlashAttribute("exitoInfo", "¡Avatar actualizado con éxito!");

    } catch (IOException e) {
      e.printStackTrace();
      redirectAttributes.addFlashAttribute(
          "errorInfo", "Error al subir la imagen: " + e.getMessage());
    }

    return "redirect:/perfil";
  }

  /**
   * Elimina el avatar del usuario actual.
   *
   * @param redirectAttributes Para pasar mensajes a través de la redirección.
   * @return Redirección al perfil del usuario.
   */
  @PostMapping("/perfil/avatar/eliminar")
  public String eliminarAvatar(RedirectAttributes redirectAttributes) {
    Usuario usuarioActual = authService.getCurrentUser();

    if (usuarioActual != null) {

      borrarFotoDelDisco(usuarioActual.getAvatarUrl());

      // 1. Ponemos null en la BD
      usuarioActual.setAvatarUrl(null);
      usuarioService.guardarEnBd(usuarioActual);

      // 2. Actualizamos la sesión para que se refleje inmediatamente
      authService.actualizarSesion(usuarioActual.getId());

      redirectAttributes.addFlashAttribute(
          "exitoInfo", "Avatar eliminado. Se usarán tus iniciales.");
    }

    return "redirect:/perfil";
  }

  // --- METODO PARA BORRAR EL ARCHIVO FÍSICO DE LA IMAGEN ---
  private void borrarFotoDelDisco(String urlAvatar) {
    if (urlAvatar != null && !urlAvatar.isEmpty()) {
      try {
        // La URL es "/uploads/nombre.png", pero el archivo está en "uploads/nombre.png"
        // Quitamos la primera barra "/" si es necesario o extraemos el nombre
        String nombreArchivo = urlAvatar.replace("/uploads/", "");

        Path rutaArchivo = Paths.get(UPLOAD_DIR).resolve(nombreArchivo);

        // Borramos el archivo si existe
        Files.deleteIfExists(rutaArchivo);
        System.out.println("LOG: Archivo borrado correctamente: " + nombreArchivo);

      } catch (IOException e) {
        System.err.println("LOG: No se pudo borrar el archivo antiguo: " + e.getMessage());
        // No lanzamos error para no interrumpir el flujo del usuario
      }
    }
  }
}
