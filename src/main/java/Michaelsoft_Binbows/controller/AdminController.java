package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.entities.Usuario;
import Michaelsoft_Binbows.exceptions.AdminCrearTareaException;
import Michaelsoft_Binbows.exceptions.AdminCrearUsuarioException;
import Michaelsoft_Binbows.exceptions.AdminGuardarTareaException;
import Michaelsoft_Binbows.exceptions.EdicionInvalidaException;
import Michaelsoft_Binbows.exceptions.RegistroInvalidoException;
import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import Michaelsoft_Binbows.model.Rol;
import Michaelsoft_Binbows.security.CustomUserDetails;
import Michaelsoft_Binbows.services.ConfiguracionService;
import Michaelsoft_Binbows.services.SeguridadService;
import Michaelsoft_Binbows.services.UsuarioService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Imports de Spring y Java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Michaelsoft_Binbows.services.TemporadaService; // <-- 
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // <--

/** Controlador para el panel de administración. Maneja la visualización y gestión de usuarios. */
@Controller
public class AdminController {

  @Autowired private PasswordEncoder passwordEncoder;

  @Autowired private UsuarioService usuarioService;

  // Dependencias del controlador
  private final SeguridadService seguridadService;

  @Autowired 
  private TemporadaService temporadaService;

  @Autowired 
  private ConfiguracionService configuracionService;

  // Inyección de dependencias
  public AdminController(SeguridadService seguridadService) {
    this.seguridadService = seguridadService;
  }

