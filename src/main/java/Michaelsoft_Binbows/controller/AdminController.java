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
    
    /*
     * Muestra la página principal del panel de administración.
     * 
     * Este método actúa como el controlador central para todas las vistas dentro del panel de admin.
     * Utiliza el parámetro 'vista' para determinar qué contenido dinámico (fragmento de Thymeleaf)
     * debe cargarse en la plantilla principal.
     * 
     * Realiza las siguientes acciones:
     * 1. Valida que el usuario en sesión sea un ADMIN o un MODERADOR. Si no, redirige al login.
     * 2. Carga los datos necesarios para la vista solicitada (ej. lista de usuarios para la vista 'usuarios').
     * 3. Prepara una lista de roles que el usuario actual tiene permitido asignar, para poblar los formularios de edición.
     * 4. Maneja la lógica para abrir el modal de edición de un usuario si se proporciona el parámetro 'editarUsuarioCorreo'.
     * 5. Pasa todos los datos necesarios al modelo para que la plantilla 'admin.html' los pueda renderizar.
     *
     * @param vistaActual El nombre de la vista a mostrar (ej. "usuarios", "tareas"). 
     *                    Viene de la URL como un parámetro '?vista=...'. Por defecto es "usuarios".
     * @param correoAEditar (Opcional) El correo del usuario cuyo modal de edición se debe mostrar.
     * @param error (Opcional) Un mensaje de error proveniente de una redirección (ej. un fallo al guardar).
     * @param model El objeto Model de Spring, usado para pasar atributos a la vista de Thymeleaf.
     * @param session La sesión HTTP actual, para obtener el usuario que ha iniciado sesión.
     * @return El nombre de la plantilla principal a renderizar ("admin").
     */
    @GetMapping("/admin")
    public String mostrarPanelAdmin(
            // Añadimos el parámetro "vista" para la navegación
            @RequestParam(name = "vista", required = false, defaultValue = "usuarios") String vistaActual, 
            @RequestParam(name = "editarUsuarioCorreo", required = false) String correoAEditar,
            @RequestParam(name = "error", required = false) String error,
            Model model, 
            HttpSession session) {
        
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");

        if (usuarioActual == null || (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR)) {
            return "redirect:/login";
        }

        // Pasamos la vista actual y el usuario al modelo
        model.addAttribute("vistaActual", vistaActual); 
        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("seguridadService", this.seguridadService);
        
        // --- LÓGICA DINÁMICA PARA LOS ROLES DISPONIBLES ---
        List<Rol> rolesDisponibles;
        if (usuarioActual.getRol() == Rol.ADMIN) {
            rolesDisponibles = Arrays.stream(Rol.values())
                                    .filter(r -> r == Rol.MODERADOR || r == Rol.USUARIO)
                                    .collect(Collectors.toList());
        } else if (usuarioActual.getRol() == Rol.MODERADOR) {
            rolesDisponibles = Arrays.asList(Rol.USUARIO);
        } else {
            rolesDisponibles = List.of();
        }
        model.addAttribute("rolesDisponibles", rolesDisponibles);
        
        // --- Lógica para cargar datos según la vista ---
        switch (vistaActual) {
            case "usuarios":
                model.addAttribute("listaDeUsuarios", baseDatos.getUsuarios());
                break;
            case "tareas":
                // Lógica futura
                break;
        }
        
        // Si se pulsa boton editar
        if (correoAEditar != null) {
            Usuario usuarioAEditar = baseDatos.buscarUsuarioPorCorreo(correoAEditar);
            if (usuarioAEditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAEditar)) {
                model.addAttribute("usuarioParaEditar", usuarioAEditar);
            }
        }
        
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }
        
        return "admin";
    }

    /*
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

        // Regla de negocio: Un admin NO puede crear otro admin desde el panel, ni un MODERADOR crear a otro MODERADOR
        if (!seguridadService.puedeAsignarRol(actor, nuevoRol)) {
            System.out.println("WARN: Intento no permitido de asignar el rol '" + nuevoRol + "'. Acción bloqueada.");
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para asignar el rol de " + nuevoRol + ".");
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