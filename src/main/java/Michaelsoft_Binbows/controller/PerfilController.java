package Michaelsoft_Binbows.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Michaelsoft_Binbows.CustomUserDetails;
import Michaelsoft_Binbows.exceptions.EdicionInvalidaException;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.Usuario;
import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilController {

    private final BaseDatos baseDatos;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(BaseDatos baseDatos, PasswordEncoder passwordEncoder) {
        this.baseDatos = baseDatos;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        System.out.println("LOG: Método 'mostrarPerfil' llamado.");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();

        model.addAttribute("usuario", usuarioActual.getNombreUsuario());
        model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        
        return "user_profile";
    }

    /**
     * Actualiza información personal
     */
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("usuario") String nuevoUsuario,
            @RequestParam("correo") String nuevoCorreo,
            Model model) {
        
        System.out.println("LOG: Intentando actualizar perfil.");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();

        //Verificar si hay cambios reales
        boolean hayUsuarioCambiado = !nuevoUsuario.equals(usuarioActual.getNombreUsuario());
        boolean hayCorreoCambiado = !nuevoCorreo.equals(usuarioActual.getCorreoElectronico());
        
        if (!hayUsuarioCambiado && !hayCorreoCambiado) {
            model.addAttribute("infoInfo", "No hay cambios que guardar.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        try {
            //Validar duplicados y formato
            baseDatos.actualizarUsuario(
                usuarioActual.getCorreoElectronico(), // correo original
                nuevoUsuario,                          // nuevo nombre
                nuevoCorreo,                           // nuevo correo
                usuarioActual.getRol()                 // mantener el rol
            );
            
            System.out.println("LOG: Perfil actualizado exitosamente para: " + nuevoUsuario);
            model.addAttribute("exitoInfo", "Información actualizada correctamente.");
            
            //Actualizar los datos en el modelo con los nuevos valores
            model.addAttribute("usuario", nuevoUsuario);
            model.addAttribute("correo", nuevoCorreo);
            
        } catch (EdicionInvalidaException e) {
            System.out.println("LOG: Error al actualizar perfil: " + e.getMessage());
            model.addAttribute("errorInfo", e.getMessage());
            // Mantener valores originales en caso de error
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        }

        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        return "user_profile";
    }

    /**
     * Cambiar contraseña usando baseDatos.actualizarContraseñaUsuario()
     */
    @PostMapping("/perfil/cambiar-contrasena")
    public String cambiarContrasena(
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("contrasenaNueva") String contrasenaNueva,
            @RequestParam("contrasenaRepetida") String contrasenaRepetida,
            Model model) {
        
        System.out.println("LOG: Intentando cambiar contraseña.");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();

        //Validar campos vacíos
        if (contrasenaActual == null || contrasenaActual.trim().isEmpty() ||
            contrasenaNueva == null || contrasenaNueva.trim().isEmpty() ||
            contrasenaRepetida == null || contrasenaRepetida.trim().isEmpty()) {
            model.addAttribute("errorPassword", "Todos los campos de contraseña son obligatorios.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        //Verificar contraseña actual (igual que en AutorizacionController)
        if (!passwordEncoder.matches(contrasenaActual, usuarioActual.getContraseña())) {
            model.addAttribute("errorPassword", "La contraseña actual es incorrecta.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        //Verificar que las contraseñas nuevas coincidan
        if (!contrasenaNueva.equals(contrasenaRepetida)) {
            model.addAttribute("errorPassword", "Las contraseñas nuevas no coinciden.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        //Verificar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(contrasenaNueva, usuarioActual.getContraseña())) {
            model.addAttribute("errorPassword", "La nueva contraseña debe ser diferente a la actual.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        try {
            //REUTILIZAR: baseDatos.actualizarContraseñaUsuario() valida y hashea automáticamente
            //1. Valida con usuario.setContraseña() (requisitos de seguridad)
            //2. Hashea con passwordEncoder.encode()
            //3. Guarda en la base de datos
            baseDatos.actualizarContraseñaUsuario(
                usuarioActual.getCorreoElectronico(),
                contrasenaNueva
            );

            System.out.println("LOG: Contraseña actualizada exitosamente para: " + usuarioActual.getNombreUsuario());
            model.addAttribute("exitoPassword", "Contraseña actualizada correctamente.");
            
        } catch (RegistroInvalidoException e) {
            //Captura errores de validación (contraseña débil, etc.)
            System.out.println("LOG: Error de validación al cambiar contraseña: " + e.getMessage());
            model.addAttribute("errorPassword", e.getMessage());
        } catch (Exception e) {
            //Captura errores inesperados
            System.out.println("LOG: Error inesperado al cambiar contraseña: " + e.getMessage());
            model.addAttribute("errorPassword", "Error al actualizar la contraseña. Intenta nuevamente.");
        }

        model.addAttribute("usuario", usuarioActual.getNombreUsuario());
        model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        
        return "user_profile";
    }

    @PostMapping("/perfil/borrar-cuenta")
    public String borrarCuenta(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();
        
        //Borrar la cuenta
        baseDatos.eliminarUsuario(usuarioActual);
        session.invalidate();
        System.out.println("LOG: Cuenta eliminada desde perfil para: " + usuarioActual.getNombreUsuario());
        return "redirect:/login";
    }
}