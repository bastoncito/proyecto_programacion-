package michaelsoftbinbows.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Ruta especial utilizada por los desarrolladores. */
@Controller
@RequestMapping("/dev")
public class DevController {

  /**
   * Controlador temporal para ver el perfil sin login Acceso: http://localhost:8080/dev/perfil.
   *
   * @param model model al que se le añadirán atributos
   * @return template de perfil
   */
  @GetMapping("/perfil")
  public String verPerfil(Model model) {
    model.addAttribute("usuario", "user 123");
    model.addAttribute("correo", "correo@ejemplo.com");
    model.addAttribute("nivel", "500");
    return "user_profile";
  }
}
