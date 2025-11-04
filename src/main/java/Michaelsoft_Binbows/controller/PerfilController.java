package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PerfilController {

  private final UsuarioService usuarioService;
  private final PasswordEncoder passwordEncoder;

  public PerfilController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
    this.usuarioService = usuarioService;
    this.passwordEncoder = passwordEncoder;
  }

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

    model.addAttribute("usuarioLogueado", usuarioActual);
    model.addAttribute("activePage", "perfil");
    return "user_profile";
  }

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

      usuarioService.guardarEnBD(usuarioActual);

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

  @PostMapping("/perfil/cambiar-contrasena")
  public String cambiarContrasena(
      @RequestParam("contrasenaActual") String contrasenaActual,
      @RequestParam("contrasenaNueva") String contrasenaNueva,
      @RequestParam("contrasenaRepetida") String contrasenaRepetida,
      Model model) {

    System.out.println("LOG: Intentando cambiar contraseña.");
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
      if (!passwordEncoder.matches(contrasenaActual, usuarioActual.getContraseña())) {
        throw new Exception("La contraseña actual es incorrecta.");
      }
      if (!contrasenaNueva.equals(contrasenaRepetida)) {
        throw new Exception("Las contraseñas nuevas no coinciden.");
      }
      String resultado = usuarioService.validarContrasena(contrasenaNueva);
      if (resultado != null) {
        throw new Exception(resultado);
      }

      usuarioActual.setContraseña(passwordEncoder.encode(contrasenaNueva));
      usuarioService.guardarEnBD(usuarioActual);
      model.addAttribute("exitoPassword", "Contraseña actualizada correctamente.");

    } catch (Exception e) {
      model.addAttribute("errorPassword", e.getMessage());
    }

    model.addAttribute("usuarioLogueado", usuarioActual);
    return "user_profile";
  }

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
