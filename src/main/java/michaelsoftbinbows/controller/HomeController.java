package michaelsoftbinbows.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import michaelsoftbinbows.data.SalonFamaRepository;
import michaelsoftbinbows.dto.TareaDto;
import michaelsoftbinbows.entities.SalonFama;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.services.AuthService;
import michaelsoftbinbows.services.ConfiguracionService;
import michaelsoftbinbows.services.GestorLogrosService;
import michaelsoftbinbows.services.TareaService;
import michaelsoftbinbows.services.UsuarioService;
import michaelsoftbinbows.services.WeatherService;
import michaelsoftbinbows.util.Dificultad;
import michaelsoftbinbows.util.SistemaNiveles;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Controller para el apartado del home/pantalla principal. */
@Controller
public class HomeController {

  @Autowired private UsuarioService usuarioService;
  @Autowired private WeatherService weatherService;
  @Autowired private ConfiguracionService configuracionService;
  @Autowired private TareaService tareaService;
  @Autowired private SalonFamaRepository salonFamaRepository;
  @Autowired private AuthService authservice;
  @Autowired private GestorLogrosService gestorLogrosService;

  /**
   * Redirige al login si se entra a la dirección.
   *
   * @return redirect a login
   */
  @GetMapping("/")
  public String redirigirLogin() {
    return "redirect:/login";
  }

