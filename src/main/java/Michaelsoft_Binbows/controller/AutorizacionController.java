package Michaelsoft_Binbows.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import Michaelsoft_Binbows.CustomUserDetails;
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.Usuario;
import Michaelsoft_Binbows.services.Rol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@Controller
public class AutorizacionController {

    // Se inyecta la dependencia de BaseDatos, gracias a que BaseDatos es un Spring Bean (@Service).
    //(Véase BaseDatos.java para más detalles)

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final BaseDatos baseDatos;
    public AutorizacionController(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    //Guarda los usuarios nuevos con contraseñas encriptadas
    public void registrarUsuario(Usuario usuario) {
        String encodedPassword = passwordEncoder.encode(usuario.getContraseña());
        usuario.setContraseña(encodedPassword);
        baseDatos.agregarUsuario(usuario);
    }

    //Métodos para registro
    @GetMapping("/register")
    public String mostrarRegister(Model model, HttpSession session) {
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
            if(username.equals(u.getNombreUsuario())) {
                model.addAttribute("error", "El nombre de usuario ya existe.");
                return "register";
            }
            if(email.equals(u.getCorreoElectronico())) {
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
            registrarUsuario(nuevoUsuario);
            System.out.println("LOG: Nuevo usuario registrado: " + username);
            CustomUserDetails userDetails = new CustomUserDetails(nuevoUsuario);
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            System.out.println("LOG: Error al registrar usuario: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }

    //Métodos para login
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        System.out.println("LOG: El método 'mostrarLogin' ha sido llamado por una petición a /login.");
        return "loginreal";
    }
}