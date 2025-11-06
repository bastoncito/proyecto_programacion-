package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.data.SalonFamaRepository; // Cambio entrante
import Michaelsoft_Binbows.entities.SalonFama; // Cambio entrante
import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.ConfiguracionService;
import Michaelsoft_Binbows.services.TareaService;
import Michaelsoft_Binbows.services.UsuarioService;
import Michaelsoft_Binbows.services.WeatherService;
import Michaelsoft_Binbows.util.SistemaNiveles; // Tu cambio
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
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
  @Autowired private SalonFamaRepository salonFamaRepository; // Cambio entrante

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

    model.addAttribute("usuario", usuarioActual);
    model.addAttribute("tareas", usuarioActual.getTareasPendientes());
    model.addAttribute("historialTareas", usuarioActual.getTareasCompletadas());

    List<Tarea> historialCompleto = usuarioActual.getTareasCompletadas();
    List<Tarea> historialReciente = historialCompleto.stream().limit(3).toList();

    model.addAttribute("historialReciente", historialReciente);
    model.addAttribute("totalHistorial", historialCompleto.size());

    // Tu lógica se mantiene intacta
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
        int timezoneOffset = weatherJson.getInt("timezone");
        Instant instant = Instant.ofEpochSecond(timestamp);
        ZoneId zoneId = ZoneId.ofOffset("UTC", java.time.ZoneOffset.ofTotalSeconds(timezoneOffset));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(zoneId);
        String horaLocal = formatter.format(instant);

        climaData.put("hora", horaLocal);
        model.addAttribute("clima", climaData);

        climaActual =
            weatherJson
                .getJSONArray("weather")
                .getJSONObject(0)
                .getString("main");
      } catch (Exception e) {
        System.err.println(
            "Error al obtener datos del clima para la ciudad '" + ciudad + "': " + e.getMessage());
        model.addAttribute(
            "climaError",
            "No se pudo obtener el clima. Verifica el nombre de la ciudad en tu perfil.");
      }
    }

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

    List<Usuario> top3 = usuarioService.getTopUsuarios(3);

    model.addAttribute("top3Usuarios", top3);
    model.addAttribute("activePage", "home");

    return "home";
  }

  @GetMapping("/ranking")
  public String mostrarRanking(Model model) {
    System.out.println("LOG: El método 'mostrarRanking' (NUEVO) ha sido llamado.");

    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
      Usuario usuarioActual = usuarioService.buscarPorCorreo(userDetails.getUsername());
      model.addAttribute("usuarioLogueado", usuarioActual);

      int limite = configuracionService.getLimiteTop();
      List<Usuario> listaRanking = usuarioService.getTopUsuarios(limite);
      model.addAttribute("listaRanking", listaRanking);

      LocalDate hoy = LocalDate.now();
      LocalDate mesPasado = hoy.minusMonths(1);

      String mesActual = hoy.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
      String mesActualMayus = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1);

      String mesAnterior =
          mesPasado.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
      String mesAnteriorMayus =
          mesAnterior.substring(0, 1).toUpperCase() + mesAnterior.substring(1);

      model.addAttribute("tituloMesActual", mesActualMayus + " " + hoy.getYear());
      model.addAttribute("tituloMesAnterior", mesAnteriorMayus + " " + mesPasado.getYear());

      // La lógica del Salón de la Fama se mantiene
      List<SalonFama> hallOfFame = salonFamaRepository.findAllByOrderByPuestoAsc();
      model.addAttribute("hallOfFame", hallOfFame);

    } catch (Exception e) {
      System.err.println("Error al cargar /ranking: " + e.getMessage());
    }
    model.addAttribute("activePage", "ranking");
    return "ranking";
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