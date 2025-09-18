// Asegúrate de que este paquete coincida exactamente con el de tus otras clases.
package Michaelsoft_Binbows.controller;   

// --- Imports necesarios de Spring Framework y Java ---
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // Necesario para el método de prueba

import Michaelsoft_Binbows.data.BaseDatos;
import Michaelsoft_Binbows.data.Usuario;

import java.util.List;

/**
 * Esta clase es un Controlador de Spring.v
 * Su responsabilidad es recibir peticiones web del navegador,
 * interactuar con la lógica de negocio (como la clase BaseDatos),
 * y decidir qué vista (archivo HTML) mostrarle al usuario.
 */
@Controller // ANOTACIÓN CLAVE: Marca esta clase para que Spring la reconozca como un controlador.
public class UsuarioController {

    // Creamos una instancia de nuestra capa de persistencia para poder usarla.
    // Nota: En proyectos más avanzados, esto se hace con una técnica llamada "Inyección de Dependencias".
    private BaseDatos baseDatos = new BaseDatos();

    /**
     * Este método maneja las peticiones a la URL "/usuarios".
     * @GetMapping("/usuarios") es la anotación que conecta la URL con este método.
     *
     * @param model Es un objeto que Spring nos proporciona para pasar datos desde el controlador a la vista HTML.
     * @return El nombre del archivo HTML (sin la extensión .html) que se debe renderizar.
     */
    @GetMapping("/usuarios")
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

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        // Si quieres mostrar mensajes de error que ya agregaste en el POST,
        // Thymeleaf los recibirá mediante el model ("error")
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        System.out.println("LOG: procesarLogin recibido: " + username);

        // Validación básica de campos
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Usuario y contraseña son requeridos.");
            return "login";
        }

        // Validación frente a la "base de datos" (recorre la lista y compara)
        for (Usuario u : baseDatos.getUsuarios()) {
            if (username.equals(u.getNombre_usuario()) && password.equals(u.getContraseña())) {
                // Credenciales válidas -> redirige a la lista de usuarios (o a la página deseada)
                System.out.println("LOG: Credenciales válidas, redirigiendo a /usuarios");
                return "redirect:/usuarios";
            }
        }

        // Si no se encontró coincidencia
        model.addAttribute("error", "Credenciales inválidas.");
        return "login";
    }

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
}