package michaelsoftbinbows.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dev") // Ruta especial para desarrollo
public class DevController {

  /** Controlador temporal para ver el perfil sin login Acceso: http://localhost:8080/dev/perfil */
  @GetMapping("/perfil")
  public String verPerfil(Model model) {
    // Datos de prueba (después los traerás del backend)
    model.addAttribute("usuario", "user 123");
    model.addAttribute("correo", "correo@ejemplo.com");
    model.addAttribute("nivel", "500");

    return "user_profile"; // Nombre del HTML (sin .html)
  }
}
