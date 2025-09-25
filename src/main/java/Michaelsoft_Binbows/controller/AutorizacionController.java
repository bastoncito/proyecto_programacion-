package Michaelsoft_Binbows.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import Michaelsoft_Binbows.data.BaseDatos;
import Michaelsoft_Binbows.data.Usuario;

@Controller
public class AutorizacionController {

    // Se inyecta la dependencia de BaseDatos, gracias a que BaseDatos es un Spring Bean (@Service).
    //(Véase BaseDatos.java para más detalles)
    private final BaseDatos baseDatos;
    public AutorizacionController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    //Métodos para registro
    @GetMapping("/register")
    public String mostrarRegister(Model model, HttpSession session) {
        session.removeAttribute("usuarioActual");
        System.out.println("LOG: El método 'mostrarRegister' ha sido llamado por una petición a /register.");
        return "register";
    }

    @PostMapping("/register")
    public String procesarRegister(
            @RequestParam("usuario") String username,
            @RequestParam("email") String email,
            @RequestParam("contraseña1") String password,
            @RequestParam("contraseña2") String passwordConfirm,
            HttpSession session,
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
            session.setAttribute("usuarioActual", nuevoUsuario);
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            System.out.println("LOG: Error al registrar usuario: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }

    //Métodos para login
    @GetMapping("/login")
    public String mostrarLogin(Model model, HttpSession session) {
        session.removeAttribute("usuarioActual");
        System.out.println("LOG: El método 'mostrarLogin' ha sido llamado por una petición a /login.");
        return "loginreal";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("usuario") String username,
            @RequestParam("contraseña") String password,
            HttpSession session,
            Model model) {

        System.out.println("LOG: procesarLogin recibido: " + username);

        // Validación básica de campos
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Usuario y contraseña son requeridos.");
            return "loginreal";
        }

        // Validación frente a la "base de datos" (recorre la lista y compara)
        for (Usuario u : baseDatos.getUsuarios()) {
            boolean credencialesValidas = password.equals(u.getContraseña()) && (username.equals(u.getNombre_usuario()) || username.equals(u.getCorreo_electronico()));

            if (credencialesValidas) {
                System.out.println("LOG: Credenciales válidas para usuario: " + u.getNombre_usuario());
                session.setAttribute("usuarioActual", u);
            return "redirect:/home";
            }
        }
        
        // Si no se encontró coincidencia
        model.addAttribute("error", "Credenciales inválidas.");
        return "loginreal";
    }
}
