package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    @Autowired
    private UsuarioService usuarioService;

    // Este método ahora responde a GET "/historial"
    @GetMapping
    public String mostrarHistorial(Model model) {
        System.out.println("LOG: El método 'mostrarHistorial' ha sido llamado desde HistorialController.");

        // 1. Obtener el usuario actual de forma segura
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Usuario usuarioActual = usuarioService.buscarPorCorreoConTareas(userDetails.getUsername());

        // 2. Manejar el caso en que el usuario no se encuentre
        if (usuarioActual == null) {
            return "redirect:/login";
        }
        
        // 3. Obtener la lista de tareas completadas
        List<Tarea> historial = usuarioActual.getTareasCompletadas();

        // 4. Añadir todos los datos necesarios al modelo
        model.addAttribute("usuario", usuarioActual);
        model.addAttribute("historialTareas", historial);
        model.addAttribute("activePage", "historial"); // Para que la navbar se ilumine

        // 5. Devolver el nombre de la plantilla HTML
        return "historial_tareas";
    }

    /* 
     *  TODO: RESUMENES SEMANALES 
     * @GetMapping("/semanal")
     * public String mostrarResumenSemanal(Model model) {
    */
}
