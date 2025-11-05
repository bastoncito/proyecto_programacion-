package michaelsoftbinbows;
/*
package Michaelsoft_Binbows;

import Michaelsoft_Binbows.dto.*;
import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.services.TareaService;
import Michaelsoft_Binbows.services.UsuarioService;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Ejecutora implements CommandLineRunner {
  InputStream file = getClass().getResourceAsStream("/usuarios.txt");
  BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  @Autowired UsuarioService usuarioService;
  @Autowired TareaService tareaService;

  @Override
  public void run(String... args) throws Exception {
    Scanner scanner = new Scanner(file);
    ArrayList<Usuario> usuarios = new ArrayList<>();
    while (scanner.hasNext()) {
      String correo = scanner.nextLine();
      try {
        Usuario usuario = usuarioService.buscarPorCorreo(correo);
        usuarios.add(usuario);
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }
    }
    TareaDTO tareaDTO = new TareaDTO();
    tareaDTO.nombre = "Batería de tareas 2";
    tareaDTO.descripcion = "Porque faltaban puntos xd";
    tareaDTO.dificultad = switch ((int) (Math.random() * 4)) {
      case 0 -> "Muy fácil";
      case 1 -> "Fácil";
      case 2 -> "Medio";
      case 3 -> "Difícil";
      case 4 -> "Muy difícil";
      default -> "Medio";
    };
    for (Usuario usuario : usuarios) {
      int limite = (int) (Math.random() * 15) + 1;
      for (int i = 0; i < limite; i++) {
        try {
          Tarea nueva = tareaService.crear(tareaDTO, usuario.getId());
          usuarioService.completarTarea(usuario.getCorreoElectronico(), nueva.getNombre());
        } catch (Exception e) {
          System.out.println("Error: " + e.getMessage());
        }
      }
    }
    System.out.println("FIN DE INYECCIÓN USUARIOS + TAREAS");
    scanner.close();
  }
}
*/
