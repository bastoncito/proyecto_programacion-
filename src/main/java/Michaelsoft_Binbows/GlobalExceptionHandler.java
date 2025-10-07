package Michaelsoft_Binbows;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Michaelsoft_Binbows.exceptions.*;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(TareaInvalidaException.class)
    public String manejoTareaInvalidaException(TareaInvalidaException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "tarea-nueva";
    }

    @ExceptionHandler(RegistroInvalidoException.class)
    public String manejoRegistroInvalidoException(RegistroInvalidoException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }

    @ExceptionHandler(EdicionInvalidaException.class)
    public String manejoEdicionInvalidaException(EdicionInvalidaException ex, Model model, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        redirectAttributes.addAttribute("editarUsuarioCorreo", ex.getCorreo());
        return "redirect:/admin";
    }
}

