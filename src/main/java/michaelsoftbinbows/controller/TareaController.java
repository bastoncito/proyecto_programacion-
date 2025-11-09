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
   * Procesa la creación de una nueva tarea para el usuario autenticado.
   * Valida que no se exceda el límite de 4 tareas pendientes antes de agregar la tarea.
   * 
   * @param nombre Nombre de la tarea a crear
   * @param descripcion Descripción de la tarea
   * @param dificultad Nivel de dificultad de la tarea
   * @param redirectAttributes Atributos para pasar mensajes de éxito durante la redirección
   * @return Redirección a la página principal ("redirect:/home")
   * @throws TareaInvalidaException Si el usuario intenta exceder el límite de 4 tareas pendientes
   */
  @PostMapping("/nueva-tarea")
  public String procesarNuevaTarea(
    @RequestParam("nombre") String nombre,
    @RequestParam("descripcion") String descripcion,
    @RequestParam("dificultad") String dificultad,
    RedirectAttributes redirectAttributes)
    throws TareaInvalidaException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();

    int tareasPendientes = usuarioService.obtenerTareasPendientes(correo).size();
    if (tareasPendientes >= 4) {
      throw new TareaInvalidaException("No puedes agregar más de 4 tareas pendientes.", nombre, descripcion);
    }

    Tarea nuevaTarea = new Tarea(nombre, descripcion, dificultad);
    usuarioService.agregarTareaAusuario(correo, nuevaTarea);

    redirectAttributes.addFlashAttribute("successMessage", "¡Tarea '" + nombre + "' agregada con éxito!");
    return "redirect:/home";
  }

}
