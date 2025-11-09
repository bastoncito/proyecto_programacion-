package michaelsoftbinbows.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.util.GestorLogros;
import jakarta.servlet.http.HttpSession;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.security.CustomUserDetails;
import michaelsoftbinbows.services.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Controller para el apartado de perfil. */
@Controller
public class PerfilController {

  private final UsuarioService usuarioService;
  private final PasswordEncoder passwordEncoder;

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

  /**
   * Muestra el perfil de usuario.
   *
   * @param model modelo para añadir atributos a la página
   * @return template de perfil
   */
  @GetMapping("/perfil")
  public String mostrarPerfil(Model model) {
    System.out.println("LOG: Método 'mostrarPerfil' llamado.");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();
    Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

    if (usuarioActual == null) {
      return "redirect:/login?error=userNotFound";
    }

    // --- SIMULACIÓN DE DATOS DE LOGROS ---
    // Lista de TODOS los logros disponibles en la app.
    // 1. Obtenemos la lista completa de logros desde GestorLogros.
    List<Logro> allAchievements = GestorLogros.getLogrosDisponibles();
    // 2. Lista de los logros que el usuario SÍ ha desbloqueado.
    // En el futuro, esta lista vendrá del objeto 'usuarioActual'.
    // Por ahora, la simulamos con datos fijos.
    Set<String> unlockedAchievementsIds = Set.of("COMPLETE_1_TASK", "REACH_LEVEL_5", "JOIN_APP");



    model.addAttribute("allAchievements", allAchievements);
    model.addAttribute("unlockedAchievementsIds", unlockedAchievementsIds);

    model.addAttribute("usuarioLogueado", usuarioActual);
    model.addAttribute("activePage", "perfil");
    return "user_profile";
  }

  /**
   * Procesa intentos de actualización de usuario desde su perfil.
   *
   * @param nuevoUsuario nuevo nombre de usuario
   * @param nuevoCorreo nuevo correo
   * @param nuevaCiudad nueva ciudad
   * @param model modelo para añadir atributos a la página
   * @return template de perfil
   */
  @PostMapping("/perfil/actualizar")
  public String actualizarPerfil(
      @RequestParam("usuario") String nuevoUsuario,
      @RequestParam("correo") String nuevoCorreo,
      @RequestParam("ciudad") String nuevaCiudad,
      Model model) {

    System.out.println("LOG: Intentando actualizar perfil.");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();
    Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

    if (usuarioActual == null) {
      model.addAttribute("errorInfo", "Error crítico: No se encontró el usuario.");
      model.addAttribute("usuarioLogueado", new Usuario());
      return "user_profile";
    }

    try {
      usuarioActual.setNombreUsuario(nuevoUsuario);
      usuarioActual.setCorreoElectronico(nuevoCorreo);
      usuarioActual.setCiudad(nuevaCiudad);

      usuarioService.guardarEnBd(usuarioActual);

      model.addAttribute("exitoInfo", "Información actualizada correctamente.");

      // Actualizar el contexto de seguridad
      CustomUserDetails nuevosDetalles = new CustomUserDetails(usuarioActual);
      Authentication nuevaAuth =
          new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
              nuevosDetalles, auth.getCredentials(), nuevosDetalles.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(nuevaAuth);

    } catch (Exception e) {
      System.out.println("LOG: Error al actualizar perfil: " + e.getMessage());
      model.addAttribute("errorInfo", e.getMessage());
    }

    model.addAttribute("usuarioLogueado", usuarioActual);
    return "user_profile";
  }

  /**
   * Procesa intentos de cambio de contraseña desde perfil.
   *
   * @param contrasenaActual contraseña actual del usuario
   * @param contrasenaNueva contraseña nueva del usuario
   * @param contrasenaRepetida confirmación de la contraseña nueva
   * @param model modelo para añadir atributos a la página
   * @return template de perfil
   */
  @PostMapping("/perfil/cambiar-contrasena")
  public String cambiarContrasena(
      @RequestParam("contrasenaActual") String contrasenaActual,
      @RequestParam("contrasenaNueva") String contrasenaNueva,
      @RequestParam("contrasenaRepetida") String contrasenaRepetida,
      Model model) {

    System.out.println("LOG: Intentando cambiar contrasena.");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();
    Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

    if (usuarioActual == null) {
      model.addAttribute("errorPassword", "Error crítico: No se encontró el usuario.");
      model.addAttribute("usuarioLogueado", new Usuario());
      return "user_profile";
    }

    try {
      if (!passwordEncoder.matches(contrasenaActual, usuarioActual.getContrasena())) {
        throw new Exception("La contrasena actual es incorrecta.");
      }
      if (!contrasenaNueva.equals(contrasenaRepetida)) {
        throw new Exception("Las contrasenas nuevas no coinciden.");
      }
      String resultado = usuarioService.validarContrasena(contrasenaNueva);
      if (resultado != null) {
        throw new Exception(resultado);
      }

      usuarioActual.setContrasena(passwordEncoder.encode(contrasenaNueva));
      usuarioService.guardarEnBd(usuarioActual);
      model.addAttribute("exitoPassword", "Contrasena actualizada correctamente.");

    } catch (Exception e) {
      model.addAttribute("errorPassword", e.getMessage());
    }

    model.addAttribute("usuarioLogueado", usuarioActual);
    return "user_profile";
  }

  /**
   * Elimina la cuenta del usuario.
   *
   * @param session sesión HTTP
   * @return redirect al login (logout) o a perfil si hay un error
   */
  @PostMapping("/perfil/borrar-cuenta")
  public String borrarCuenta(HttpSession session) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();
    Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

    if (usuarioActual != null) {
      try {
        usuarioService.eliminar(usuarioActual.getId());
        SecurityContextHolder.clearContext();
        session.invalidate();
      } catch (Exception e) {
        return "redirect:/perfil?error=deleteFailed";
      }
    }
    return "redirect:/login?logout";
  }
}
