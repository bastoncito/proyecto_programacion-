package michaelsoftbinbows.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import michaelsoftbinbows.dto.TareaDto;
import michaelsoftbinbows.entities.Tarea;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.dto.TopJugadorLogrosDto;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.exceptions.AdminCrearTareaException;
import michaelsoftbinbows.exceptions.AdminCrearUsuarioException;
import michaelsoftbinbows.exceptions.AdminGuardarTareaException;
import michaelsoftbinbows.exceptions.EdicionInvalidaException;
import michaelsoftbinbows.exceptions.RegistroInvalidoException;
import michaelsoftbinbows.exceptions.TareaInvalidaException;
import michaelsoftbinbows.model.Rol;
import michaelsoftbinbows.security.CustomUserDetails;
import michaelsoftbinbows.services.ConfiguracionService;
import michaelsoftbinbows.services.SeguridadService;
import michaelsoftbinbows.services.TareaService;
import michaelsoftbinbows.services.TemporadaService;
import michaelsoftbinbows.services.UsuarioService;
import michaelsoftbinbows.services.LogroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para el panel de administración. Maneja la visualización y gestión de usuarios,
 * tareas y configuraciones del sistema, asegurando que solo los usuarios con roles de ADMIN o
 * MODERADOR puedan acceder.
 */
@Controller
public class AdminController {

  // --- Inyección de Dependencias ---
  // Spring se encarga de instanciar y proporcionar estos servicios al controlador.

  @Autowired private PasswordEncoder passwordEncoder; // Para encriptar contraseñas.
  @Autowired private UsuarioService usuarioService; // Lógica de negocio para usuarios.
  @Autowired private SeguridadService seguridadService; // Reglas de permisos y roles.
  @Autowired private TemporadaService temporadaService; // Lógica para el reseteo de temporadas.
  @Autowired private TareaService tareaService; // Lógica de negocio para tareas.
  @Autowired private LogroService logroService;
  @Autowired private ConfiguracionService configuracionService; // Para ajustes del sistema como el límite del Top.
  

