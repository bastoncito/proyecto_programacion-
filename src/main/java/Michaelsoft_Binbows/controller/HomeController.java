package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.UsuarioService;
import Michaelsoft_Binbows.services.WeatherService;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

  @Autowired private UsuarioService usuarioService;
  
  @Autowired private WeatherService weatherService;

  @GetMapping("/")
  public String redirigirLogin() {
    return "redirect:/login";
  }

  @GetMapping("/home")
  public String mostrarHome(Model model) {
    System.out.println("LOG: El método 'mostrarMain' ha sido llamado por una petición a /home.");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();

    Usuario usuarioActual = usuarioService.buscarPorCorreoConTareas(correo);
    usuarioActual.resetRacha();

    model.addAttribute("usuario", usuarioActual);
    model.addAttribute("tareas", usuarioActual.getTareasPendientes());
    model.addAttribute("historialTareas", usuarioActual.getTareasCompletadas());
    
    String ciudad = usuarioActual.getCiudad();
    if (ciudad != null && !ciudad.trim().isEmpty()) {
        try {
            String weatherJsonString = weatherService.getWeatherByCity(ciudad);
            JSONObject weatherJson = new JSONObject(weatherJsonString);

            Map<String, Object> climaData = new HashMap<>();
            climaData.put("temperatura", weatherJson.getJSONObject("main").getInt("temp"));
            climaData.put("descripcion", weatherJson.getJSONArray("weather").getJSONObject(0).getString("description"));
            climaData.put("humedad", weatherJson.getJSONObject("main").getInt("humidity"));
            climaData.put("icono", weatherJson.getJSONArray("weather").getJSONObject(0).getString("icon"));
            long timestamp = weatherJson.getLong("dt");
            // Seccion zona horaria
            int timezoneOffset = weatherJson.getInt("timezone");
            // Convertimos el timestamp UTC a un objeto Instant y le aplicamos el desfase horario
            Instant instant = Instant.ofEpochSecond(timestamp);
            ZoneId zoneId = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezoneOffset));
            // Formateamos la hora al formato "HH:mm"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(zoneId);
            String horaLocal = formatter.format(instant);

            climaData.put("hora", horaLocal);
            model.addAttribute("clima", climaData);
        } catch (Exception e) {
            System.err.println("Error al obtener datos del clima para la ciudad '" + ciudad + "': " + e.getMessage());
            model.addAttribute("climaError", "No se pudo obtener el clima. Verifica el nombre de la ciudad en tu perfil.");
        }
    }

    return "home";
  }

  @GetMapping("/ranking")
  public String mostrarRanking(Model model) {
    System.out.println("LOG: El método 'mostrarRanking' ha sido llamado por una petición a /home.");
    List<Usuario> rankingNivel = usuarioService.obtenerTodos();
    List<Usuario> rankingCompletadas = usuarioService.obtenerTodos();
    rankingNivel.sort(Comparator.comparing(Usuario::getNivelExperiencia).reversed());
    rankingCompletadas.sort(Comparator.comparing(Usuario::getNumeroCompletadas).reversed());
    model.addAttribute(
        "rankingNivel", rankingNivel != null ? rankingNivel : Collections.emptyList());
    model.addAttribute(
        "rankingCompletadas",
        rankingCompletadas != null ? rankingCompletadas : Collections.emptyList());
    return "ranking";
  }

  @GetMapping("/historial")
  public String mostrarHistorial(Model model) {
    System.out.println(
        "LOG: El método 'mostrarHistorial' ha sido llamado por una petición a /historial.");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    String correo = userDetails.getUsername();

    Usuario usuarioActual = usuarioService.buscarPorCorreoConTareas(correo);

    List<Tarea> historialTareas = usuarioActual.getTareasCompletadas();

    model.addAttribute("usuario", usuarioActual);
    model.addAttribute("historialTareas", historialTareas);

    return "historial_tareas";
  }

  @GetMapping("/hola")
  @ResponseBody
  public String decirHola() {
    System.out.println(
        "LOG: El método de prueba 'decirHola' ha sido llamado por una petición a /hola.");
    return "<h1>¡Éxito! La respuesta viene del controlador de Java.</h1>";
  }

  @GetMapping("/403")
  @ResponseBody
  public String mostrarError() {
    return "<title>Error</title><h1>403 - Forbidden</h1><h3>¿No se te está olvidando algo?</h3>";
  }
}