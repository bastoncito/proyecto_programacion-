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
        @GetMapping("/admin")
    public String mostrarAdmin(Model model, HttpSession session) {
        System.out.println("LOG: El método 'mostrarAdmin' ha sido llamado por una petición a /admin.");
        
        List<Usuario> todosLosUsuarios = baseDatos.getUsuarios();

        model.addAttribute("listaDeUsuarios", todosLosUsuarios);


        return "admin";
    }

}