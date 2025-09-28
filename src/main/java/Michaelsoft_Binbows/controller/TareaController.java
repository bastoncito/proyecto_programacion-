package Michaelsoft_Binbows.controller;

import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import Michaelsoft_Binbows.services.*;

@Controller
public class TareaController {
    // Se inyecta la dependencia de BaseDatos, gracias a que BaseDatos es un Spring Bean (@Service).
    private final BaseDatos baseDatos;
    public TareaController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    @GetMapping("/tareas")
    public String mostrarTareas(Model model, HttpSession session) {
        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        System.out.println("LOG: El método 'mostrarTareas' ha sido llamado por una petición a /tareas.");
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        List<Tarea> tareas = usuarioActual != null ?usuarioActual.getTareas() : Collections.emptyList();
        model.addAttribute("tareas",  tareas);

        return "tareas";
    }

    @GetMapping("/nueva-tarea")
    public String mostrarFormularioNuevaTarea(Model model, HttpSession session) {
        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        System.out.println("LOG: El método 'mostrarFormularioNuevaTarea' ha sido llamado por una petición a /nueva-tarea.");
        return "tarea-nueva";
    }

    @PostMapping("/nueva-tarea")
    public String procesarNuevaTarea(
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion, 
        @RequestParam("dificultad") String dificultad,
        Model model,
        HttpSession session) {
        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        try{
            Tarea nuevaTarea = new Tarea(nombre, descripcion, dificultad);
            usuarioActual.agregarTarea(nuevaTarea);
            baseDatos.guardarBaseDatos();
            model.addAttribute("mensaje", "Tarea agregada exitosamente.");
            return "tarea-nueva";
        }catch(IllegalArgumentException e){
            System.out.println("ERROR: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "tarea-nueva";
    }

    @GetMapping("/tareas/historial")
    public String mostrarHistorialTTareas(Model model, HttpSession session) {
        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        model.addAttribute("nombre_usuario", usuarioActual != null ? usuarioActual.getNombreUsuario() : "-");

        return "";
    }

}
