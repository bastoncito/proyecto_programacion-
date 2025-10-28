package Michaelsoft_Binbows.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.security.CustomUserDetails;
 
import Michaelsoft_Binbows.entities.Usuario;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Controller
public class AutorizacionController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private Michaelsoft_Binbows.services.UsuarioService usuarioService;

    //Guarda los usuarios nuevos con contraseñas encriptadas
    public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
        String encodedPassword = passwordEncoder.encode(usuario.getContraseña());
        usuario.setContraseña(encodedPassword);
        // Registrar usando el servicio para aplicar validaciones y unicidad
        usuarioService.guardarSinValidarContraseña(usuario);
    }

    //Métodos para registro
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
            Model model) throws RegistroInvalidoException {

        System.out.println("LOG: procesarRegister recibido: " + username + ", " + email);
        if(!password.equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "register";
        }
        //Validar la sintáxis del correo y contraseña
        //Si todo está bien, crear el nuevo usuario y agregarlo a la base de datos
    Usuario nuevoUsuario = new Usuario(username, email, password);
    // registrarUsuario encodifica y llama al servicio que valida unicidad
    registrarUsuario(nuevoUsuario);
    // usuario registrado; authentication already set below
        System.out.println("LOG: Nuevo usuario registrado: " + username);
        CustomUserDetails userDetails = new CustomUserDetails(nuevoUsuario);
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        return "redirect:/home";
    }

    /*
     * Por temas de Spring Security, este es la única excepción que no puedo manejar dentro de GlobalExceptionHandler
     */
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        System.out.println("LOG: El método 'mostrarLogin' ha sido llamado por una petición a /login.");
        if(error != null){
            System.out.println("LOG: Error al hacer login");
            model.addAttribute("error", "Credenciales inválidas");
        }
        return "loginreal";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        return "redirect:/home";
    }

}