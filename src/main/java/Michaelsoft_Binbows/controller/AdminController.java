package Michaelsoft_Binbows.controller;

import java.util.List;
import java.util.stream.Collectors;

// Imports de Spring y Java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

// Imports de nuestras propias clases
import Michaelsoft_Binbows.services.Rol;
import Michaelsoft_Binbows.services.Usuario;
import Michaelsoft_Binbows.services.SeguridadService;
import Michaelsoft_Binbows.services.BaseDatos;
import java.util.Arrays;

/**
 * Controlador para el panel de administración.
 * Maneja la visualización y gestión de usuarios.
 */
@Controller
public class AdminController {

    // Dependencias del controlador
    private final BaseDatos baseDatos;
    private final SeguridadService seguridadService;

    // Inyección de dependencias
    public AdminController(BaseDatos baseDatos, SeguridadService seguridadService) {
        this.baseDatos = baseDatos;
        this.seguridadService = seguridadService;
    }
    
    /**
     * Muestra la página principal del panel de admin.
     * Carga la lista de usuarios y prepara el modal de edición si es necesario.
     */
    @GetMapping("/admin")
    public String mostrarPanelAdmin(
            @RequestParam(name = "editarUsuarioCorreo", required = false) String correoAEditar,
            @RequestParam(name = "error", required = false) String error, // Para recibir errores de la redirección
            Model model, 
            HttpSession session) {
        
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");

        if (usuarioActual == null || (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR)) {
            return "redirect:/403";
        }

        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("listaDeUsuarios", baseDatos.getUsuarios());
        model.addAttribute("seguridadService", this.seguridadService);
        
        // Preparamos la lista de roles que un admin puede asignar (excluimos ADMIN)
        List<Rol> rolesDisponibles = Arrays.stream(Rol.values())
                                           .filter(r -> r != Rol.ADMIN)
                                           .collect(Collectors.toList());
        model.addAttribute("rolesDisponibles", rolesDisponibles);
        
        // Si se pidió editar, buscamos al usuario y lo pasamos al modelo para el modal
        if (correoAEditar != null) {
            Usuario usuarioAEditar = baseDatos.buscarUsuarioPorCorreo(correoAEditar);
            if (usuarioAEditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAEditar)) {
                model.addAttribute("usuarioParaEditar", usuarioAEditar);
            }
        }
        
        // Si hay un mensaje de error desde el proceso de guardado, lo añadimos al modelo
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }
        
        return "admin";
    }

        /**
     * Procesa el guardado de los cambios de un usuario desde el modal, con logging detallado.
     */
    @PostMapping("/admin/guardar")
    public String guardarUsuarioEditado(
            @RequestParam("nombreUsuario") String nuevoNombre,
            @RequestParam("correoElectronicoOriginal") String correoOriginal,
            @RequestParam("nuevoCorreo") String nuevoCorreo,
            @RequestParam("rol") Rol nuevoRol,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("\n--- INICIO PROCESO DE EDICIÓN DE USUARIO ---");

        Usuario actor = (Usuario) session.getAttribute("usuarioActual");
        Usuario objetivo = baseDatos.buscarUsuarioPorCorreo(correoOriginal);

        System.out.println("LOG: Actor '" + actor.getNombreUsuario() + "' (Rol: " + actor.getRol() + ") intenta editar a '" + objetivo.getNombreUsuario() + "'.");
        System.out.println("LOG: Datos recibidos del formulario:");
        System.out.println("LOG: -> Nuevo Nombre: '" + nuevoNombre + "'");
        System.out.println("LOG: -> Nuevo Correo: '" + nuevoCorreo + "'");
        System.out.println("LOG: -> Nuevo Rol: '" + nuevoRol + "'");

        // Chequeo de seguridad en el servidor
        if (!seguridadService.puedeEditar(actor, objetivo)) {
            System.out.println("WARN: Fallo de seguridad. El actor no tiene permisos para editar. Acción bloqueada.");
            return "redirect:/acceso-denegado";
        }

        // Regla de negocio: Un admin NO puede crear otro admin desde el panel
        if (nuevoRol == Rol.ADMIN) {
            System.out.println("WARN: Intento no permitido de asignar rol ADMIN. Acción bloqueada.");
            redirectAttributes.addFlashAttribute("error", "No se puede asignar el rol de ADMIN desde este panel.");
            redirectAttributes.addAttribute("editarUsuarioCorreo", correoOriginal);
            return "redirect:/admin";
        }
        
        System.out.println("LOG: Permisos verificados. Intentando aplicar cambios en la capa de datos...");

        // Se intentan aplicar los cambios. Si hay un error de validación, se atrapa.
        try {
            baseDatos.actualizarUsuario(correoOriginal, nuevoNombre, nuevoCorreo, nuevoRol);
            redirectAttributes.addFlashAttribute("success", "Usuario '" + nuevoNombre + "' actualizado correctamente.");
            System.out.println("SUCCESS: Usuario actualizado exitosamente en la base de datos.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Usamos System.err para que los errores se impriman en un color diferente en la consola
            System.err.println("ERROR: Fallo al actualizar usuario. Causa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addAttribute("editarUsuarioCorreo", correoOriginal);
            return "redirect:/admin";
        }
        
        return "redirect:/admin";
    }
    /**
     * Elimina un usuario.
     */
    @GetMapping("/admin/eliminar")
    public String eliminarUsuario(@RequestParam("correo") String correoAEliminar, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario actor = (Usuario) session.getAttribute("usuarioActual");
        Usuario objetivo = baseDatos.buscarUsuarioPorCorreo(correoAEliminar);

        // Chequeo de seguridad en el servidor
        if (!seguridadService.puedeEliminar(actor, objetivo)) {
            return "redirect:/acceso-denegado";
        }
        
        baseDatos.eliminarUsuario(objetivo);
        redirectAttributes.addFlashAttribute("success", "Usuario '" + objetivo.getNombreUsuario() + "' eliminado.");

        return "redirect:/admin";
    }
    
    /**
     * Página de "Acceso Denegado".
     */
    @GetMapping("/acceso-denegado")
    public String mostrarAccesoDenegado() {
        return "acceso-denegado"; 
    }
}