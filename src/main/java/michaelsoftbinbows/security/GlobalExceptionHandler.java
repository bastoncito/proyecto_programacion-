package michaelsoftbinbows.security;

import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.exceptions.AdminCrearTareaException;
import michaelsoftbinbows.exceptions.AdminCrearUsuarioException;
import michaelsoftbinbows.exceptions.AdminGuardarTareaException;
import michaelsoftbinbows.exceptions.EdicionInvalidaException;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.exceptions.WeatherApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador global de excepciones (Advice) para manejar errores de la aplicación de forma
 * centralizada y devolver vistas de error amigables.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Maneja las excepciones de TareaInvalidaException, redirigiendo al usuario a la página principal
   * con un mensaje de error y mostrando el modal de creación de tarea.
   *
   * @param ex La excepción capturada.
   * @param redirectAttributes Atributos para pasar datos durante la redirección.
   * @return La vista de redirección a la página principal ("redirect:/home").
   */
  @ExceptionHandler(TareaInvalidaException.class)
  public String manejoTareaInvalidaException(
      TareaInvalidaException ex, RedirectAttributes redirectAttributes) {

    redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
    redirectAttributes.addFlashAttribute("nombreTareaFallida", ex.getNombre());
    redirectAttributes.addFlashAttribute("descripcionTareaFallida", ex.getDescripcion());

    redirectAttributes.addAttribute("showCreateTaskModal", "true");

    return "redirect:/home";
  }

  /**
   * Maneja las excepciones de RegistroInvalidoException, devolviendo al usuario a la página de
   * registro con un mensaje de error.
   *
   * @param ex La excepción capturada.
   * @param model El modelo para pasar datos a la vista.
   * @return El nombre de la vista ("register").
   */
  @ExceptionHandler(RegistroInvalidoException.class)
  public String manejoRegistroInvalidoException(RegistroInvalidoException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    return "register";
  }

  /**
   * Maneja las excepciones de EdicionInvalidaException (Panel de Admin). Redirige al panel de admin
   * mostrando el modal de edición con el error.
   *
   * @param ex La excepción capturada.
   * @param model El modelo (no usado aquí, pero requerido).
   * @param redirectAttributes Para pasar el error tras la redirección.
   * @return Una redirección a /admin.
   */
  @ExceptionHandler(EdicionInvalidaException.class)
  public String manejoEdicionInvalidaException(
      EdicionInvalidaException ex, Model model, RedirectAttributes redirectAttributes) {
    System.err.println("ERROR: Fallo al actualizar usuario. Causa: " + ex.getMessage());
    redirectAttributes.addFlashAttribute("error", ex.getMessage());
    redirectAttributes.addAttribute("editarUsuarioCorreo", ex.getCorreo());
    return "redirect:/admin";
  }

  /**
   * Maneja las excepciones al crear tareas desde el panel de admin.
   *
   * @param ex La excepción capturada.
   * @param model El modelo.
   * @param redirectAttributes Para pasar el error tras la redirección.
   * @return Una redirección a /admin.
   */
  @ExceptionHandler(AdminCrearTareaException.class)
  public String manejoAdminCrearTareaException(
      AdminCrearTareaException ex, Model model, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errorCreacionTarea", ex.getMessage());
    redirectAttributes.addAttribute("crearTarea", true);
    redirectAttributes.addAttribute("vista", "tareas");
    return "redirect:/admin";
  }

  /**
   * Maneja las excepciones al crear usuarios desde el panel de admin.
   *
   * @param ex La excepción capturada.
   * @param model El modelo.
   * @param redirectAttributes Para pasar el error tras la redirección.
   * @return Una redirección a /admin.
   */
  @ExceptionHandler(AdminCrearUsuarioException.class)
  public String manejoAdminCrearUsuarioException(
      AdminCrearUsuarioException ex, Model model, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errorCreacion", ex.getMessage());
    redirectAttributes.addAttribute("crearUsuario", true);
    return "redirect:/admin";
  }

  /**
   * Maneja las excepciones al guardar tareas desde el panel de admin.
   *
   * @param ex La excepción capturada.
   * @param model El modelo.
   * @param redirectAttributes Para pasar el error tras la redirección.
   * @return El nombre de la vista ("admin-tarea-form").
   */
  @ExceptionHandler(AdminGuardarTareaException.class)
  public String manejoAdminGuardarTareaException(
      AdminGuardarTareaException ex, Model model, RedirectAttributes redirectAttributes) {

    model.addAttribute("error", ex.getMessage());
    model.addAttribute("usuario", ex.getUsuario());

    try {
      //  Usamos un constructor vacío y setters para evitar problemas si la dificultad es inválida.
      Tarea tareaConDatosPrevios = new Tarea();
      tareaConDatosPrevios.setNombre(ex.getNombreTarea());
      tareaConDatosPrevios.setDescripcion(ex.getDescripcionTarea());
      model.addAttribute("tarea", tareaConDatosPrevios);
    } catch (TareaInvalidaException ignored) {
      // Se ignora intencionalmente. Si falla la re-población de datos,
      // solo se muestra el error principal.
    }

    return "admin-tarea-form";
  }

  /**
   * Maneja las excepciones de la API del Clima (WeatherApiException). Devuelve una respuesta JSON
   * de error (ResponseEntity).
   *
   * @param ex La excepción capturada.
   * @return Un ResponseEntity con estado BAD_REQUEST y un JSON de error.
   */
  @ExceptionHandler(WeatherApiException.class)
  public ResponseEntity<String> handleWeatherApiException(WeatherApiException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("{\"error\": \"" + ex.getMessage() + "\"}");
  }
}
