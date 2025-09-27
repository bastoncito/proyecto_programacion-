// Asegúrate de que este paquete coincida exactamente con el de tus otras clases.
package Michaelsoft_Binbows.controller;   

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
// --- Imports necesarios de Spring Framework y Java ---
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // Necesario para el método de prueba

import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.Tarea;
import Michaelsoft_Binbows.services.Usuario;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.List;

/**
 * Esta clase es un Controlador de Spring.v
 * Su responsabilidad es recibir peticiones web del navegador,
 * interactuar con la lógica de negocio (como la clase BaseDatos),
 * y decidir qué vista (archivo HTML) mostrarle al usuario.
 */
@Controller // ANOTACIÓN CLAVE: Marca esta clase para que Spring la reconozca como un controlador.
public class AdminController {

    // Se inyecta la dependencia de BaseDatos, gracias a que BaseDatos es un Spring Bean (@Service).
    //(Véase BaseDatos.java para más detalles)
    private final BaseDatos baseDatos;
    public AdminController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    @GetMapping("/admin/usuarios")
    public String mostrarListaDeUsuarios(Model model) {
        
        System.out.println("LOG: El método 'mostrarListaDeUsuarios' ha sido llamado por una petición a /usuarios.");

        // 1. Obtener los datos usando nuestra lógica de negocio existente.
        List<Usuario> listaDeUsuarios = baseDatos.getUsuarios();
        
        // 2. Agregar la lista de usuarios al "model".
        //    Esto es como poner los datos en una bandeja para llevarlos a la mesa (el HTML).
        //    La clave "usuariosParaLaVista" es el nombre que usaremos en el archivo HTML para acceder a esta lista.
        model.addAttribute("usuariosParaLaVista", listaDeUsuarios);
        
        // 3. Devolver el nombre de la plantilla.
        //    Spring buscará un archivo llamado "lista-usuarios.html" dentro de la carpeta "src/main/resources/templates/".
        return "lista-usuarios";
    }

    @GetMapping("/admin")
    public String mostrarAdmin(Model model, HttpSession session) {
        System.out.println("LOG: El método 'mostrarAdmin' ha sido llamado por una petición a /admin.");
        /* 
        Completar cuándo esté listo el login de Admin

        if(session.getAttribute("usuarioActual") == null){
            return "redirect:/error";
        }
        Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActual");
        model.addAttribute("nombre_usuario", usuarioActual != null ? usuarioActual.getNombre_usuario() : "-");
        */
        return "admin";
    }
}