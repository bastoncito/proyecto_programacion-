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

    @GetMapping("/home")
    public String mostrarMain(Model model) {
        System.out.println("LOG: El método 'mostrarMain' ha sido llamado por una petición a /home.");
        return "home";
    }

    @GetMapping("/register")
    public String mostrarRegister(Model model) {
        System.out.println("LOG: El método 'mostrarRegister' ha sido llamado por una petición a /register.");
        return "register";
    }
    @PostMapping("/register")
    public String procesarRegister(
            @RequestParam("usuario") String username,
            @RequestParam("email") String email,
            @RequestParam("contraseña1") String password,
            @RequestParam("contraseña2") String passwordConfirm,
            Model model) {

        System.out.println("LOG: procesarRegister recibido: " + username + ", " + email);
        // Validación básica de campos
        if(username == null || username.trim().isEmpty() ||
           email == null || email.trim().isEmpty() ||
           password == null || password.trim().isEmpty() ||
           passwordConfirm == null || passwordConfirm.trim().isEmpty()) {
            model.addAttribute("error", "Todos los campos son requeridos.");
            return "register";
        }
        //Validar si el usuario ya está registrado
        for(Usuario u : baseDatos.getUsuarios()) {
            if(username.equals(u.getNombre_usuario())) {
                model.addAttribute("error", "El nombre de usuario ya existe.");
                return "register";
            }
            if(email.equals(u.getCorreo_electronico())) {
                model.addAttribute("error", "El email ya está registrado.");
                return "register";
            }
        }
        //Validar si la contraseña y su confirmación coinciden
        if(!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }
        //Validar la sintáxis del correo y contraseña
        //Si todo está bien, crear el nuevo usuario y agregarlo a la base de datos
        try {
            Usuario nuevoUsuario = new Usuario(username, email, password);
            baseDatos.agregarUsuario(nuevoUsuario);
            System.out.println("LOG: Nuevo usuario registrado: " + username);
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            System.out.println("LOG: Error al registrar usuario: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        // Si quieres mostrar mensajes de error que ya agregaste en el POST,
        // Thymeleaf los recibirá mediante el model ("error")
        System.out.println("LOG: El método 'mostrarLogin' ha sido llamado por una petición a /login.");
        return "loginreal";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("usuario") String username,
            @RequestParam("contraseña") String password,
            Model model) {

        System.out.println("LOG: procesarLogin recibido: " + username);

        // Validación básica de campos
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Usuario y contraseña son requeridos.");
            return "loginreal";
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
        return "loginreal";
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

    @GetMapping("/error")
    @ResponseBody
    public String mostrarError() {
        System.out.println("LOG: El método 'mostrarError' ha sido llamado por una petición a /error.");
        return "<h1>Ups.</h1>";
    }
}