  public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
    String encodedPassword = passwordEncoder.encode(usuario.getContraseña());
    usuario.setContraseña(encodedPassword);
    usuarioService.guardarEnBD(usuario);
  }

  /*
   * Muestra la página principal del panel de administración.
   *
   * Este método actúa como el controlador central para todas las vistas dentro del panel de admin.
   * Utiliza el parámetro 'vista' para determinar qué contenido dinámico (fragmento de Thymeleaf)
   * debe cargarse en la plantilla principal.
   *
   * Realiza las siguientes acciones:
   * 1. Valida que el usuario en sesión sea un ADMIN o un MODERADOR. Si no, redirige al login.
   * 2. Carga los datos necesarios para la vista solicitada (ej. lista de usuarios para la vista 'usuarios').
   * 3. Prepara una lista de roles que el usuario actual tiene permitido asignar, para poblar los formularios de edición.
   * 4. Maneja la lógica para abrir el modal de edición de un usuario si se proporciona el parámetro 'editarUsuarioCorreo'.
   * 5. Pasa todos los datos necesarios al modelo para que la plantilla 'admin.html' los pueda renderizar.
   *
   * @param vistaActual El nombre de la vista a mostrar (ej. "usuarios", "tareas").
   *                    Viene de la URL como un parámetro '?vista=...'. Por defecto es "usuarios".
   * @param correoAEditar (Opcional) El correo del usuario cuyo modal de edición se debe mostrar.
   * @param error (Opcional) Un mensaje de error proveniente de una redirección (ej. un fallo al guardar).
   * @param model El objeto Model de Spring, usado para pasar atributos a la vista de Thymeleaf.
   * @param session La sesión HTTP actual, para obtener el usuario que ha iniciado sesión.
   * @return El nombre de la plantilla principal a renderizar ("admin").
   */
  @GetMapping("/admin")
  public String mostrarPanelAdmin(
      // Añadimos el parámetro "vista" para la navegación
      @RequestParam(name = "vista", required = false, defaultValue = "usuarios") String vistaActual,
      @RequestParam(name = "editarUsuarioCorreo", required = false) String correoAEditar,
      @RequestParam(name = "crearUsuario", required = false) boolean crearUsuario,
      @RequestParam(name = "error", required = false) String error,
      @RequestParam(name = "correo", required = false) String correoUsuarioSeleccionado,
      @RequestParam(name = "crearTarea", required = false) boolean crearTarea,
      @RequestParam(name = "errorCreacionTarea", required = false) String errorCreacionTarea,
      @RequestParam(name = "limite", required = false) Integer limite,
      @RequestParam(name = "mostrarConfig", required = false) boolean mostrarConfig,
      Model model) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Usuario usuarioActual = userDetails.getUsuario();

    if (usuarioActual == null
        || (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR)) {
      return "redirect:/403";
    }

    int limiteActual;
    if (limite == null) {
      // Si la URL no trae ?limite=X, buscamos el valor guardado en la BD
      limiteActual = configuracionService.getLimiteTop();
    } else {
      // Si la URL sí trae un límite, usamos ese
      limiteActual = limite;
    }

    // Pasamos la vista actual y el usuario al modelo
    model.addAttribute("vistaActual", vistaActual);
    model.addAttribute("usuarioActual", usuarioActual);
    model.addAttribute("seguridadService", this.seguridadService);

    // Aquí es donde comprobamos si la URL pide abrir el modal de creación, para el boton +
    // multiusos.
    if (crearUsuario) {
      model.addAttribute("mostrarModalCrear", true);
    }
    if (crearTarea) {
      model.addAttribute("mostrarModalCrearTarea", true);
    }
    if (mostrarConfig) {
      model.addAttribute("mostrarModalTopConfig", true);
    }

    model.addAttribute("listaDeUsuarios", usuarioService.obtenerTodos());

    // --- LÓGICA DINÁMICA PARA LOS ROLES DISPONIBLES ---
    List<Rol> rolesDisponibles;
    if (usuarioActual.getRol() == Rol.ADMIN) {
      rolesDisponibles =
          Arrays.stream(Rol.values())
              .filter(r -> r == Rol.MODERADOR || r == Rol.USUARIO)
              .collect(Collectors.toList());
    } else if (usuarioActual.getRol() == Rol.MODERADOR) {
      rolesDisponibles = Arrays.asList(Rol.USUARIO);
    } else {
      rolesDisponibles = List.of();
    }
    model.addAttribute("rolesDisponibles", rolesDisponibles);

    // --- Lógica para cargar datos según la vista ---
    switch (vistaActual) {
      case "usuarios":
        //  model.addAttribute("listaDeUsuarios", baseDatos.getUsuarios());
        //  la lista la añadi afuera
        break;
      case "tareas":
        model.addAttribute("listaDeUsuarios", usuarioService.obtenerTodos());
        if (correoUsuarioSeleccionado != null) {
          Usuario usuarioSeleccionado =
              usuarioService.buscarPorCorreoConTareas(
                  correoUsuarioSeleccionado); // ✅ Carga con tareas
          model.addAttribute("usuarioSeleccionado", usuarioSeleccionado);

          model.addAttribute("tareasPendientes", usuarioSeleccionado.getTareasPendientes());
        }
        break;

      case "top":
        List<Usuario> topUsuarios = usuarioService.getTopUsuarios(limiteActual); // Ahora usa el límite

        model.addAttribute("listaTop10", topUsuarios);
        model.addAttribute("limiteActual", limiteActual);
        break;
    }

    // Si se pulsa boton editar
    if (correoAEditar != null) {
      Usuario usuarioAEditar = usuarioService.buscarPorCorreo(correoAEditar);
      if (usuarioAEditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAEditar)) {
        model.addAttribute("usuarioParaEditar", usuarioAEditar);
      }
    }

    // Errores
    if (error != null && !error.isEmpty()) {
      model.addAttribute("error", error);
    }
    if (errorCreacionTarea != null && !errorCreacionTarea.isEmpty()) {
      model.addAttribute("errorCreacionTarea", errorCreacionTarea);
    }

    return "admin";
  }

  /*
   * Procesa el guardado de los cambios de un usuario desde el modal, con logging detallado.
   */
  @PostMapping("/admin/guardar")
  public String guardarUsuarioEditado(
      @RequestParam("nombreUsuario") String nuevoNombre,
      @RequestParam("correoElectronicoOriginal") String correoOriginal,
      @RequestParam("nuevoCorreo") String nuevoCorreo,
      @RequestParam("rol") Rol nuevoRol,
      @RequestParam(name = "nuevaContraseña", required = false) String nuevaContraseña,
      @RequestParam(name = "confirmarContraseña", required = false) String confirmarContraseña,
      RedirectAttributes redirectAttributes)
      throws EdicionInvalidaException {

    System.out.println("\n--- INICIO PROCESO DE EDICIÓN DE USUARIO ---");

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Usuario actor = userDetails.getUsuario();
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

    // Chequeo de seguridad en el servidor
    if (!seguridadService.puedeEditar(actor, objetivo)) {
      System.out.println(
          "WARN: Fallo de seguridad. El actor no tiene permisos para editar. Acción bloqueada.");
      return "redirect:/acceso-denegado";
    }

    // Regla de negocio: Un admin NO puede crear otro admin desde el panel, ni un MODERADOR crear a
    // otro MODERADOR
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

    // Se intentan aplicar los cambios. Si hay un error de validación, se atrapa.
    try {
      // Actualizacion los datos básicos del usuario (nombre, correo, rol).
      usuarioService.actualizarUsuario(correoOriginal, nuevoNombre, nuevoCorreo, nuevoRol);

      //  Revisamos si se quiera cambiar la contraseña
      //  Solo procederemos si el campo de nueva contraseña NO está vacío.
      if (nuevaContraseña != null && !nuevaContraseña.isEmpty()) {
        System.out.println("LOG: Se ha detectado un intento de cambio de contraseña.");

        //  La contraseña ingresada y su confirmacion deben coincidir
        if (!nuevaContraseña.equals(confirmarContraseña)) {
          System.err.println("ERROR: Las contraseñas no coinciden.");
          throw new IllegalArgumentException("Las contraseñas no coinciden. Inténtalo de nuevo.");
        }

        //  Si coinciden llamamos al metodo actualizarContraseñaUsuario
        //  Este método se encargará de la validación de seguridad y la encriptación.
        usuarioService.actualizarContraseñaUsuario(nuevoCorreo, nuevaContraseña);

        System.out.println("SUCCESS: La contraseña fue actualizada exitosamente.");
      }

      redirectAttributes.addFlashAttribute(
          "success", "Usuario '" + nuevoNombre + "' actualizado correctamente.");
      System.out.println("SUCCESS: Usuario actualizado exitosamente en la base de datos.");

    } catch (IllegalArgumentException | IllegalStateException | RegistroInvalidoException e) {
      throw new EdicionInvalidaException(confirmarContraseña, nuevoCorreo);
    }

    return "redirect:/admin";
  }

  /** Elimina un usuario. */
  @GetMapping("/admin/eliminar")
  public String eliminarUsuario(
      @RequestParam("correo") String correoAEliminar, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Usuario actor = userDetails.getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoAEliminar);

    // Chequeo de seguridad en el servidor
    if (!seguridadService.puedeEliminar(actor, objetivo)) {
      return "redirect:/acceso-denegado";
    }

    usuarioService.eliminarPorCorreo(correoAEliminar);
    redirectAttributes.addFlashAttribute(
        "success", "Usuario '" + objetivo.getNombreUsuario() + "' eliminado.");

    return "redirect:/admin";
  }

  /*
   * Elimina una tarea específica de un usuario.
   * Se invoca desde la vista de tareas del panel de administración.
   */
  @GetMapping("/admin/tareas/eliminar")
  public String eliminarTareaDeUsuario(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreTarea") String nombreTarea,
      RedirectAttributes redirectAttributes) {

    //  Obtenemos el usuario que está realizando la acción (el admin/moderador).
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Usuario actor = userDetails.getUsuario();

    //  Buscamos al usuario al que pertenece la tarea.
    Usuario objetivo = usuarioService.buscarPorCorreo(correoUsuario);

    //  Chequeo de seguridad: ¿Tiene el admin permiso para modificar a este usuario?
    //  Se reutiliza la lógica de 'puedeEditar' porque si puede editar al usuario,
    //  también debería poder gestionar sus tareas.
    if (!seguridadService.puedeGestionarTareasDe(actor, objetivo)) {
      System.out.println(
          "WARN: Fallo de seguridad al intentar eliminar tarea. El actor no tiene permisos sobre el objetivo.");
      return "redirect:/acceso-denegado";
    }

    //  Si los permisos son correctos, procedemos a eliminar la tarea.
    try {

      usuarioService.eliminarTarea(correoUsuario, nombreTarea);

      redirectAttributes.addFlashAttribute(
          "success",
          "Tarea '"
              + nombreTarea
              + "' del usuario '"
              + objetivo.getNombreUsuario()
              + "' ha sido eliminada correctamente.");
      System.out.println(
          "LOG: El admin '"
              + actor.getNombreUsuario()
              + "' eliminó la tarea '"
              + nombreTarea
              + "' del usuario '"
              + objetivo.getNombreUsuario()
              + "'.");

    } catch (RegistroInvalidoException e) {
      // Esto pasaría si la tarea no se encuentra, por ejemplo.
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      System.err.println("ERROR al eliminar tarea: " + e.getMessage());
    }

    //  Redirigimos de vuelta a la misma vista de tareas, manteniendo al usuario seleccionado.
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

  /*
   * Muestra el formulario para que un administrador edite la tarea de un usuario.
   * Este método se activa cuando se hace clic en el botón "Editar" de la tabla de tareas.
   *
   * @param correoUsuario El correo del usuario propietario de la tarea.
   * @param nombreTarea El nombre de la tarea a editar.
   * @param model El objeto Model para pasar datos a la vista.
   * @return El nombre de la plantilla del formulario de edición ('admin-tarea-form').
   */
  @GetMapping("/admin/tareas/editar")
  public String mostrarFormularioEditarTarea(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreTarea") String nombreTarea,
      Model model) {

    // Buscamos al usuario en la base de datos.
    Usuario usuario = usuarioService.buscarPorCorreo(correoUsuario);
    // Buscamos la tarea específica dentro de la lista de tareas de ese usuario.
    Tarea tarea = usuario.buscarTareaPorNombre(nombreTarea);

    // Medida de seguridad: si por alguna razón el usuario o la tarea no se encuentran,
    // evitamos un error y simplemente volvemos al panel de admin.
    if (usuario == null || tarea == null) {
      return "redirect:/admin";
    }

    // Pasamos tanto el usuario como la tarea a la vista.
    // El 'usuario' se necesita para el título y el enlace de "Cancelar".
    // La 'tarea' se necesita para rellenar los campos del formulario.
    model.addAttribute("usuario", usuario);
    model.addAttribute("tarea", tarea);

    // Devolvemos el nombre de nuestra nueva plantilla HTML.
    return "admin-tarea-form";
  }

  /*
   * Procesa y guarda los cambios de una tarea editada por un administrador.
   * Este método se activa cuando se envía el formulario desde 'admin-tarea-form.html'.
   *
   * @return Una redirección a la vista de tareas del usuario afectado.
   */
  @PostMapping("/admin/tareas/guardar")
  public String guardarTareaEditada(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreOriginal") String nombreOriginal,
      @RequestParam("nombre") String nuevoNombre,
      @RequestParam("descripcion") String nuevaDescripcion,
      @RequestParam("dificultad") String nuevaDificultad,
      RedirectAttributes redirectAttributes,
      Model model)
      throws AdminGuardarTareaException {

    Usuario usuario = usuarioService.buscarPorCorreo(correoUsuario);

    try {
      //  Creamos un objeto Tarea temporal con los nuevos datos recibidos del formulario.
      Tarea tareaActualizada = new Tarea(nuevoNombre, nuevaDescripcion, nuevaDificultad);

      //  Le pedimos al objeto Usuario que actualice la tarea original con los nuevos datos.
      //  Toda la lógica de validación está dentro de este método en la clase Usuario.
      usuario.actualizarTarea(nombreOriginal, tareaActualizada);

      //  Si la actualización fue exitosa, guardamos el estado completo de la base de datos.
      usuarioService.guardarEnBD(usuario);

      //  Preparamos un mensaje de éxito para mostrar después de la redirección.
      redirectAttributes.addFlashAttribute("success", "Tarea actualizada correctamente.");

      //  Redirigimos de vuelta a la vista de tareas, manteniendo al usuario seleccionado.
      redirectAttributes.addAttribute("vista", "tareas");
      redirectAttributes.addAttribute("correo", correoUsuario);
      return "redirect:/admin";

    } catch (TareaInvalidaException | RegistroInvalidoException e) {
      throw new AdminGuardarTareaException(e.getMessage(), usuario, nuevoNombre, nuevaDescripcion);
    }
  }

  @GetMapping("/admin/usuarios/nuevo")
  @ResponseBody // Usamos ResponseBody para no tener que crear un HTML todavía
  public String mostrarFormularioNuevoUsuario() {
    return "<h1>Formulario para crear un nuevo usuario (en construcción)</h1>";
  }

  @GetMapping("/admin/tareas/nuevo")
  @ResponseBody
  public String mostrarFormularioNuevaTarea() {
    return "<h1>Formulario para que el admin cree una nueva tarea para un usuario (en construcción)</h1>";
  }

  @PostMapping("/admin/crear")
  public String crearNuevoUsuario(
      @RequestParam("nombreUsuario") String nombre,
      @RequestParam("correo") String correo,
      @RequestParam("contraseña") String contraseña,
      @RequestParam("rol") Rol rol,
      RedirectAttributes redirectAttributes)
      throws AdminCrearUsuarioException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
    Usuario actor = userDetails.getUsuario();

    // Chequeo de seguridad: ¿puede este admin/mod asignar este rol?
    if (!seguridadService.puedeAsignarRol(actor, rol)) {
      redirectAttributes.addFlashAttribute(
          "errorCreacion", "No tienes permiso para crear un usuario con el rol de " + rol + ".");
      redirectAttributes.addAttribute("crearUsuario", true); // Mantenemos el modal abierto
      return "redirect:/admin";
    }

    try {
      // Creamos una instancia de Usuario. Lanzará una excepción si los datos son inválidos.
      Usuario nuevoUsuario = new Usuario(nombre, correo, contraseña);
      nuevoUsuario.setRol(rol);

      registrarUsuario(nuevoUsuario);

      redirectAttributes.addFlashAttribute(
          "success", "Usuario '" + nombre + "' creado exitosamente.");

    } catch (RegistroInvalidoException e) {
      throw new AdminCrearUsuarioException(e.getMessage());
    }

    return "redirect:/admin";
  }

  @PostMapping("/admin/tareas/crear")
  public String crearNuevaTarea(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombre") String nombre,
      @RequestParam("descripcion") String descripcion,
      @RequestParam("dificultad") String dificultad,
      RedirectAttributes redirectAttributes)
      throws AdminCrearTareaException, RegistroInvalidoException {

    Usuario usuario = usuarioService.buscarPorCorreo(correoUsuario);
    if (usuario == null) {
      redirectAttributes.addFlashAttribute(
          "errorCreacionTarea", "El usuario seleccionado no es válido.");
      redirectAttributes.addAttribute("crearTarea", true);
      return "redirect:/admin?vista=tareas";
    }

    try {
      // Creamos la nueva tarea
      Tarea nuevaTarea = new Tarea(nombre, descripcion, dificultad);

      // La añadimos al usuario
      usuario.agregarTarea(nuevaTarea);

      // Guardamos los cambios
      usuarioService.guardarEnBD(usuario);

      redirectAttributes.addFlashAttribute(
          "success",
          "Tarea '" + nombre + "' añadida exitosamente a " + usuario.getNombreUsuario() + ".");

      // Redirigimos a la vista de tareas, con el usuario ya seleccionado
      redirectAttributes.addAttribute("vista", "tareas");
      redirectAttributes.addAttribute("correo", correoUsuario);
      return "redirect:/admin";

    } catch (TareaInvalidaException e) {
      // Si hay un error de validación (nombre duplicado, etc.)
      throw new AdminCrearTareaException(e.getMessage());
    }
  }

  /*
  * Endpoint para que el Admin fuerce el reseteo de la temporada.
  * Se puede probar visitando la URL como admin.
  */
  @GetMapping("/admin/temporada/reset-manual")
  public String forzarReseteoTemporada(RedirectAttributes redirectAttributes) {  
    // Llamamos manualmente a la función de reseteo
    try {
      // Esta es la función que pusimos como @Transactional en el TemporadaService
      temporadaService.forzarReseteoManual();
            
      // Si todo sale bien, manda un mensaje de éxito
      redirectAttributes.addFlashAttribute("success", "¡Reseteo de temporada forzado con éxito! Todos los puntos de liga están en 0.");
        
    } catch (Exception e) {
      // Si algo falla (ej. la base de datos), manda un error
      redirectAttributes.addFlashAttribute("error", "Error al forzar el reseteo: " + e.getMessage());
    }
        
    // Vuelve a la vista de admin (top)
    redirectAttributes.addAttribute("vista", "top");
    return "redirect:/admin";
  }
    
  /** Página de "Acceso Denegado". */
  @GetMapping("/acceso-denegado")
  public String mostrarAccesoDenegado() {
    return "acceso-denegado";
  }

  /**
  * Guarda la nueva configuración del límite del Top en la BD.
  */
  @GetMapping("/admin/top/set-limite")
  public String setLimiteTop(
          @RequestParam("limite") int limite,
          RedirectAttributes redirectAttributes) {

      try {
          configuracionService.setLimiteTop(limite);
          redirectAttributes.addFlashAttribute("success", "Límite del Top guardado en " + limite + ".");
      } catch (Exception e) {
          redirectAttributes.addFlashAttribute("error", "Error al guardar el límite.");
      }

      // Vuelve a la vista 'top' con el nuevo límite activo
      return "redirect:/admin?vista=top&limite=" + limite;
  }
}
