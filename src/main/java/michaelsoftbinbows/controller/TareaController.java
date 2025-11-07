package michaelsoftbinbows.controller;

import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.security.CustomUserDetails;
import michaelsoftbinbows.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Controlador para vistas referentes a tareas. */
@Controller
public class TareaController {

  @Autowired private UsuarioService usuarioService;

  /**
   * Elimina una tarea específica de un usuario.
   *
   * @param model modelo para añadir atributos
   * @param nombreTarea nombre de tarea a eliminar
   * @return redirect al home
   * @throws RegistroInvalidoException si la tarea no se puede eliminar
   */
  @PostMapping("/eliminar-tarea")
  public String eliminarTarea(Model model, @RequestParam("nombreTarea") String nombreTarea)
      throws RegistroInvalidoException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();
    usuarioService.eliminarTarea(correo, nombreTarea);
    return "redirect:/home";
  }

  /**
   * Marca una tarea de un usuario como completada.
   *
   * @param model modelo para añadir atributos
   * @param nombreTarea nombre de la tarea a completar
   * @return redirect al home
   * @throws RegistroInvalidoException si la tarea no se puede completar
   */
  @PostMapping("/completar-tarea")
  public String completarTarea(Model model, @RequestParam("nombreTarea") String nombreTarea)
      throws RegistroInvalidoException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();

    usuarioService.completarTarea(correo, nombreTarea);
    return "redirect:/home";
  }

  /**
   * Muestra el formulario para que el usuario cree una nueva tarea.
   *
   * @param model modelo para añadir atributos
   * @return template para tarea nueva
   */
  @GetMapping("/nueva-tarea")
  public String mostrarFormularioNuevaTarea(Model model) {
    // No es estrictamente necesario pasar un objeto Tarea vacío,
    // pero lo mantenemos por si la plantilla lo necesita.
    model.addAttribute("tarea", new Tarea());
    return "tarea-nueva";
  }

  /**
   * Procesa la creación de una nueva tarea para el usuario actual.
   *
   * @param nombre nombre de la tarea nueva
   * @param descripcion descripcion de la tarea nueva
   * @param dificultad dificultad de la tarea nueva
   * @param model modelo para añadir atributos
   * @param redirectAttributes atributos para redirect (mensajes de éxito)
   * @return template para tarea nueva
   * @throws TareaInvalidaException si la tarea no es válida
   */
  @PostMapping("/nueva-tarea")
  public String procesarNuevaTarea(
      @RequestParam("nombre") String nombre,
      @RequestParam("descripcion") String descripcion,
      @RequestParam("dificultad") String dificultad,
      Model model,
      RedirectAttributes redirectAttributes)
      throws TareaInvalidaException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();

    Tarea nuevaTarea = new Tarea(nombre, descripcion, dificultad);
    usuarioService.agregarTareaAUsuario(correo, nuevaTarea);

    // Usamos RedirectAttributes para que el mensaje de éxito se vea en /home
    redirectAttributes.addFlashAttribute("mensaje", "¡Tarea agregada con éxito!");
    return "tarea-nueva";
  }
}
