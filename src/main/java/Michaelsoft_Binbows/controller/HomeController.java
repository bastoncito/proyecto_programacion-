    // Asegúrate de que este paquete coincida exactamente con el de tus otras clases.
    package Michaelsoft_Binbows.controller;   


    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    // --- Imports necesarios de Spring Framework y Java ---
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.ResponseBody; // Necesario para el método de prueba

import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.UsuarioService;
import jakarta.servlet.http.HttpSession;

    import java.util.Collections;
    import java.util.Comparator;
    import java.util.List;

    /**
     * Esta clase es un Controlador de Spring.v
     * Su responsabilidad es recibir peticiones web del navegador,
     * interactuar con la lógica de negocio (como la clase BaseDatos),
     * y decidir qué vista (archivo HTML) mostrarle al usuario.
     */
    @Controller // ANOTACIÓN CLAVE: Marca esta clase para que Spring la reconozca como un controlador.
    public class HomeController {

        // Se inyecta la dependencia de BaseDatos, gracias a que BaseDatos es un Spring Bean (@Service).
        //(Véase BaseDatos.java para más detalles)
        private final BaseDatos baseDatos;
        public HomeController(BaseDatos baseDatos) {
            this.baseDatos = baseDatos;
        }

        @Autowired
        private UsuarioService usuarioService;

        @GetMapping("/")
        public String redirigirLogin() {
            return "redirect:/login";
        }

        @GetMapping("/home")
        public String mostrarHome(Model model, HttpSession session) {
            System.out.println("LOG: El método 'mostrarMain' ha sido llamado por una petición a /home.");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String correo = userDetails.getUsername();
            
            Usuario usuarioActual = usuarioService.buscarPorCorreoConTareas(correo);
            usuarioActual.resetRacha();
            
            model.addAttribute("usuario", usuarioActual);
            
            // ✅ Tareas PENDIENTES (no completadas)
            List<Tarea> tareasPendientes = usuarioActual.getTareasPendientes();
            model.addAttribute("tareas", tareasPendientes);
            
            // ✅ Tareas COMPLETADAS (historial)
            List<Tarea> historialTareas = usuarioActual.getTareasCompletadas();
            model.addAttribute("historialTareas", historialTareas);
            
            return "home";
        }

        @GetMapping("/ranking")
        public String mostrarRanking(Model model) {
            System.out.println("LOG: El método 'mostrarRanking' ha sido llamado por una petición a /home.");
            //Se crean 2 listas para 2 tipos diferentes de ranking
            List<Usuario> rankingNivel = baseDatos.getUsuarios();
            List<Usuario> rankingCompletadas = baseDatos.getUsuarios();
            rankingNivel.sort(Comparator.comparing(Usuario::getNivelExperiencia).reversed());
            rankingCompletadas.sort(Comparator.comparing(Usuario::getNumeroCompletadas).reversed());
            model.addAttribute("rankingNivel", rankingNivel != null ? rankingNivel : Collections.emptyList());
            model.addAttribute("rankingCompletadas", rankingCompletadas != null ? rankingCompletadas : Collections.emptyList());

            return "ranking";
        }

        @GetMapping("/historial")
        public String mostrarHistorial(Model model) {
            System.out.println("LOG: El método 'mostrarHistorial' ha sido llamado por una petición a /historial.");
            
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            String correo = userDetails.getUsername();
            
            // Buscar el usuario con sus tareas cargadas
            Usuario usuarioActual = usuarioService.buscarPorCorreoConTareas(correo);
            
            // Obtener el historial completo de tareas completadas
            List<Tarea> historialTareas = usuarioActual.getTareasCompletadas();
            
            // Pasar datos al modelo
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("historialTareas", historialTareas);
            
            return "historial_tareas"; // nombre del archivo HTML sin .html
        }
    /*   
        @GetMapping("/perfil")
        public String mostrarPerfil(Model model, HttpSession session) {
            System.out.println("LOG: El método 'mostrarPerfil' ha sido llamado por una petición a /perfil.");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Usuario usuarioActual = userDetails.getUsuario(); 
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("nombre_usuario", usuarioActual != null ? usuarioActual.getNombreUsuario() : "-");

            return "user-profile";  // ← Este es el cambio crítico
        }
    */

        /**
         * Este es un método de diagnóstico para verificar que el controlador responde.
         * @ResponseBody le dice a Spring que no busque un archivo HTML, sino que devuelva
         * el texto de este método directamente como respuesta al navegador.
         */
        @GetMapping("/hola")
        @ResponseBody
        public String decirHola() {
            System.out.println("LOG: El método de prueba 'decirHola' ha sido llamado por una petición a /hola.");
            return "<h1>¡Éxito! La respuesta viene del controlador de Java.</h1>";
        }

        @GetMapping("/403")
        @ResponseBody
        public String mostrarError() {
            return "<title>Error</title><h1>403 - Forbidden</h1><h3>¿No se te está olvidando algo?</h3>";
        }
    }