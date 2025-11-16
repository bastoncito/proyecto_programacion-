package michaelsoftbinbows.controller;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.time.format.DateTimeFormatter;

import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.entities.UsuarioLogro;
import michaelsoftbinbows.services.AuthService;
import michaelsoftbinbows.services.GestorLogrosService;
import michaelsoftbinbows.services.LogroService;
import michaelsoftbinbows.services.UsuarioService;
import michaelsoftbinbows.util.UsuarioValidator;
import org.springframework.beans.factory.annotation.Autowired;
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
  private UsuarioValidator usuarioValidator = new UsuarioValidator();
  @Autowired private GestorLogrosService gestorLogrosService;
  @Autowired private AuthService authservice;

    @Autowired
    private AuthService authService;

    @Autowired
    private LogroService logroService;

  /**
   * Constructor de clase.
   *
   * <p>Inyecta los servicios y objetos necesarios para su funcionamiento.
   *
   * @param usuarioService Service para acciones de Usuario
   * @param passwordEncoder encriptador de contraseñas BCrypt
   * 
   * 
   */
  public PerfilController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
    this.usuarioService = usuarioService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
  * Muestra la página de perfil del usuario logueado.
  */
  @GetMapping("/perfil")
  public String mostrarPerfil(Model model) {
        
    String correo = authService.getCurrentUser().getCorreoElectronico();
    
    // 1. Ejecuta la lógica de login (previene LazyInitializationException)
    usuarioService.manejarLogicaDeLogin(correo);
    
    // 2. Volvemos a cargar el usuario con todos sus datos actualizados
    Usuario usuario = usuarioService.buscarPorCorreo(correo);

    
    // --- PREPARAR DATOS PARA LA VISTA ---
    
    // 1. Obtenemos TODOS los logros que existen en la BD (para la cuadrícula)
    //    Nos aseguramos de que solo sean los "activos"
    List<Logro> allAchievements = logroService.obtenerTodosActivos();

    // 2. Obtenemos las asociaciones de logros del usuario (UsuarioLogro)
    List<UsuarioLogro> unlockedAssociations = usuario.getUsuarioLogros();

    // 3. Creamos un Map (diccionario) de las asociaciones desbloqueadas
    Map<String, UsuarioLogro> unlockedMap = unlockedAssociations.stream()
            .collect(Collectors.toMap(
                ul -> ul.getLogro().getId(), // La clave (ej. "REACH_GOLD")
                ul -> ul,                  // El valor (el objeto UsuarioLogro)
                (existing, replacement) -> existing // ¡LA SOLUCIÓN!
            ));
    
    // 4. Creamos un Set (conjunto) solo con los IDs de los logros desbloqueados

    Set<String> unlockedIds = unlockedMap.keySet();


    // 5. Añadimos todo al modelo
    model.addAttribute("usuarioLogueado", usuario); // Para los formularios
    model.addAttribute("allAchievements", allAchievements); // Para el bucle th:each
    model.addAttribute("unlockedAchievementsIds", unlockedIds); // Para la clase .locked
    model.addAttribute("unlockedMap", unlockedMap); // ¡Para poder buscar la fecha!

    model.addAttribute("activePage", "perfil"); // Para la navbar
    
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
    Usuario usuarioActual = authservice.getCurrentUser();

    if (usuarioActual == null) {
      model.addAttribute("errorInfo", "Error crítico: No se encontró el usuario.");
      model.addAttribute("usuarioLogueado", new Usuario());
      return "user_profile";
    }

    try {
      usuarioService.actualizarUsuario(
          usuarioActual.getCorreoElectronico(),
          nuevoUsuario,
          nuevoCorreo,
          usuarioActual.getRol(),
          nuevaCiudad);

      model.addAttribute("exitoInfo", "Información actualizada correctamente.");

      // Actualizar el contexto de seguridad
      authservice.actualizarSesion(usuarioActual.getId());

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
    Usuario usuarioActual = authservice.getCurrentUser();

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
      String resultado = usuarioValidator.validarContrasena(contrasenaNueva);
      if (resultado != null) {
        throw new Exception(resultado);
      }

      usuarioActual.setContrasena(passwordEncoder.encode(contrasenaNueva));
      usuarioService.guardarEnBd(usuarioActual);
      authservice.actualizarSesion(usuarioActual.getId());
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
    Usuario usuarioActual = authservice.getCurrentUser();

    if (usuarioActual != null) {
      try {
        usuarioService.eliminarUsuario(usuarioActual.getId());
        SecurityContextHolder.clearContext();
        session.invalidate();
      } catch (Exception e) {
        return "redirect:/perfil?error=deleteFailed";
      }
    }
    return "redirect:/login?logout";
  }
}
