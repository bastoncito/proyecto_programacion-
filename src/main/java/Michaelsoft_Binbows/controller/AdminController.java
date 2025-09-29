package Michaelsoft_Binbows.controller;

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
            Model model, 
            HttpSession session) {
        
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");

        // Filtro de seguridad básico
        if (usuarioActual == null) {
            return "redirect:/403";
        }
        if (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR) {
            return "redirect:/acceso-denegado"; 
        }

        // Preparamos los datos para la vista
        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("listaDeUsuarios", baseDatos.getUsuarios());
        model.addAttribute("seguridadService", this.seguridadService);
        
        // Si se pidió editar, buscamos al usuario y lo pasamos al modelo para el modal
        if (correoAEditar != null) {
            Usuario usuarioAEditar = baseDatos.buscarUsuarioPorCorreo(correoAEditar);
            if (usuarioAEditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAEditar)) {
                model.addAttribute("usuarioParaEditar", usuarioAEditar);
            }
        }
        
        return "admin";
    }

    /**
     * Procesa el guardado de los cambios de un usuario desde el modal.
     */
    @PostMapping("/admin/guardar")
    public String guardarUsuarioEditado(
            @RequestParam("nombreUsuario") String nuevoNombre,
            @RequestParam("correoElectronico") String correoOriginal,
            @RequestParam("rol") Rol nuevoRol,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario actor = (Usuario) session.getAttribute("usuarioActual");
        Usuario objetivo = baseDatos.buscarUsuarioPorCorreo(correoOriginal);

        // Chequeo de seguridad en el servidor
        if (!seguridadService.puedeEditar(actor, objetivo)) {
            return "redirect:/acceso-denegado";
        }

        // Se intentan aplicar los cambios. Si hay un error de validación, se atrapa.
        try {
            baseDatos.actualizarUsuario(correoOriginal, nuevoNombre, nuevoRol);
            redirectAttributes.addFlashAttribute("success", "Usuario '" + nuevoNombre + "' actualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin?editarUsuarioCorreo=" + correoOriginal; // Vuelve al modal con el error
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