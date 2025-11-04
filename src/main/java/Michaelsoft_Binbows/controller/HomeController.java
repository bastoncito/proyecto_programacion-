package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.ConfiguracionService;
import Michaelsoft_Binbows.services.TareaService;
import Michaelsoft_Binbows.services.UsuarioService;
import Michaelsoft_Binbows.services.WeatherService;
import Michaelsoft_Binbows.util.SistemaNiveles;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

  @Autowired private ConfiguracionService configuracionService;

  @Autowired private TareaService tareaService;

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

    List<Tarea> historialCompleto = usuarioActual.getTareasCompletadas();
    List<Tarea> historialReciente = historialCompleto.stream().limit(3).toList();

    model.addAttribute("historialReciente", historialReciente); // La lista corta
    model.addAttribute("totalHistorial", historialCompleto.size()); // El número total

    int expSiguienteNivel = SistemaNiveles.experienciaParaNivel(usuarioActual.getNivelExperiencia() + 1);
    model.addAttribute("expSiguienteNivel", expSiguienteNivel);
    

    
    String ciudad = usuarioActual.getCiudad();
    String climaActual = null;
    if (ciudad != null && !ciudad.trim().isEmpty()) {
      try {
        String weatherJsonString = weatherService.getWeatherByCity(ciudad);
        JSONObject weatherJson = new JSONObject(weatherJsonString);

        Map<String, Object> climaData = new HashMap<>();
        climaData.put("temperatura", weatherJson.getJSONObject("main").getInt("temp"));
        climaData.put("descripcion",weatherJson.getJSONArray("weather").getJSONObject(0).getString("description"));
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

        // ---NUEVO: obtener el clima principal---
        climaActual =
            weatherJson
                .getJSONArray("weather")
                .getJSONObject(0)
                .getString("main"); // Ejemplo: "Clear", "Clouds", "Rain"
      } catch (Exception e) {
        System.err.println(
            "Error al obtener datos del clima para la ciudad '" + ciudad + "': " + e.getMessage());
        model.addAttribute(
            "climaError",
            "No se pudo obtener el clima. Verifica el nombre de la ciudad en tu perfil.");
      }
    }

    // ---NUEVO: recomendar tarea según clima---
    Tarea tareaRecomendada = null;
    if (climaActual != null) {
      String categoriaClima =
          switch (climaActual) {
            case "Clear" -> "Soleado";
            case "Clouds" -> "Nublado";
            case "Rain", "Drizzle", "Thunderstorm" -> "Lluvia";
            case "Snow" -> "Nieve";
            default -> "Soleado";
          };
      List<Tarea> recomendadas = tareaService.obtenerTareasRecomendadasPorClima(categoriaClima);
      if (!recomendadas.isEmpty()) {
        tareaRecomendada = recomendadas.get(0);
      }
    }
    model.addAttribute("tareaRecomendada", tareaRecomendada);

    // 1. Pedimos el Top 3 (usando el método que ya creamos en UsuarioService)
    List<Usuario> top3 = usuarioService.getTopUsuarios(3);

    // 2. Lo añadimos al modelo para que el HTML lo pueda usar
    model.addAttribute("top3Usuarios", top3);
    model.addAttribute("activePage", "home");

    return "home";
  }

  @GetMapping("/ranking")
  public String mostrarRanking(Model model) {
    System.out.println("LOG: El método 'mostrarRanking' (NUEVO) ha sido llamado.");

    // --- Lógica Nueva ---
    try {
      // 1. Obtener el usuario actual (para la tarjeta de perfil)
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
      Usuario usuarioActual = usuarioService.buscarPorCorreo(userDetails.getUsername());
      model.addAttribute("usuarioLogueado", usuarioActual); // <-- Lo pasamos al HTML

      // 2. Obtener el límite del Top (10, 20, etc.) desde la BD
      int limite = configuracionService.getLimiteTop();

      // 3. Obtener la lista del ranking (ordenada por puntosLiga)
      List<Usuario> listaRanking = usuarioService.getTopUsuarios(limite);
      model.addAttribute("listaRanking", listaRanking);

      // 4. Generar los textos de los meses (para los títulos)
      LocalDate hoy = LocalDate.now();
      LocalDate mesPasado = hoy.minusMonths(1);

      // Capitaliza la primera letra del mes
      String mesActual = hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
      String mesActualMayus = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1);

      String mesAnterior =
          mesPasado.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
      String mesAnteriorMayus =
          mesAnterior.substring(0, 1).toUpperCase() + mesAnterior.substring(1);

      model.addAttribute("tituloMesActual", mesActualMayus + " " + hoy.getYear());
      model.addAttribute("tituloMesAnterior", mesAnteriorMayus + " " + mesPasado.getYear());

      // 5. TODO: Lógica para el Salón de la Fama
      // (Por ahora, enviamos una lista vacía para que el HTML no se rompa)
      model.addAttribute("hallOfFame", Collections.emptyList());

    } catch (Exception e) {
      System.err.println("Error al cargar /ranking: " + e.getMessage());
      // TODO: redirigir a una página de error o al home
    }
    model.addAttribute("activePage", "ranking");
    return "ranking"; // Devuelve el nuevo ranking.html
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
