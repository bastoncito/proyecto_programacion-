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

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(TareaInvalidaException.class)
  public String manejoTareaInvalidaException(TareaInvalidaException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    // Re-poblamos los datos que el usuario ya había escrito
    try {
      Tarea tareaConDatosPrevios = new Tarea();
      tareaConDatosPrevios.setNombre(ex.getNombre());
      tareaConDatosPrevios.setDescripcion(ex.getDescripcion());
      model.addAttribute("tarea", tareaConDatosPrevios);
    } catch (TareaInvalidaException ignored) {
    }
    return "tarea-nueva";
  }

  @ExceptionHandler(RegistroInvalidoException.class)
  public String manejoRegistroInvalidoException(RegistroInvalidoException ex, Model model) {
    model.addAttribute("error", ex.getMessage());
    return "register";
  }

  @ExceptionHandler(EdicionInvalidaException.class)
  public String manejoEdicionInvalidaException(
      EdicionInvalidaException ex, Model model, RedirectAttributes redirectAttributes) {
    System.err.println("ERROR: Fallo al actualizar usuario. Causa: " + ex.getMessage());
    redirectAttributes.addFlashAttribute("error", ex.getMessage());
    redirectAttributes.addAttribute("editarUsuarioCorreo", ex.getCorreo());
    return "redirect:/admin";
  }

  @ExceptionHandler(AdminCrearTareaException.class)
  public String manejoAdminCrearTareaException(
      AdminCrearTareaException ex, Model model, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errorCreacionTarea", ex.getMessage());
    redirectAttributes.addAttribute("crearTarea", true);
    redirectAttributes.addAttribute("vista", "tareas");
    return "redirect:/admin";
  }

  @ExceptionHandler(AdminCrearUsuarioException.class)
  public String manejoAdminCrearUsuarioException(
      AdminCrearUsuarioException ex, Model model, RedirectAttributes redirectAttributes) {
    redirectAttributes.addFlashAttribute("errorCreacion", ex.getMessage());
    redirectAttributes.addAttribute("crearUsuario", true);
    return "redirect:/admin";
  }

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
    }

    return "admin-tarea-form";
  }

  @ExceptionHandler(WeatherApiException.class)
  public ResponseEntity<String> handleWeatherApiException(WeatherApiException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body("{\"error\": \"" + ex.getMessage() + "\"}");
  }
}