  /**
   * Método de ayuda para registrar un nuevo usuario con la contraseña ya encriptada. Centraliza la
   * lógica de encriptación antes de guardar.
   *
   * @param usuario El objeto Usuario a registrar.
   * @throws RegistroInvalidoException si los datos del usuario no son válidos (ej. correo
   *     duplicado).
   */
  public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
    String encodedPassword = passwordEncoder.encode(usuario.getContrasena());
    usuario.setContrasena(encodedPassword);
    usuarioService.guardarSinValidarContrasena(usuario);
  }

  /**
   * Método principal que maneja todas las peticiones GET a /admin. Actúa como un enrutador central
   * para las diferentes vistas del panel (Usuarios, Tareas, Top) y gestiona la lógica para mostrar
   * los modales de edición y creación.
   *
   * <p>RESPONSABILIDADES:
   *
   * <p>- Seguridad: Verifica que el usuario logueado tenga rol de ADMIN o MODERADOR. - Enrutamiento
   * de Vistas: Usa el parámetro 'vista' para decidir qué sección mostrar. - Gestión de Modales: Lee
   * los parámetros de la URL (ej. 'editarUsuarioCorreo', 'crearUsuario') para determinar si se debe
   * mostrar un modal. - Carga de Datos: Prepara y añade al Model todos los datos necesarios para la
   * plantilla de Thymeleaf.
   *
   * @param vistaActual El nombre de la vista a mostrar ('usuarios', 'tareas', 'top').
   * @param correoEditado Correo del usuario para el cual se debe abrir el modal de edición.
   * @param crearUsuario Activa el modal de creación de usuario si es 'true'.
   * @param error Mensaje de error opcional pasado por RedirectAttributes.
   * @param correoUsuarioSeleccionado Correo del usuario que se muestra en la vista de 'Tareas'.
   * @param crearTarea Activa el modal para añadir una nueva tarea si es 'true'.
   * @param errorCreacionTarea Mensaje de error específico para el modal de creación de tareas.
   * @param nombreTareaParaEditar Nombre de la tarea para la cual se debe abrir el modal de edición.
   * @param correoUsuarioParaEditar Correo del propietario de la tarea a editar.
   * @param limite Define cuántos usuarios mostrar en la vista 'Top'.
   * @param mostrarConfig Activa el modal de configuración del Top si es 'true'.
   * @param preselectedUserEmail El correo del usuario seleccionado al estar en la seccion de
   *     agregar tarea.
   * @param errorConfig Mensaje de error para la configuración de ligas.
   * @param model Objeto Model de Spring para pasar atributos a la vista.
   * @return El nombre de la plantilla a renderizar ("admin").
   */
  @GetMapping("/admin")
  public String mostrarPanelAdmin(
      @RequestParam(name = "vista", required = false, defaultValue = "usuarios") String vistaActual,
      @RequestParam(name = "editarUsuarioCorreo", required = false) String correoEditado,
      @RequestParam(name = "crearUsuario", required = false) boolean crearUsuario,
      @RequestParam(name = "error", required = false) String error,
      @RequestParam(name = "correo", required = false) String correoUsuarioSeleccionado,
      @RequestParam(name = "crearTarea", required = false) boolean crearTarea,
      @RequestParam(name = "errorCreacionTarea", required = false) String errorCreacionTarea,
      @RequestParam(name = "editarTareaNombre", required = false) String nombreTareaParaEditar,
      @RequestParam(name = "editarTareaUsuario", required = false) String correoUsuarioParaEditar,
      @RequestParam(name = "limite", required = false) Integer limite,
      @RequestParam(name = "mostrarConfig", required = false) boolean mostrarConfig,
      @RequestParam(name = "preselectUser", required = false) String preselectedUserEmail,
      @RequestParam(name = "errorConfig", required = false) String errorConfig,
      @RequestParam(name = "editarLogroId", required = false) String logroIdParaEditar,
      @RequestParam(name = "errorLogro", required = false) String errorLogro,
      Model model) {

    // Se obtiene el usuario que está actualmente logueado para verificar sus permisos.
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario usuarioActual = ((CustomUserDetails) auth.getPrincipal()).getUsuario();

    // Primera barrera de seguridad: si el usuario no tiene el rol adecuado, se le redirige.
    if (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR) {
      System.err.println(
          "WARN: Intento de acceso no autorizado a /admin por usuario: "
              + usuarioActual.getNombreUsuario());
      return "redirect:/acceso-denegado";
    }

    // Se añaden al modelo los datos comunes que todas las vistas del panel necesitan.
    model.addAttribute("vistaActual", vistaActual);
    model.addAttribute("usuarioActual", usuarioActual);
    model.addAttribute("seguridadService", seguridadService);
    model.addAttribute("listaDeUsuarios", usuarioService.obtenerTodos());
    model.addAttribute(
        "rolesDisponibles",
        (usuarioActual.getRol() == Rol.ADMIN)
            ? List.of(Rol.MODERADOR, Rol.USUARIO)
            : List.of(Rol.USUARIO));

    if (error != null && !error.isEmpty()) {
      model.addAttribute("error", error);
    }

    if (errorCreacionTarea != null && !errorCreacionTarea.isEmpty()) {
      model.addAttribute("errorCreacionTarea", errorCreacionTarea);
    }

    // --- Lógica para activar los modales (ventanas emergentes) ---

    if (crearUsuario) {
      System.out.println("Log: Se ha solicitado abrir el modal para crear un nuevo usuario.");
      model.addAttribute("mostrarModalCrear", true);
    }
    if (crearTarea) {
      System.out.println("Log: Se ha solicitado abrir el modal para crear una nueva tarea.");
      model.addAttribute("mostrarModalCrearTarea", true);
      if (preselectedUserEmail != null) {
        model.addAttribute("preselectedUserEmail", preselectedUserEmail);
      }
    }
    if (mostrarConfig) {
      System.out.println(
          "Log: Se ha solicitado abrir el modal para mostrar configuraciones de top.");
      model.addAttribute("mostrarModalTopConfig", true);

      Map<String, Integer> limitesLiga = new HashMap<>();
      limitesLiga.put("LIGA_PLATA", configuracionService.getLimiteLiga("LIGA_PLATA", 500));
      limitesLiga.put("LIGA_ORO", configuracionService.getLimiteLiga("LIGA_ORO", 1500));
      limitesLiga.put("LIGA_PLATINO", configuracionService.getLimiteLiga("LIGA_PLATINO", 3000));
      limitesLiga.put("LIGA_DIAMANTE", configuracionService.getLimiteLiga("LIGA_DIAMANTE", 5000));
      model.addAttribute("limitesLiga", limitesLiga);

      if (errorConfig != null) {
        model.addAttribute("errorConfig", errorConfig);
      }
    }

    if (correoEditado != null) {
      System.out.println(
          "DEBUG: Se ha solicitado abrir el modal para editar al usuario: " + correoEditado);

      Usuario usuarioAeditar = usuarioService.buscarPorCorreo(correoEditado);
      if (usuarioAeditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAeditar)) {
        model.addAttribute("usuarioParaEditar", usuarioAeditar);
      }
    }

    if (nombreTareaParaEditar != null && correoUsuarioParaEditar != null) {
      System.out.println(
          "DEBUG: Se ha solicitado abrir el modal para editar la tarea '"
              + nombreTareaParaEditar
              + "' del usuario: "
              + correoUsuarioParaEditar);
      Usuario usuarioDeLaTarea = usuarioService.buscarPorCorreo(correoUsuarioParaEditar);
      if (usuarioDeLaTarea != null
          && seguridadService.puedeGestionarTareasDe(usuarioActual, usuarioDeLaTarea)) {
        Tarea tareaParaEditar = usuarioDeLaTarea.buscarTareaPorNombre(nombreTareaParaEditar);
        if (tareaParaEditar != null) {
          model.addAttribute("tareaParaEditar", tareaParaEditar);
          model.addAttribute("usuarioDeLaTarea", usuarioDeLaTarea);
        }
      }
    }

    if (logroIdParaEditar != null) {
      System.out.println("DEBUG: Se ha solicitado abrir el modal para editar el logro: " + logroIdParaEditar);
      
      // Buscamos el logro en la BD usando el servicio
      logroService.obtenerPorId(logroIdParaEditar).ifPresent(logro -> {
          model.addAttribute("logroParaEditar", logro);
      });
      
      if (errorLogro != null) {
          model.addAttribute("errorLogro", errorLogro);
      }
    }

    // --- Carga de datos específicos para cada vista ---
    switch (vistaActual) {
      case "tareas":
        System.out.println("DEBUG: Cargando datos para la vista 'tareas'.");
        if (correoUsuarioSeleccionado != null) {
          model.addAttribute(
              "usuarioSeleccionado",
              usuarioService.buscarPorCorreoConTareas(correoUsuarioSeleccionado));
        }
        break;
      case "top":
        int limiteActual = (limite == null) ? configuracionService.getLimiteTop() : limite;
        System.out.println("DEBUG: Cargando datos para la vista 'top' con límite: " + limiteActual);
        model.addAttribute("listaTop10", usuarioService.getTopUsuarios(limiteActual));
        model.addAttribute("limiteActual", limiteActual);
        break;

      case "logros":
        System.out.println("DEBUG: Cargando datos para la vista 'logros'.");
        
        // (Datos que ya teníamos)
        model.addAttribute("totalLogros", logroService.getConteoTotalLogros());
        model.addAttribute("logrosActivos", logroService.getConteoLogrosActivos());
        model.addAttribute("listaDeLogros", logroService.obtenerTodos());

        // --- ¡NUEVO! Cargas los datos calculados desde UsuarioService ---
        // 1. Añade el conteo total para la tarjeta de estadísticas
        model.addAttribute("totalCompletados", usuarioService.getConteoTotalLogrosCompletados());

        // 2. Añade la lista del Top 5 Jugadores
        model.addAttribute("top5Jugadores", usuarioService.getTop5JugadoresPorLogros());
        
        break;

      case "usuarios":
        // No hay carga extra para la vista de usuarios.
        break;
      default:
        // Cae en "usuarios" (vista por defecto) o cualquier otro caso.
        break;
    }

    model.addAttribute("activePage", "admin");
    return "admin";
  }

  /**
   * Procesa el formulario para guardar los cambios de un usuario editado.
   *
   * @param nuevoNombre El nuevo nombre de usuario.
   * @param correoOriginal El correo original del usuario, para identificarlo en la BD.
   * @param nuevoCorreo El nuevo correo electrónico.
   * @param nuevoRol El nuevo rol asignado.
   * @param nuevaContrasena La nueva contraseña (opcional).
   * @param confirmarContrasena Confirmación de la nueva contraseña.
   * @param redirectAttributes Permite enviar mensajes (feedback) a la vista después de una
   *     redirección.
   * @return Una redirección a la página de administración.
   */
  @PostMapping("/admin/guardar")
  public String guardarUsuarioEditado(
      @RequestParam("nombreUsuario") String nuevoNombre,
      @RequestParam("correoElectronicoOriginal") String correoOriginal,
      @RequestParam("nuevoCorreo") String nuevoCorreo,
      @RequestParam("rol") Rol nuevoRol,
      @RequestParam(name = "nuevaContrasena", required = false) String nuevaContrasena,
      @RequestParam(name = "confirmarContrasena", required = false) String confirmarContrasena,
      RedirectAttributes redirectAttributes)
      throws EdicionInvalidaException {

    System.out.println("\n--- INICIO PROCESO DE EDICIÓN DE USUARIO ---");
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario actor = ((CustomUserDetails) auth.getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoOriginal);

    System.out.println(
        "LOG: Actor '"
            + actor.getNombreUsuario()
            + "' (Rol: "
            + actor.getRol()
            + ") intenta editar a '"
            + objetivo.getNombreUsuario()
            + "'.");
    System.out.println("LOG: Datos recibidos del formulario:");
    System.out.println("LOG: -> Nuevo Nombre: '" + nuevoNombre + "'");
    System.out.println("LOG: -> Nuevo Correo: '" + nuevoCorreo + "'");
    System.out.println("LOG: -> Nuevo Rol: '" + nuevoRol + "'");

    if (!seguridadService.puedeEditar(actor, objetivo)) {
      System.out.println(
          "WARN: Fallo de seguridad. El actor no tiene permisos para editar. Acción bloqueada.");
      return "redirect:/acceso-denegado";
    }
    if (!seguridadService.puedeAsignarRol(actor, nuevoRol)) {
      System.out.println(
          "WARN: Intento no permitido de asignar el rol '" + nuevoRol + "'. Acción bloqueada.");
      redirectAttributes.addFlashAttribute(
          "error", "No tienes permiso para asignar el rol de " + nuevoRol + ".");
      redirectAttributes.addAttribute("editarUsuarioCorreo", correoOriginal);
      return "redirect:/admin";
    }

    System.out.println(
        "LOG: Permisos verificados. Intentando aplicar cambios en la capa de datos...");
    try {
      usuarioService.actualizarUsuario(correoOriginal, nuevoNombre, nuevoCorreo, nuevoRol, null);

      if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
        System.out.println("LOG: Se ha detectado un intento de cambio de contraseña.");
        if (!nuevaContrasena.equals(confirmarContrasena)) {
          System.err.println("ERROR: Las contraseñas no coinciden.");
          throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        usuarioService.actualizarContrasenaUsuario(nuevoCorreo, nuevaContrasena);
        System.out.println("SUCCESS: La contraseña fue actualizada exitosamente.");
      }
      redirectAttributes.addFlashAttribute(
          "success", "Usuario '" + nuevoNombre + "' actualizado correctamente.");
      System.out.println("SUCCESS: Usuario actualizado exitosamente en la base de datos.");
    } catch (IllegalArgumentException | RegistroInvalidoException e) {
      throw new EdicionInvalidaException(e.getMessage(), correoOriginal);
    }
    return "redirect:/admin";
  }

  /**
   * Maneja la solicitud para eliminar un usuario del sistema.
   *
   * @param correoAeliminar El correo del usuario a eliminar.
   * @param redirectAttributes Para enviar mensajes de feedback a la vista.
   * @return Redirección al panel de administración.
   */
  @GetMapping("/admin/eliminar")
  public String eliminarUsuario(
      @RequestParam("correo") String correoAeliminar, RedirectAttributes redirectAttributes) {
    Usuario actor =
        ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoAeliminar);

    System.out.println(
        "Log: El usuario '"
            + actor.getNombreUsuario()
            + "' está intentando eliminar a '"
            + (objetivo != null ? objetivo.getNombreUsuario() : correoAeliminar)
            + "'.");

    if (objetivo == null) {
      System.err.println(
          "ERROR: Se intentó eliminar un usuario que no existe con el correo: " + correoAeliminar);
      redirectAttributes.addFlashAttribute("error", "No se encontró el usuario a eliminar.");
      return "redirect:/admin";
    }
    if (!seguridadService.puedeEliminar(actor, objetivo)) {
      System.err.println(
          "WARN: Fallo de seguridad al eliminar. El actor '"
              + actor.getNombreUsuario()
              + "' no tiene permisos sobre '"
              + objetivo.getNombreUsuario()
              + "'.");
      return "redirect:/acceso-denegado";
    }

    usuarioService.eliminarPorCorreo(correoAeliminar);
    redirectAttributes.addFlashAttribute(
        "success", "Usuario '" + objetivo.getNombreUsuario() + "' eliminado.");
    System.out.println(
        "SUCCESS: Usuario '"
            + objetivo.getNombreUsuario()
            + "' eliminado por '"
            + actor.getNombreUsuario()
            + "'.");
    return "redirect:/admin";
  }

  /**
   * Maneja la eliminación de una tarea específica de un usuario.
   *
   * @param correoUsuario El correo del dueño de la tarea.
   * @param nombreTarea El nombre de la tarea a eliminar.
   * @param redirectAttributes Para enviar mensajes de feedback.
   * @return Redirección a la vista de tareas del usuario correspondiente.
   */
  @GetMapping("/admin/tareas/eliminar")
  public String eliminarTareaDeUsuario(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreTarea") String nombreTarea,
      RedirectAttributes redirectAttributes) {

    Usuario actor =
        ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoUsuario);

    if (!seguridadService.puedeGestionarTareasDe(actor, objetivo)) {
      System.out.println(
          "WARN: Fallo al eliminar tarea. El actor no tiene permisos sobre el objetivo.");
      return "redirect:/acceso-denegado";
    }

    try {
      tareaService.eliminarPorUsuarioYNombreTarea(objetivo.getId(), nombreTarea);
      redirectAttributes.addFlashAttribute(
          "success",
          "Tarea '"
              + nombreTarea
              + "' del usuario '"
              + objetivo.getNombreUsuario()
              + "' ha sido eliminada.");
      System.out.println(
          "LOG: El admin '"
              + actor.getNombreUsuario()
              + "' eliminó la tarea '"
              + nombreTarea
              + "' del usuario '"
              + objetivo.getNombreUsuario()
              + "'.");
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      System.err.println("ERROR al eliminar tarea: " + e.getMessage());
    }
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

  /**
   * Guarda los cambios de una tarea editada desde el panel de admin.
   *
   * @param correoUsuario El correo del propietario de la tarea.
   * @param nombreOriginal El nombre original de la tarea.
   * @param nuevoNombre El nuevo nombre para la tarea.
   * @param nuevaDescripcion La nueva descripción para la tarea.
   * @param nuevaDificultad La nueva dificultad para la tarea.
   * @param redirectAttributes Atributos for redirect.
   * @return Redirección a la vista de tareas del usuario.
   * @throws AdminGuardarTareaException Si la tarea es inválida.
   */
  @PostMapping("/admin/tareas/guardar")
  public String guardarTareaEditada(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreOriginal") String nombreOriginal,
      @RequestParam("nombre") String nuevoNombre,
      @RequestParam("descripcion") String nuevaDescripcion,
      @RequestParam("dificultad") String nuevaDificultad,
      RedirectAttributes redirectAttributes)
      throws AdminGuardarTareaException {

    Long idUsuario = usuarioService.buscarPorCorreo(correoUsuario).getId();
    try {
      TareaDto tareaActualizada = new TareaDto();
      tareaActualizada.nombre = nuevoNombre;
      tareaActualizada.descripcion = nuevaDescripcion;
      tareaActualizada.dificultad = nuevaDificultad;
      tareaService.actualizarTarea(idUsuario, nombreOriginal, tareaActualizada);
      redirectAttributes.addFlashAttribute("success", "Tarea actualizada correctamente.");
    } catch (TareaInvalidaException | RegistroInvalidoException e) {
      throw new AdminGuardarTareaException(
          e.getMessage(),
          usuarioService.buscarPorCorreo(correoUsuario),
          nuevoNombre,
          nuevaDescripcion);
    }
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

  /**
   * Procesa el formulario para crear un nuevo usuario desde el panel de admin.
   *
   * @param nombre nombre del usuario nuevo
   * @param correo correo del usuario nuevo
   * @param contrasena contraseña del usuario nueevo
   * @param rol rol del usuario a crear
   * @param redirectAttributes atributos para redirect
   * @return redirect admin
   * @throws AdminCrearUsuarioException si el usuario nuevo es inválido
   */
  @PostMapping("/admin/crear")
  public String crearNuevoUsuario(
      @RequestParam("nombreUsuario") String nombre,
      @RequestParam("correo") String correo,
      @RequestParam("contrasena") String contrasena,
      @RequestParam("rol") Rol rol,
      RedirectAttributes redirectAttributes)
      throws AdminCrearUsuarioException {
    Usuario actor =
        ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsuario();

    if (!seguridadService.puedeAsignarRol(actor, rol)) {
      redirectAttributes.addFlashAttribute(
          "errorCreacion", "No tienes permiso para crear un usuario con el rol de " + rol + ".");
      redirectAttributes.addAttribute("crearUsuario", true);
      return "redirect:/admin";
    }

    System.out.println(
        "LOG: Recibida petición POST para crear un nuevo usuario con nombre: " + nombre);
    try {
      Usuario nuevoUsuario = new Usuario(nombre, correo, contrasena);
      nuevoUsuario.setRol(rol);
      registrarUsuario(nuevoUsuario);
      redirectAttributes.addFlashAttribute(
          "success", "Usuario '" + nombre + "' creado exitosamente.");
      System.out.println(
          "SUCCESS: Usuario '" + nombre + "' creado exitosamente en la base de datos.");
    } catch (RegistroInvalidoException e) {
      System.err.println(
          "ERROR: Falló la creación del usuario '" + nombre + "'. Causa: " + e.getMessage());
      throw new AdminCrearUsuarioException(e.getMessage());
    }
    return "redirect:/admin";
  }

  /**
   * Crea nueva Tarea para un Usuario.
   *
   * @param correoUsuario correo del usuario al q se le asigna la tarea
   * @param nombre nombre tarea nueva
   * @param descripcion descripcion tarea nueva
   * @param dificultad dificultad tarea nuevo
   * @param redirectAttributes atributos para redirect
   * @return redirect admin
   * @throws AdminCrearTareaException si la tarea es inválida
   */
  @PostMapping("/admin/tareas/crear")
  public String crearNuevaTarea(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombre") String nombre,
      @RequestParam("descripcion") String descripcion,
      @RequestParam("dificultad") String dificultad,
      RedirectAttributes redirectAttributes)
      throws AdminCrearTareaException {

    Usuario usuario = usuarioService.buscarPorCorreo(correoUsuario);
    if (usuario == null) {
      redirectAttributes.addFlashAttribute(
          "errorCreacionTarea", "El usuario seleccionado no es válido.");
      redirectAttributes.addAttribute("crearTarea", true);
      return "redirect:/admin?vista=tareas";
    }

    try {
      TareaDto tareaDto = new TareaDto();
      tareaDto.nombre = nombre;
      tareaDto.descripcion = descripcion;
      tareaDto.dificultad = dificultad;
      tareaService.crear(tareaDto, usuario.getId());
      redirectAttributes.addFlashAttribute(
          "success",
          "Tarea '" + nombre + "' anadida exitosamente a " + usuario.getNombreUsuario() + ".");
    } catch (TareaInvalidaException e) {
      throw new AdminCrearTareaException(e.getMessage());
    }
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

  /**
   * Endpoint para que el Admin fuerce el reseteo de la temporada.
   *
   * <p>Se puede probar visitando la URL como admin.
   *
   * @param redirectAttributes atributos para redirect
   * @return redirect admin
   */
  @GetMapping("/admin/temporada/reset-manual")
  public String forzarReseteoTemporada(RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String actor = auth.getName();
    System.out.println(
        "LOG: El admin '" + actor + "' está forzando un reseteo de temporada manual.");
    try {
      temporadaService.forzarReseteoManual();
      System.out.println("SUCCESS: Reseteo de temporada forzado con éxito por '" + actor + "'.");
      redirectAttributes.addFlashAttribute("success", "¡Reseteo de temporada forzado con éxito!");
    } catch (Exception e) {
      System.err.println("ERROR: Falló el reseteo de temporada manual. Causa: " + e.getMessage());
      redirectAttributes.addFlashAttribute(
          "error", "Error al forzar el reseteo: " + e.getMessage());
    }
    return "redirect:/admin?vista=top";
  }

  /**
   * Guarda la nueva configuración del límite del Top en la BD.
   *
   * @param limite limite establecido para el top
   * @param redirectAttributes atributos para redirect
   * @return redirect
   */
  @GetMapping("/admin/top/set-limite")
  public String setLimiteTop(
      @RequestParam("limite") int limite, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String actor = auth.getName();
    System.out.println(
        "LOG: El admin '" + actor + "' está cambiando el límite del Top a: " + limite);
    try {
      configuracionService.setLimiteTop(limite);
      redirectAttributes.addFlashAttribute("success", "Límite del Top guardado en " + limite + ".");
    } catch (Exception e) {
      System.err.println("ERROR: Falló al guardar el límite del Top. Causa: " + e.getMessage());
      redirectAttributes.addFlashAttribute("error", "Error al guardar el límite.");
    }
    return "redirect:/admin?vista=top&limite=" + limite;
  }

  /**
   * Muestra la página de acceso denegado personalizada.
   *
   * @return El nombre de la plantilla "acceso-denegado".
   */
  @GetMapping("/acceso-denegado")
  public String mostrarAccesoDenegado() {
    return "acceso-denegado";
  }

  /**
   * Guarda la nueva configuración de límites de Ligas en la BD. Incluye la validación y el
   * recálculo global.
   */
  @PostMapping("/admin/ligas/guardar")
  public String guardarLimitesLigas(
      @RequestParam("limitePlata") int plata,
      @RequestParam("limiteOro") int oro,
      @RequestParam("limitePlatino") int platino,
      @RequestParam("limiteDiamante") int diamante,
      @RequestParam("limiteActual") int limiteActual,
      RedirectAttributes redirectAttributes) {

    if (plata <= 0 || oro <= 0 || platino <= 0 || diamante <= 0) {
      redirectAttributes.addFlashAttribute(
          "errorConfig", "Error: Los puntos deben ser mayores a 0.");
      return "redirect:/admin?vista=top&mostrarConfig=true&limite=" + limiteActual;
    }

    if (!(plata < oro && oro < platino && platino < diamante)) {
      redirectAttributes.addFlashAttribute(
          "errorConfig",
          "Error: El orden de ligas es incorrecto (Plata < Oro < Platino < Diamante).");
      return "redirect:/admin?vista=top&mostrarConfig=true&limite=" + limiteActual;
    }

    try {
      configuracionService.setLimiteLiga("LIGA_PLATA", plata);
      configuracionService.setLimiteLiga("LIGA_ORO", oro);
      configuracionService.setLimiteLiga("LIGA_PLATINO", platino);
      configuracionService.setLimiteLiga("LIGA_DIAMANTE", diamante);
      usuarioService.recalcularLigasGlobal();

    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorConfig", "Error al guardar: " + e.getMessage());
      return "redirect:/admin?vista=top&mostrarConfig=true&limite=" + limiteActual;
    }

    redirectAttributes.addFlashAttribute(
        "success", "Límites de liga actualizados. Todas las ligas han sido recalculadas.");
    return "redirect:/admin?vista=top&limite=" + limiteActual;
  }

  /**
   * Maneja la petición para cambiar el estado (activo/inactivo) de un logro.
   *
   * @param id El ID del logro a modificar.
   * @param redirectAttributes Para enviar mensajes de feedback a la vista.
   * @return Redirección a la vista de logros.
   */
  @GetMapping("/admin/logros/toggle")
  public String toggleLogroActivo(
      @RequestParam("id") String id, RedirectAttributes redirectAttributes) {
        
    // 1. Buscamos el logro en el servicio
    Optional<Logro> logroOpt = logroService.obtenerPorId(id);

    if (logroOpt.isPresent()) {
      Logro logro = logroOpt.get();
      
      // 2. Invertimos su estado
      logro.setActivo(!logro.isActivo());
      
      // 3. Guardamos los cambios
      logroService.guardar(logro);
      
      String estado = logro.isActivo() ? "activado" : "desactivado";
      redirectAttributes.addFlashAttribute(
          "success", "Logro '" + logro.getNombre() + "' ha sido " + estado + ".");
    } else {
      redirectAttributes.addFlashAttribute("error", "No se encontró el logro con ID: " + id);
    }

    // 4. Redirigimos de vuelta a la pestaña de logros
    return "redirect:/admin?vista=logros";
  }

  /**
   * Procesa el formulario para guardar los cambios de un logro editado.
   *
   * @param id El ID del logro a guardar.
   * @param nombre El nuevo nombre para el logro.
   * @param descripcion La nueva descripción.
   * @param imagenUrl La nueva URL de la imagen/icono.
   * @param experienciaRecompensa La nueva cantidad de XP.
   * @param redirectAttributes Para enviar mensajes de feedback.
   * @return Redirección a la vista de logros.
   */
  @PostMapping("/admin/logros/guardar")
  public String guardarLogroEditado(
      @RequestParam("id") String id,
      @RequestParam("nombre") String nombre,
      @RequestParam("descripcion") String descripcion,
      @RequestParam("imagenUrl") String imagenUrl,
      @RequestParam("experienciaRecompensa") int experienciaRecompensa,
      RedirectAttributes redirectAttributes) {

    // 1. Validamos los datos
    if (nombre == null || nombre.trim().isEmpty()) {
      redirectAttributes.addFlashAttribute("errorLogro", "El nombre no puede estar vacío.");
      // Devolvemos al usuario al modal de edición
      return "redirect:/admin?vista=logros&editarLogroId=" + id;
    }
    if (experienciaRecompensa < 0) {
      redirectAttributes.addFlashAttribute("errorLogro", "La experiencia no puede ser negativa.");
      return "redirect:/admin?vista=logros&editarLogroId=" + id;
    }

    // 2. Buscamos el logro
    Optional<Logro> logroOpt = logroService.obtenerPorId(id);
    if (logroOpt.isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "Error: No se encontró el logro a guardar.");
      return "redirect:/admin?vista=logros";
    }

    // 3. Actualizamos el objeto Logro
    Logro logro = logroOpt.get();
    logro.setNombre(nombre.trim());
    logro.setDescripcion(descripcion.trim());
    logro.setExperienciaRecompensa(experienciaRecompensa);
    logro.setImagenUrl(imagenUrl.trim()); // Guardamos la nueva URL

    // 4. Guardamos en la base de datos
    logroService.guardar(logro);

    redirectAttributes.addFlashAttribute(
        "success", "Logro '" + logro.getNombre() + "' guardado correctamente.");
    return "redirect:/admin?vista=logros";
  }

}
