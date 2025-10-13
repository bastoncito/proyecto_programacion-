package Michaelsoft_Binbows.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.exceptions.EdicionInvalidaException;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public PerfilController(UsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        System.out.println("LOG: Método 'mostrarPerfil' llamado.");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String correo = userDetails.getUsername(); // Si tu CustomUserDetails usa el correo como username
        Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

        // Verificación para evitar NullPointerException
        if (usuarioActual == null) {
            model.addAttribute("errorInfo", "No se encontró el usuario en la base de datos.");
            return "user_profile";
        }

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
        String correo = userDetails.getUsername(); // Si tu CustomUserDetails usa el correo como username
        Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

        if (usuarioActual == null) {
            model.addAttribute("errorInfo", "No se encontró el usuario en la base de datos.");
            return "user_profile";
        }       

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
            usuarioActual.setNombreUsuario(nuevoUsuario);
            usuarioActual.setCorreoElectronico(nuevoCorreo);
            // Si quieres mantener el rol, no necesitas cambiarlo
            usuarioService.guardar(usuarioActual); // Guarda los cambios en la base de datos
            
            System.out.println("LOG: Perfil actualizado exitosamente para: " + nuevoUsuario);
            model.addAttribute("exitoInfo", "Información actualizada correctamente.");
            
            //Actualizar los datos en el modelo con los nuevos valores
            model.addAttribute("usuario", nuevoUsuario);
            model.addAttribute("correo", nuevoCorreo);

            // --- ACTUALIZA EL USUARIO AUTENTICADO EN EL CONTEXTO DE SEGURIDAD ---
            CustomUserDetails nuevosDetalles = new CustomUserDetails(usuarioActual);
            Authentication nuevaAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                nuevosDetalles,
                auth.getCredentials(),
                nuevosDetalles.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(nuevaAuth);
            // ---------------------------------------------------------------------
            
        } catch (EdicionInvalidaException e) {
            System.out.println("LOG: Error al actualizar perfil: " + e.getMessage());
            model.addAttribute("errorInfo", e.getMessage());
            // Mantener valores originales en caso de error
            model.addAttribute("usuario", usuarioActual.getNombreUsuario());
            model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        } catch (RegistroInvalidoException e) {
            System.out.println("LOG: Error de registro inválido al actualizar perfil: " + e.getMessage());
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
        String correo = userDetails.getUsername();
        Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

        //Validar campos vacíos
        if (usuarioActual == null) {
        model.addAttribute("errorPassword", "No se encontró el usuario en la base de datos.");
        return "user_profile";
    }       

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

        String resultado = usuarioService.validarContrasena(contrasenaNueva);
        if (resultado != null) {
            model.addAttribute("errorPassword", resultado);
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
           usuarioActual.setContraseña(passwordEncoder.encode(contrasenaNueva));
           //usuarioService.guardarSinValidarContraseña(usuarioActual);
           usuarioService.guardarEnBD(usuarioActual); //Guarda la nueva contraseña en la base de datos

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
public String borrarCuenta(HttpSession session, Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();
    Usuario usuarioActual = usuarioService.buscarPorCorreo(correo);

    if (usuarioActual == null) {
            model.addAttribute("errorInfo", "No se encontró el usuario en la base de datos.");
            return "user_profile";
        }

    System.out.println("LOG: Intentando borrar usuario con ID: " + usuarioActual.getId());

    try {
        usuarioService.eliminar(usuarioActual.getId());
        System.out.println("LOG: Usuario eliminado correctamente.");
        session.invalidate();
        return "redirect:/login";
    } catch (Exception e) {
        System.out.println("LOG: Error al borrar usuario: " + e.getMessage());
        model.addAttribute("errorDelete", "No se pudo borrar la cuenta: " + e.getMessage());
        model.addAttribute("usuario", usuarioActual.getNombreUsuario());
        model.addAttribute("correo", usuarioActual.getCorreoElectronico());
        model.addAttribute("nivel", usuarioActual.getNivelExperiencia());
        return "user_profile";
    }
}
}