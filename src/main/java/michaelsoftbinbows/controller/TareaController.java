package michaelsoftbinbows.controller;

import java.util.Optional;  
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.dto.TareaDto;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.services.AuthService;
import michaelsoftbinbows.services.TareaService;
import michaelsoftbinbows.services.UsuarioService;
import michaelsoftbinbows.services.UsuarioTareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Controlador para vistas referentes a tareas. */
@Controller
public class TareaController {

  @Autowired private UsuarioService usuarioService;
  @Autowired private TareaService tareaService;
  @Autowired private AuthService authservice;
  @Autowired private UsuarioTareaService usuarioTareaService;

  /**
   * Elimina una tarea específica de un usuario.
   *
   * @param model modelo para añadir atributos
   * @param nombreTarea nombre de tarea a eliminar
   * @param redirectAttributes Atributos para pasar mensajes de éxito durante la redirección
   * @return redirect al home
   * @throws RegistroInvalidoException si la tarea no se puede eliminar
   */
  @PostMapping("/eliminar-tarea")
  public String eliminarTarea(Model model, @RequestParam("nombreTarea") String nombreTarea, RedirectAttributes redirectAttributes)
      throws RegistroInvalidoException {
    Long id = authservice.getCurrentUser().getId();
    tareaService.eliminarPorUsuarioYNombreTarea(id, nombreTarea);
    redirectAttributes.addFlashAttribute("successMessage", "Tarea '" + nombreTarea + "' eliminada correctamente.");
    return "redirect:/home";
  }

  /**
   * Marca una tarea de un usuario como completada.
   *
   * @param model modelo para añadir atributos
   * @param nombreTarea nombre de la tarea a completar
   * @param redirectAttributes Atributos para pasar mensajes de éxito durante la redirección
   * @return redirect al home
   * @throws RegistroInvalidoException si la tarea no se puede completar
   */
  @PostMapping("/completar-tarea")
  public String completarTarea(Model model, @RequestParam("nombreTarea") String nombreTarea, RedirectAttributes redirectAttributes)
      throws RegistroInvalidoException {
    Long idUsuario = authservice.getCurrentUser().getId();

    // Llamamos al método del TareaService para buscar la tarea PENDIENTE.
    Optional<Tarea> tareaPendienteOpt = 
        tareaService.obtenerTareaPendientePorNombreYUsuarioId(nombreTarea, idUsuario);

    // Verificamos si la tarea pendiente realmente existe.
    if (tareaPendienteOpt.isPresent()) {
      // Si existe, obtenemos su ID y llamamos al servicio para completarla.
      Long idTarea = tareaPendienteOpt.get().getId();
      usuarioTareaService.completarTarea(idUsuario, idTarea);
      redirectAttributes.addFlashAttribute("successMessage", "¡Felicidades! Has completado la tarea '" + nombreTarea + "'.");
    } else {
      // Si no se encuentra una tarea pendiente con ese nombre, lanzamos un error.
      throw new RegistroInvalidoException(
          "No se encontró una tarea pendiente con el nombre: " + nombreTarea);
    }
    
    return "redirect:/home";
  }

  /**
   * Procesa la creación de una nueva tarea para el usuario autenticado. Valida que no se exceda el
   * límite de 4 tareas pendientes antes de agregar la tarea.
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
    Long id = authservice.getCurrentUser().getId();
    String correo = authservice.getCurrentUser().getCorreoElectronico();

    int tareasPendientes = usuarioService.obtenerTareasPendientes(correo).size();
    if (tareasPendientes >= 4) {
      throw new TareaInvalidaException(
          "No puedes agregar más de 4 tareas pendientes.", nombre, descripcion);
    }
    TareaDto tareaDto = new TareaDto();
    tareaDto.nombre = nombre;
    tareaDto.descripcion = descripcion;
    tareaDto.dificultad = dificultad;
    tareaService.crear(tareaDto, id);

    redirectAttributes.addFlashAttribute(
        "successMessage", "¡Tarea '" + nombre + "' agregada con éxito!");
    return "redirect:/home";
  }
}
