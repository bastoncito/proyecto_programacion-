package michaelsoftbinbows.controller;

import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Controller para el apartado de login/registro. */
@Controller
public class AutorizacionController {

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private michaelsoftbinbows.services.UsuarioService usuarioService;

  /**
   * Guarda usuarios con contraseñas encriptadas.
   *
   * @param usuario usuario a guardar
   * @throws RegistroInvalidoException si el usuario a guardar no es válido
   */
  public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
    String encodedPassword = passwordEncoder.encode(usuario.getContrasena());
    usuario.setContrasena(encodedPassword);
    // Registrar usando el servicio para aplicar validaciones y unicidad
    usuarioService.guardarSinValidarContrasena(usuario);
  }

  /**
   * Muestra la pantalla de registro.
   *
   * @param model modelo para añadir atributos
   * @return template de register
   */
  @GetMapping("/register")
  public String mostrarRegister(Model model) {
    System.out.println(
        "LOG: El método 'mostrarRegister' ha sido llamado por una petición a /register.");
    return "register";
  }

  /**
   * Procesa el registro de un nuevo usuario.
   *
   * @param username nombre del usuario nuevo
   * @param email correo nuevo
   * @param password contraseña del usuario nuevo
   * @param passwordConfirm confirmación de la contraseña
   * @param model modelo para añadir atributos
   * @return redirect a home o template de register
   * @throws RegistroInvalidoException si el usuario a registrar no es válido
   */
  @PostMapping("/register")
  public String procesarRegister(
      @RequestParam("usuario") String username,
      @RequestParam("email") String email,
      @RequestParam("contrasena1") String password,
      @RequestParam("contrasena2") String passwordConfirm,
      Model model)
      throws RegistroInvalidoException {

    System.out.println("LOG: procesarRegister recibido: " + username + ", " + email);
    if (!password.equals(passwordConfirm)) {
      model.addAttribute("error", "Las contrasenas no coinciden.");
      return "register";
    }
    // Validar la sintáxis del correo y contrasena
    // Si todo está bien, crear el nuevo usuario y agregarlo a la base de datos
    Usuario nuevoUsuario = new Usuario(username, email, password);
    // registrarUsuario encodifica y llama al servicio que valida unicidad
    registrarUsuario(nuevoUsuario);
    // usuario registrado; authentication already set below
    System.out.println("LOG: Nuevo usuario registrado: " + username);
    CustomUserDetails userDetails = new CustomUserDetails(nuevoUsuario);
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);
    return "redirect:/home";
  }

  /**
   * Muestra la pantalla de login.
   *
   * @param error mensaje de error si es que hace falta
   * @param model modelo para añadir atributos
   * @return template de login
   */
  @GetMapping("/login")
  public String mostrarLogin(
      @RequestParam(value = "error", required = false) String error, Model model) {
    System.out.println("LOG: El método 'mostrarLogin' ha sido llamado por una petición a /login.");
    if (error != null) {
      System.out.println("LOG: " + error);
      model.addAttribute("error", "Credenciales inválidas");
    }
    return "login";
  }
}
