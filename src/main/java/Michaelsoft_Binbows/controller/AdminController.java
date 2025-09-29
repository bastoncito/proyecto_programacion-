// Ubicación del controlador
package Michaelsoft_Binbows.controller;   

// Imports de Spring y Java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

// Imports de nuestras propias clases
import Michaelsoft_Binbows.services.Rol;
import Michaelsoft_Binbows.services.Usuario;
import Michaelsoft_Binbows.services.SeguridadService;
import Michaelsoft_Binbows.services.BaseDatos;

import java.util.List;

/**
 * Controlador para gestionar el panel de administración.
 * Maneja las rutas que empiezan con /admin.
 */
@Controller
public class AdminController {

    // Dependencias que necesita el controlador para funcionar.
    // Son 'final' porque se asignan una vez en el constructor y no cambian.
    private final BaseDatos baseDatos;
    private final SeguridadService seguridadService;

    // Constructor para la Inyección de Dependencias.
    // Spring se encarga de pasarnos las instancias de BaseDatos y SeguridadService.
    public AdminController(BaseDatos baseDatos, SeguridadService seguridadService) {
        this.baseDatos = baseDatos;
        this.seguridadService = seguridadService;
    }
    
    /**
     * Muestra la página principal del panel de administración.
     * Carga la lista de usuarios y verifica los permisos del visitante.
     */
    @GetMapping("/admin")
    public String mostrarPanelAdmin(Model model, HttpSession session) {
        
        // Obtenemos el usuario que inició sesión desde su "mochila" (la sesión).
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");

        // --- Filtro de Seguridad ---
        // Si nadie ha iniciado sesión, lo mandamos al login.
        if (usuarioActual == null) {
            System.out.println("LOG: Acceso denegado a /admin (no hay sesión). Redirigiendo a /login.");
            return "redirect:/login";
        }
        
        // Si el usuario no es ADMIN o MODERADOR, no debería estar aquí.
        if (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR) {
            System.out.println("LOG: Acceso denegado a /admin para el rol " + usuarioActual.getRol() + ".");
            return "redirect:/acceso-denegado"; 
        }

        // Si pasa los filtros, preparamos la página.
        System.out.println("LOG: Acceso a /admin concedido para: " + usuarioActual.getNombreUsuario());
        
        // 1. Pedimos a la base de datos la lista de todos los usuarios.
        List<Usuario> todosLosUsuarios = baseDatos.getUsuarios();
        
        // 2. "Empaquetamos" los datos para que el HTML pueda usarlos.
        model.addAttribute("usuarioActual", usuarioActual); // Para saber quién está viendo la página.
        model.addAttribute("listaDeUsuarios", todosLosUsuarios); // La lista para la tabla.
        model.addAttribute("seguridadService", this.seguridadService); // La herramienta para chequear permisos.
        
        // 3. Le decimos a Spring que renderice el archivo "admin.html".
        return "admin";
    }

}