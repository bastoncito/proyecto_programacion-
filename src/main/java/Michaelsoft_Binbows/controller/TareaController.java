package Michaelsoft_Binbows.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

@Controller
public class TareaController {
    // Se inyecta la dependencia de BaseDatos, gracias a que BaseDatos es un Spring Bean (@Service).
    /*private final BaseDatos baseDatos;
    public TareaController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }*/

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/eliminar-tarea")
    public String eliminarTarea(Model model, @RequestParam("nombreTarea") String nombreTarea) throws RegistroInvalidoException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String correo = userDetails.getUsername();
        /*Usuario usuarioActual = userDetails.getUsuario();
        usuarioActual.cancelarTarea(nombreTarea);
        usuarioService.guardarEnBD(usuarioActual); //Guarda el usuario y sus tareas en la base de datos*/

        usuarioService.eliminarTarea(correo, nombreTarea);

        return "redirect:/home";
    }

    @PostMapping("/completar-tarea")
    public String completarTarea(Model model, @RequestParam("nombreTarea") String nombreTarea) throws RegistroInvalidoException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        /*Usuario usuarioActual = userDetails.getUsuario();
        usuarioActual.completarTarea(nombreTarea);*/
        String correo = userDetails.getUsername();

        usuarioService.completarTarea(correo, nombreTarea);
        //usuarioService.guardarEnBD(usuarioActual); // Guarda el usuario y sus tareas en la base de datos
        return "redirect:/home";
    }
    /**
     * Muestra el formulario para que el usuario cree una nueva tarea.
     */
    @GetMapping("/nueva-tarea")
    public String mostrarFormularioNuevaTarea(Model model) {
        // No es estrictamente necesario pasar un objeto Tarea vacío,
        // pero lo mantenemos por si la plantilla lo necesita.
        model.addAttribute("tarea", new Tarea());
        return "tarea-nueva";
    }

    /**
     * Procesa la creación de una nueva tarea para el usuario actual.
     * @throws TareaInvalidaException 
     * @throws RegistroInvalidoException 
     */
    @PostMapping("/nueva-tarea")
    public String procesarNuevaTarea(
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion, 
        @RequestParam("dificultad") String dificultad,
        Model model,
        RedirectAttributes redirectAttributes) throws TareaInvalidaException, RegistroInvalidoException{
            
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String correo = userDetails.getUsername();
        //Usuario usuarioActual = userDetails.getUsuario(); 

        Tarea nuevaTarea = new Tarea(nombre, descripcion, dificultad);
        usuarioService.agregarTareaAUsuario(correo, nuevaTarea);
        //usuarioService.guardarEnBD(usuarioActual); //Guarda el usuario y sus tareas en la base de datos

        // Usamos RedirectAttributes para que el mensaje de éxito se vea en /home
        redirectAttributes.addFlashAttribute("mensaje", "¡Tarea agregada con éxito!");
        return "tarea-nueva";
    }
    /* 
    @GetMapping("/tareas/historial")
    public String mostrarHistorialTTareas(Model model, HttpSession session) {
        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        model.addAttribute("nombre_usuario", usuarioActual != null ? usuarioActual.getNombreUsuario() : "-");

        return "";
    }
        |*/

            /* 
    @GetMapping("/tareas")
    public String mostrarTareas(Model model, HttpSession session) {
        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        @GetMapping("/eliminar")

        System.out.println("LOG: El método 'mostrarTareas' ha sido llamado por una petición a /tareas.");
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        List<Tarea> tareas = usuarioActual != null ?usuarioActual.getTareas() : Collections.emptyList();
        model.addAttribute("tareas",  tareas);

        return "tareas";
    }
    */

}
