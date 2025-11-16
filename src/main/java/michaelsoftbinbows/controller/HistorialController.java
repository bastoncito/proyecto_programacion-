package michaelsoftbinbows.controller;

import java.util.List;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.services.AuthService;
import michaelsoftbinbows.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Controlador para gestionar la vista del historial de tareas completadas del usuario. */
@Controller
@RequestMapping("/historial")
public class HistorialController {

  @Autowired private AuthService authservice;
  @Autowired private UsuarioService usuarioService;

  /**
   * Muestra la página de historial de tareas del usuario actualmente logueado. Obtiene las tareas
   * completadas y las pasa a la vista.
   *
   * @param model El modelo de Spring para pasar datos a la plantilla.
   * @return El nombre de la plantilla "historial_tareas".
   */
  @GetMapping
  public String mostrarHistorial(Model model) {
    System.out.println(
        "LOG: El método 'mostrarHistorial' ha sido llamado desde HistorialController.");

    // 1. Obtener el usuario actual de forma segura
    Usuario usuarioActual =
        usuarioService.buscarPorCorreo(authservice.getCurrentUser().getCorreoElectronico());

    // 2. Manejar el caso en que el usuario no se encuentre
    if (usuarioActual == null) {
      return "redirect:/login";
    }

    // 3. Obtener la lista de tareas completadas
    List<Tarea> historial = usuarioActual.getTareasCompletadas();

    // 4. Añadir todos los datos necesarios al modelo
    model.addAttribute("usuario", usuarioActual);
    model.addAttribute("historialTareas", historial);
    model.addAttribute("activePage", "historial"); // Para que la navbar se ilumine

    // 5. Devolver el nombre de la plantilla HTML
    return "historial_tareas";
  }

  /*
   * TODO: RESUMENES SEMANALES
   * @GetMapping("/semanal")
   * public String mostrarResumenSemanal(Model model) {
   */
}
