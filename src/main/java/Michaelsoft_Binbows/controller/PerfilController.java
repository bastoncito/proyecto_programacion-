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
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.Usuario;
import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilController {

    private final BaseDatos baseDatos;
    private final PasswordEncoder passwordEncoder;

    // Inyectar PasswordEncoder para manejar contraseñas hasheadas
    public PerfilController(BaseDatos baseDatos, PasswordEncoder passwordEncoder) {
        this.baseDatos = baseDatos;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Muestra la página de perfil del usuario actual con sus datos.
     */
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        System.out.println("LOG: Método 'mostrarPerfil' llamado.");
        
        // Obtener usuario autenticado mediante Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();

        // Pasar los datos del usuario al modelo para mostrarlos en la vista
        model.addAttribute("usuario", usuarioActual.getNombreUsuario());
        model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        
        return "user_profile";
    }

    /**
     * Actualiza la información personal del usuario (nombre y/o correo).
     * Solo guarda si hay cambios reales.
     */
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam("usuario") String nuevoUsuario,
            @RequestParam("correo") String nuevoCorreo,
            Model model) {
        
        System.out.println("LOG: Intentando actualizar perfil.");
        
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();

        // Validar que los campos no estén vacíos
        if (nuevoUsuario == null || nuevoUsuario.trim().isEmpty() ||
            nuevoCorreo == null || nuevoCorreo.trim().isEmpty()) {
            model.addAttribute("errorInfo", "Los campos no pueden estar vacíos.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        // Verificar si hay cambios reales
        boolean hayUsuarioCambiado = !nuevoUsuario.equals(usuarioActual.getNombreUsuario());
        boolean hayCorreoCambiado = !nuevoCorreo.equals(usuarioActual.getCorreoElectronico());
        
        if (!hayUsuarioCambiado && !hayCorreoCambiado) {
            model.addAttribute("infoInfo", "No hay cambios que guardar.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        // Si cambió el nombre de usuario, verificar que no esté repetido
        if (hayUsuarioCambiado) {
            for (Usuario u : baseDatos.getUsuarios()) {
                if (u.getNombreUsuario().equals(nuevoUsuario) && 
                    !u.getCorreoElectronico().equals(usuarioActual.getCorreoElectronico())) {
                    model.addAttribute("errorInfo", "El nombre de usuario ya está en uso.");
                    model.addAttribute("usuario", usuarioActual.getNombreUsuario());
                    model.addAttribute("correo", usuarioActual.getCorreoElectronico());
                    model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
                    return "user_profile";
                }
            }
        }

        // Si cambió el correo, verificar que no esté repetido
        if (hayCorreoCambiado) {
            for (Usuario u : baseDatos.getUsuarios()) {
                if (u.getCorreoElectronico().equals(nuevoCorreo) && 
                    !u.getCorreoElectronico().equals(usuarioActual.getCorreoElectronico())) {
                    model.addAttribute("errorInfo", "El correo electrónico ya está registrado.");
                    model.addAttribute("usuario", usuarioActual.getNombreUsuario());
                    model.addAttribute("correo", usuarioActual.getCorreoElectronico());
                    model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
                    return "user_profile";
                }
            }
        }

        // Intentar actualizar los datos
        try {
            if (hayUsuarioCambiado) {
                usuarioActual.setNombreUsuario(nuevoUsuario);
            }
            if (hayCorreoCambiado) {
                usuarioActual.setCorreoElectronico(nuevoCorreo);
            }
            
            baseDatos.guardarBaseDatos(); // Guardar en base de datos los cambios
            
            System.out.println("LOG: Perfil actualizado exitosamente para: " + usuarioActual.getNombreUsuario());
            model.addAttribute("exitoInfo", "Información actualizada correctamente.");
            
        } catch (Exception e) {
            System.out.println("LOG: Error al actualizar perfil: " + e.getMessage());
            model.addAttribute("errorInfo", e.getMessage());
        }

        // Recargar datos en el modelo
        model.addAttribute("usuario", usuarioActual.getNombreUsuario());
        model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        
        return "user_profile";
    }

    /**
     * Cambia la contraseña del usuario actual.
     * Valida la contraseña actual usando PasswordEncoder, que las nuevas coincidan y cumplan requisitos.
     */
    @PostMapping("/perfil/cambiar-contrasena")
    public String cambiarContrasena(
            @RequestParam("contrasenaActual") String contrasenaActual,
            @RequestParam("contrasenaNueva") String contrasenaNueva,
            @RequestParam("contrasenaRepetida") String contrasenaRepetida,
            Model model) {
        
        System.out.println("LOG: Intentando cambiar contraseña.");
        
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();

        // Validar que los campos no estén vacíos
        if (contrasenaActual == null || contrasenaActual.trim().isEmpty() ||
            contrasenaNueva == null || contrasenaNueva.trim().isEmpty() ||
            contrasenaRepetida == null || contrasenaRepetida.trim().isEmpty()) {
            model.addAttribute("errorPassword", "Todos los campos de contraseña son obligatorios.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        // Verificar contraseña hasheada usando PasswordEncoder
        if (!passwordEncoder.matches(contrasenaActual, usuarioActual.getContraseña())) {
            model.addAttribute("errorPassword", "La contraseña actual es incorrecta.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        // Verificar que las contraseñas nuevas coincidan
        if (!contrasenaNueva.equals(contrasenaRepetida)) {
            model.addAttribute("errorPassword", "Las contraseñas nuevas no coinciden.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        // Verificar que la nueva contraseña no sea igual a la actual
        if (passwordEncoder.matches(contrasenaNueva, usuarioActual.getContraseña())) {
            model.addAttribute("errorPassword", "La nueva contraseña debe ser diferente a la actual.");
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
            model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
            return "user_profile";
        }

        // Intentar cambiar la contraseña
        try {
            //Hashear la nueva contraseña antes de guardarla
            String contrasenaHasheada = passwordEncoder.encode(contrasenaNueva);
            usuarioActual.setContraseña(contrasenaHasheada);

            baseDatos.guardarBaseDatos(); // Guardar en base de datos los cambios

            System.out.println("LOG: Contraseña actualizada exitosamente para: " + usuarioActual.getNombreUsuario());
            model.addAttribute("exitoPassword", "Contraseña actualizada correctamente.");
            
        } catch (Exception e) {
            System.out.println("LOG: Error al cambiar contraseña: " + e.getMessage());
            model.addAttribute("errorPassword", e.getMessage());
        }

        // Recargar datos en el modelo
        model.addAttribute("usuario", usuarioActual.getNombreUsuario());
        model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        
        return "user_profile";
    }

    /**
     * Elimina la cuenta del usuario actual.
     */
    @PostMapping("/perfil/borrar-cuenta")
    public String borrarCuenta(HttpSession session) {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = userDetails.getUsuario();
        
        baseDatos.eliminarUsuario(usuarioActual);
        session.invalidate();
        System.out.println("LOG: Cuenta eliminada desde perfil para: " + usuarioActual.getNombreUsuario());
        return "redirect:/login";
    }
}