package Michaelsoft_Binbows.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import Michaelsoft_Binbows.services.BaseDatos;
import Michaelsoft_Binbows.services.Usuario;
import Michaelsoft_Binbows.services.Rol;

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
            @RequestParam("usuario") String username, //puede ser el nombre de usuario o correo electronico
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
            boolean credencialesValidas = password.equals(u.getContraseña()) && (username.equals(u.getNombreUsuario()) || username.equals(u.getCorreoElectronico()));

            if (credencialesValidas) {
                System.out.println("LOG: Credenciales válidas para usuario: " + u.getNombreUsuario());
                session.setAttribute("usuarioActual", u);
                if(u.getRol()==Rol.ADMIN){
                    System.out.println("LOG: El usuario es ADMIN. Redirigiendo a /admin.");
                    return "redirect:/admin"; // Si el usuario tiene rol ADMIN redirige la pagina a el panel de admin
                } else if (u.getRol()==Rol.MODERADOR){
                    System.out.println("LOG: El usuario es MODERADOR. Redirigiendo a /home (o a un panel de moderador).");
                    return "redirect:/home"; // Por ahora, los moderadores también van a /home, posiblemente podriamos hacer una pestaña de admin duplicada pero que en los admins no pueda editar
                }else{
                    return "redirect:/home";
                }
            }
        }
        
        // Si no se encontró coincidencia
        model.addAttribute("error", "Credenciales inválidas.");
        return "loginreal";
    }
    /*
    * Cierra la sesión del usuario actual.
    * Invalida la sesión HTTP para eliminar todos los atributos guardados (como 'usuarioActual')
    * y luego redirige al usuario a la página de inicio de sesión.
    *
    * @param session La sesión HTTP que se va a invalidar.
    * @return Una cadena de redirección a la página de login.
    */
    @PostMapping("/logout")
    public String procesarLogout(HttpSession session) {
        // Se invalida la sesión actual del usuario
        session.invalidate();
        System.out.println("LOG: Sesión cerrada exitosamente. Redirigiendo a /login.");
        // Se redirige a la página de inicio de sesión
        return "redirect:/login";
    }
}

