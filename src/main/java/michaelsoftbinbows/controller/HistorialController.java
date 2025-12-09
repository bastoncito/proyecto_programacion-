package michaelsoftbinbows.controller;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.services.AuthService;
import michaelsoftbinbows.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Controlador para gestionar la vista del historial de tareas completadas del usuario. */
@Controller
@RequestMapping("/historial")
public class HistorialController {

  @Autowired private AuthService authservice;
  @Autowired private UsuarioService usuarioService;

  /**
   * Muestra la página de historial de tareas del usuario actualmente logueado. Obtiene las tareas
   * completadas y las pasa a la vista.
   *
   * @param model El modelo de Spring para pasar datos a la plantilla.
   * @return El nombre de la plantilla "historial_tareas".
   */
  @GetMapping
  public String mostrarHistorial(Model model) {
    System.out.println(
        "LOG: El método 'mostrarHistorial' ha sido llamado desde HistorialController.");

    // 1. Obtener el usuario actual de forma segura (con tareas cargadas)
    Usuario usuarioActual =
        usuarioService.buscarPorCorreoConTareas(
            authservice.getCurrentUser().getCorreoElectronico());

    // 2. Manejar el caso en que el usuario no se encuentre
    if (usuarioActual == null) {
      return "redirect:/login";
    }

    // 3. Obtener la lista de tareas completadas
    List<Tarea> historial = usuarioActual.getTareasCompletadas();

    // 4. Añadir todos los datos necesarios al modelo
    model.addAttribute("usuario", usuarioActual);
    model.addAttribute("historialTareas", historial);
    model.addAttribute("activePage", "historial"); // Para que la navbar se ilumine

    // 5. Devolver el nombre de la plantilla HTML
    return "historial_tareas";
  }

  /**
   * Obtiene los datos de tareas completadas de la última semana (últimos 7 días). Retorna un JSON
   * con datos desglosados por día.
   *
   * @return ResponseEntity con un mapa que contiene arrays de días y cantidad de tareas completadas
   *     por día.
   */
  @GetMapping("/api/semanal")
  public ResponseEntity<Map<String, Object>> obtenerResumenSemanal() {
    try {
      Usuario usuarioActual =
          usuarioService.buscarPorCorreoConTareas(
              authservice.getCurrentUser().getCorreoElectronico());

      if (usuarioActual == null) {
        return ResponseEntity.status(401).body(new HashMap<>());
      }

      // Obtener la fecha de hace 7 días
      LocalDateTime hace7Dias = LocalDateTime.now().minusDays(7);

      // Obtener las tareas completadas en los últimos 7 días
      List<Tarea> tareasUltima7Dias = new ArrayList<>();
      for (Tarea tarea : usuarioActual.getTareasCompletadas()) {
        if (tarea.getFechaCompletada() != null && tarea.getFechaCompletada().isAfter(hace7Dias)) {
          tareasUltima7Dias.add(tarea);
        }
      }

      // Crear estructura de datos para los últimos 7 días
      String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
      int[] tareasPorDia = new int[7];

      // Contar tareas por día
      for (Tarea tarea : tareasUltima7Dias) {
        LocalDateTime fechaCompletada = tarea.getFechaCompletada();
        int diaDelaSemana = fechaCompletada.getDayOfWeek().getValue() - 1; // 0 = Lunes, 6 = Domingo
        tareasPorDia[diaDelaSemana]++;
      }

      Map<String, Object> resultado = new HashMap<>();
      resultado.put("dias", dias);
      resultado.put("tareas", tareasPorDia);
      resultado.put("total", tareasUltima7Dias.size());

      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(new HashMap<>());
    }
  }

  /**
   * Obtiene los datos de tareas completadas del último mes (últimos 30 días). Retorna un JSON con
   * datos desglosados por día del mes.
   *
   * @return ResponseEntity con un mapa que contiene arrays de días y cantidad de tareas completadas
   *     por día.
   */
  @GetMapping("/api/mensual")
  public ResponseEntity<Map<String, Object>> obtenerResumenMensual() {
    try {
      Usuario usuarioActual =
          usuarioService.buscarPorCorreoConTareas(
              authservice.getCurrentUser().getCorreoElectronico());

      if (usuarioActual == null) {
        return ResponseEntity.status(401).body(new HashMap<>());
      }

      // Obtener la fecha de hace 30 días
      LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);

      // Obtener las tareas completadas en los últimos 30 días
      List<Tarea> tareasUltimo30Dias = new ArrayList<>();
      for (Tarea tarea : usuarioActual.getTareasCompletadas()) {
        if (tarea.getFechaCompletada() != null && tarea.getFechaCompletada().isAfter(hace30Dias)) {
          tareasUltimo30Dias.add(tarea);
        }
      }

      // Crear estructura de datos para 30 días
      int[] tareasPorDia = new int[30];
      String[] etiquetas = new String[30];

      // Crear etiquetas para cada día
      for (int i = 0; i < 30; i++) {
        LocalDateTime fecha = LocalDateTime.now().minusDays(29 - i);
        etiquetas[i] = String.format("%d/%d", fecha.getDayOfMonth(), fecha.getMonthValue());
      }

      // Contar tareas por día
      for (Tarea tarea : tareasUltimo30Dias) {
        LocalDateTime fechaCompletada = tarea.getFechaCompletada();
        long diasDiferencia = ChronoUnit.DAYS.between(hace30Dias, fechaCompletada);
        if (diasDiferencia >= 0 && diasDiferencia < 30) {
          tareasPorDia[(int) diasDiferencia]++;
        }
      }

      Map<String, Object> resultado = new HashMap<>();
      resultado.put("etiquetas", etiquetas);
      resultado.put("tareas", tareasPorDia);
      resultado.put("total", tareasUltimo30Dias.size());

      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(new HashMap<>());
    }
  }
}
