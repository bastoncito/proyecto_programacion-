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
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

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
    public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
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

    System.out.println("=== INICIO REGISTRO ===");
    System.out.println("Username: " + username);
    System.out.println("Email: " + email);
    System.out.println("Password length: " + password.length());
    
    if(!password.equals(passwordConfirm)) {
        model.addAttribute("error", "Las contraseñas no coinciden.");
        return "register";
    }
    
    try {
        System.out.println("1. Creando usuario...");
        Usuario nuevoUsuario = new Usuario(username, email, password);
        System.out.println("2. Usuario creado exitosamente");
        
        System.out.println("3. Llamando a registrarUsuario...");
        registrarUsuario(nuevoUsuario);
        System.out.println("4. registrarUsuario completado");
        
        System.out.println("5. Verificando en BD...");
        Usuario verificar = baseDatos.buscarUsuarioPorCorreo(email);
        if(verificar != null) {
            System.out.println("✓ Usuario encontrado en BD: " + verificar.getNombreUsuario());
        } else {
            System.out.println("✗ Usuario NO encontrado en BD después de guardar!");
        }
        
        CustomUserDetails userDetails = new CustomUserDetails(nuevoUsuario);
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        
        System.out.println("=== FIN REGISTRO EXITOSO ===");
        return "redirect:/home";
        
    } catch (Exception e) {
        System.out.println("✗✗✗ ERROR EN REGISTRO: " + e.getClass().getName());
        System.out.println("Mensaje: " + e.getMessage());
        e.printStackTrace();
        model.addAttribute("error", e.getMessage());
        return "register";
    }
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

}