  /**
   * Muestra la pantalla principal del usuario.
   *
   * @param model modelo para añadir atributos a la página
   * @return template de home
   */
  @GetMapping("/home")
  public String mostrarHome(Model model) {
    System.out.println("LOG: El método 'mostrarMain' ha sido llamado por una petición a /home.");
    Usuario usuarioActual = usuarioService.buscarPorCorreo(authservice.getCurrentUser().getCorreoElectronico());

    model.addAttribute("usuario", usuarioActual);
    model.addAttribute("tareas", usuarioActual.getTareasPendientes());
    model.addAttribute("historialTareas", usuarioActual.getTareasCompletadas());

    List<Tarea> historialCompleto = usuarioActual.getTareasCompletadas();
    List<Tarea> historialReciente = historialCompleto.stream().limit(3).toList();

    model.addAttribute("historialReciente", historialReciente); // La lista corta
    model.addAttribute("totalHistorial", historialCompleto.size()); // El número total

    int expSiguienteNivel =
        SistemaNiveles.experienciaParaNivel(usuarioActual.getNivelExperiencia() + 1);
    model.addAttribute("expSiguienteNivel", expSiguienteNivel);

    String ciudad = usuarioActual.getCiudad();
    String climaActual = null;
    if (ciudad != null && !ciudad.trim().isEmpty()) {
      try {
        String weatherJsonString = weatherService.getWeatherByCity(ciudad);
        JSONObject weatherJson = new JSONObject(weatherJsonString);

        Map<String, Object> climaData = new HashMap<>();
        climaData.put("temperatura", weatherJson.getJSONObject("main").getInt("temp"));
        climaData.put(
            "descripcion",
            weatherJson.getJSONArray("weather").getJSONObject(0).getString("description"));
        climaData.put("humedad", weatherJson.getJSONObject("main").getInt("humidity"));
        climaData.put(
            "icono", weatherJson.getJSONArray("weather").getJSONObject(0).getString("icon"));

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

        // Obtener el clima principal
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

    // Recomendar tarea según clima
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

    // Si hay tarea recomendada y no está en el usuario, la agregamos
    if (tareaRecomendada != null
        && usuarioActual.buscarTareaPorNombre(tareaRecomendada.getNombre()) == null) {
      try {
        TareaDto tareaDto = new TareaDto();
        tareaDto.nombre = tareaRecomendada.getNombre();
        tareaDto.descripcion = tareaRecomendada.getDescripcion();
        tareaDto.dificultad = Dificultad.obtenerDificultadPorExp(tareaRecomendada.getExp());
        tareaService.crear(tareaDto, expSiguienteNivel);
      } catch (Exception e) {
        System.err.println("Error al agregar tarea recomendada por clima: " + e.getMessage());
      }
    }

    // Ahora, busca la tarea recomendada desde el usuario para obtener el estado actualizado
    Tarea tareaRecomendadaUsuario =
        tareaRecomendada != null
            ? usuarioActual.buscarTareaPorNombre(tareaRecomendada.getNombre())
            : null;
    boolean tareaRecomendadaCompletada =
        tareaRecomendadaUsuario != null && tareaRecomendadaUsuario.isCompletada();
    model.addAttribute("tareaRecomendada", tareaRecomendadaUsuario);
    model.addAttribute("tareaRecomendadaCompletada", tareaRecomendadaCompletada);

    // 1. Pedimos el Top 3 (usando el método de UsuarioService)
    List<Usuario> top3 = usuarioService.getTopUsuarios(3);

    // 2. Lo anadimos al modelo para que el HTML lo pueda usar
    model.addAttribute("top3Usuarios", top3);
    model.addAttribute("activePage", "home");

    // Obtener la tarea semanal del usuario
    java.util.Optional<Tarea> tareaSemanal =
        tareaService.obtenerTareaSemanal(usuarioActual.getId());
    model.addAttribute("tareaSemanal", tareaSemanal.orElse(null));
    boolean desafioCompletado = tareaSemanal.isPresent() && tareaSemanal.get().isCompletada();
    model.addAttribute("desafioCompletado", desafioCompletado);

    // Logros revisar
    usuarioService.manejarLogicaDeLogin(usuarioActual.getCorreoElectronico());

    return "home";
  }

  /**
   * Muestra el ranking/top de usuarios.
   *
   * @param model modelo para añadir atributos a página
   * @return template de ranking
   */
  @GetMapping("/ranking")
  public String mostrarRanking(Model model) {
    System.out.println("LOG: El método 'mostrarRanking' (NUEVO) ha sido llamado.");

    try {
      // 1. Obtener el usuario actual (para la tarjeta de perfil)
      Usuario usuarioActual = authservice.getCurrentUser();
      model.addAttribute("usuarioLogueado", usuarioActual); // Lo pasamos al HTML

      // 2. Obtener el límite del Top (10, 20, etc.) desde la BD
      int limite = configuracionService.getLimiteTop();

      // 3. Obtener la lista del ranking (ordenada por puntosLiga)
      List<Usuario> listaRanking = usuarioService.getTopUsuarios(limite);
      model.addAttribute("listaRanking", listaRanking);

      // 4. Generar los textos de los meses (para los títulos)
      LocalDate hoy = LocalDate.now(ZoneId.systemDefault());
      LocalDate mesPasado = hoy.minusMonths(1);

      // Capitaliza la primera letra del mes
      Locale localeEs = Locale.forLanguageTag("es-ES");
      String mesActual = hoy.getMonth().getDisplayName(TextStyle.FULL, localeEs);
      String mesActualMayus =
          mesActual.substring(0, 1).toLowerCase(Locale.getDefault()) + mesActual.substring(1);

      String mesAnterior = mesPasado.getMonth().getDisplayName(TextStyle.FULL, localeEs);
      String mesAnteriorMayus =
          mesAnterior.substring(0, 1).toLowerCase(Locale.getDefault()) + mesAnterior.substring(1);

      model.addAttribute("tituloMesActual", mesActualMayus + " " + hoy.getYear());
      model.addAttribute("tituloMesAnterior", mesAnteriorMayus + " " + mesPasado.getYear());

      // 5. Lógica para el Salón de la Fama
      List<SalonFama> hallOfFame = salonFamaRepository.findAllByOrderByPuestoAsc();
      model.addAttribute("hallOfFame", hallOfFame);

    } catch (Exception e) {
      System.err.println("Error al cargar /ranking: " + e.getMessage());
      // TODO: redirigir a una página de error o al home
    }
    model.addAttribute("activePage", "ranking");
    return "ranking"; // Devuelve el nuevo ranking.html
  }

  /**
   * Página de forbidden/403.
   *
   * @return responsebody para el error
   */
  @GetMapping("/403")
  @ResponseBody
  public String mostrarError() {
    return "<title>Error</title><h1>403 - Forbidden</h1><h3>¿No se te está olvidando algo?</h3>";
  }
}
