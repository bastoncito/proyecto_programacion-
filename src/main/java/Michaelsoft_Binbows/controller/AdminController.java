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
import Michaelsoft_Binbows.services.TemporadaService;
import Michaelsoft_Binbows.services.UsuarioService;
import java.util.List;
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
 * Controlador para el panel de administración.
 * Maneja la visualización y gestión de usuarios, tareas y configuraciones del sistema,
 * asegurando que solo los usuarios con roles de ADMIN o MODERADOR puedan acceder.
 */
@Controller
public class AdminController {

  // --- Inyección de Dependencias ---
  // Spring se encarga de instanciar y proporcionar estos servicios al controlador.

  @Autowired private PasswordEncoder passwordEncoder; // Para encriptar contraseñas.
  @Autowired private UsuarioService usuarioService; // Lógica de negocio para usuarios.
  @Autowired private SeguridadService seguridadService; // Reglas de permisos y roles.
  @Autowired private TemporadaService temporadaService; // Lógica para el reseteo de temporadas.
  @Autowired private ConfiguracionService configuracionService; // Para ajustes del sistema como el límite del Top.

  /**
   * Método de ayuda para registrar un nuevo usuario con la contraseña ya encriptada.
   * Centraliza la lógica de encriptación antes de guardar.
   *
   * @param usuario El objeto Usuario a registrar.
   * @throws RegistroInvalidoException si los datos del usuario no son válidos (ej. correo duplicado).
   */
  public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
    // Se encripta la contraseña del usuario antes de guardarla en la base de datos.
    String encodedPassword = passwordEncoder.encode(usuario.getContraseña());
    usuario.setContraseña(encodedPassword);
    usuarioService.guardarEnBD(usuario);
  }

  /**
   * Método principal que maneja todas las peticiones GET a /admin.
   * Actúa como un enrutador central para las diferentes vistas del panel (Usuarios, Tareas, Top)
   * y gestiona la lógica para mostrar los modales de edición y creación.
   *
   * RESPONSABILIDADES:
   *
   * - Seguridad: Verifica que el usuario logueado tenga rol de ADMIN o MODERADOR.
   * - Enrutamiento de Vistas: Usa el parámetro 'vista' para decidir qué sección mostrar.
   * - Gestión de Modales: Lee los parámetros de la URL (ej. 'editarUsuarioCorreo', 'crearUsuario')
   *   para determinar si se debe mostrar un modal.
   * - Carga de Datos: Prepara y añade al Model todos los datos necesarios para la plantilla de Thymeleaf.
   *
   * @param vistaActual El nombre de la vista a mostrar ('usuarios', 'tareas', 'top').
   * @param correoAEditar Correo del usuario para el cual se debe abrir el modal de edición.
   * @param crearUsuario Activa el modal de creación de usuario si es 'true'.
   * @param error Mensaje de error opcional pasado por RedirectAttributes.
   * @param correoUsuarioSeleccionado Correo del usuario que se muestra en la vista de 'Tareas'.
   * @param crearTarea Activa el modal para añadir una nueva tarea si es 'true'.
   * @param errorCreacionTarea Mensaje de error específico para el modal de creación de tareas.
   * @param nombreTareaParaEditar Nombre de la tarea para la cual se debe abrir el modal de edición.
   * @param correoUsuarioParaEditar Correo del propietario de la tarea a editar.
   * @param limite Define cuántos usuarios mostrar en la vista 'Top'.
   * @param mostrarConfig Activa el modal de configuración del Top si es 'true'.
   * @param preselectedUserEmail El correo del usuario seleccionado al estar en la seccion de agregar tarea.
   * @param model Objeto Model de Spring para pasar atributos a la vista.
   * @return El nombre de la plantilla a renderizar ("admin").
   */
  @GetMapping("/admin")
  public String mostrarPanelAdmin(
      @RequestParam(name = "vista", required = false, defaultValue = "usuarios") String vistaActual,
      @RequestParam(name = "editarUsuarioCorreo", required = false) String correoAEditar,
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
      Model model) {

    // Se obtiene el usuario que está actualmente logueado para verificar sus permisos.
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario usuarioActual = ((CustomUserDetails) auth.getPrincipal()).getUsuario();

    // Primera barrera de seguridad: si el usuario no tiene el rol adecuado, se le redirige.
    if (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR) {
      System.err.println("WARN: Intento de acceso no autorizado a /admin por usuario: " + usuarioActual.getNombreUsuario());
      return "redirect:/acceso-denegado";
    }

    // Se añaden al modelo los datos comunes que todas las vistas del panel necesitan.
    model.addAttribute("vistaActual", vistaActual);
    model.addAttribute("usuarioActual", usuarioActual);
    model.addAttribute("seguridadService", seguridadService); // Para usar sus métodos en Thymeleaf.
    model.addAttribute("listaDeUsuarios", usuarioService.obtenerTodos());
    // Se define qué roles puede asignar el usuario actual (un admin puede asignar más que un moderador).
    model.addAttribute("rolesDisponibles", (usuarioActual.getRol() == Rol.ADMIN) ? List.of(Rol.MODERADOR, Rol.USUARIO) : List.of(Rol.USUARIO));
    if (error != null && !error.isEmpty()) model.addAttribute("error", error);
    if (errorCreacionTarea != null && !errorCreacionTarea.isEmpty()) model.addAttribute("errorCreacionTarea", errorCreacionTarea);

    // --- Lógica para activar los modales (ventanas emergentes) ---

    if (crearUsuario) {
        System.out.println("Log: Se ha solicitado abrir el modal para crear un nuevo usuario.");
        model.addAttribute("mostrarModalCrear", true);
    }
    if (crearTarea) {
        System.out.println("Log: Se ha solicitado abrir el modal para crear una nueva tarea.");
        model.addAttribute("mostrarModalCrearTarea", true);
        // Si venimos de la vista de un usuario, lo preseleccionamos en el formulario.
        if (preselectedUserEmail != null) {
          model.addAttribute("preselectedUserEmail", preselectedUserEmail);
      }
    }
    if (mostrarConfig) {
        System.out.println("Log: Se ha solicitado abrir el modal para mostrar configuraciones de top.");
        model.addAttribute("mostrarModalTopConfig", true);
    }

    // Si la URL contiene 'editarUsuarioCorreo', se busca al usuario y se prepara el modal de edición.
    if (correoAEditar != null) {
      System.out.println("DEBUG: Se ha solicitado abrir el modal para editar al usuario: " + correoAEditar);
      Usuario usuarioAEditar = usuarioService.buscarPorCorreo(correoAEditar);
      // Doble chequeo: el usuario debe existir y el admin debe tener permiso para editarlo.
      if (usuarioAEditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAEditar)) {
        model.addAttribute("usuarioParaEditar", usuarioAEditar);
      }
    }
    
    // Lógica similar para abrir el modal de edición de una tarea.
    if (nombreTareaParaEditar != null && correoUsuarioParaEditar != null) {
        System.out.println("DEBUG: Se ha solicitado abrir el modal para editar la tarea '" + nombreTareaParaEditar + "' del usuario: " + correoUsuarioParaEditar);
        Usuario usuarioDeLaTarea = usuarioService.buscarPorCorreo(correoUsuarioParaEditar);
        if (usuarioDeLaTarea != null && seguridadService.puedeGestionarTareasDe(usuarioActual, usuarioDeLaTarea)) {
            Tarea tareaParaEditar = usuarioDeLaTarea.buscarTareaPorNombre(nombreTareaParaEditar);
            if (tareaParaEditar != null) {
                model.addAttribute("tareaParaEditar", tareaParaEditar);
                model.addAttribute("usuarioDeLaTarea", usuarioDeLaTarea);
            }
        }
    }

    // --- Carga de datos específicos para cada vista ---
    switch (vistaActual) {
      case "tareas":
        System.out.println("DEBUG: Cargando datos para la vista 'tareas'.");
        if (correoUsuarioSeleccionado != null) {
          // Si se seleccionó un usuario, se carga su perfil con sus tareas.
          model.addAttribute("usuarioSeleccionado", usuarioService.buscarPorCorreoConTareas(correoUsuarioSeleccionado));
        }
        break;
      case "top":
        // Se determina el límite de usuarios a mostrar, ya sea por parámetro o desde la configuración guardada.
        int limiteActual = (limite == null) ? configuracionService.getLimiteTop() : limite;
        System.out.println("DEBUG: Cargando datos para la vista 'top' con límite: " + limiteActual);
        model.addAttribute("listaTop10", usuarioService.getTopUsuarios(limiteActual));
        model.addAttribute("limiteActual", limiteActual);
        break;
    }
    
    // Se indica a Thymeleaf que la página activa es 'admin' para iluminar el ícono en la navbar.
    model.addAttribute("activePage", "admin");
    return "admin"; // Devuelve el nombre del archivo HTML (admin.html).
  }
  
  /**
   * Procesa el formulario para guardar los cambios de un usuario editado.
   *
   * @param nuevoNombre El nuevo nombre de usuario.
   * @param correoOriginal El correo original del usuario, para identificarlo en la BD.
   * @param nuevoCorreo El nuevo correo electrónico.
   * @param nuevoRol El nuevo rol asignado.
   * @param nuevaContraseña La nueva contraseña (opcional).
   * @param confirmarContraseña Confirmación de la nueva contraseña.
   * @param redirectAttributes Permite enviar mensajes (feedback) a la vista después de una redirección.
   * @return Una redirección a la página de administración.
   */
  @PostMapping("/admin/guardar")
  public String guardarUsuarioEditado(
      @RequestParam("nombreUsuario") String nuevoNombre,
      @RequestParam("correoElectronicoOriginal") String correoOriginal,
      @RequestParam("nuevoCorreo") String nuevoCorreo,
      @RequestParam("rol") Rol nuevoRol,
      @RequestParam(name = "nuevaContraseña", required = false) String nuevaContraseña,
      @RequestParam(name = "confirmarContraseña", required = false) String confirmarContraseña,
      RedirectAttributes redirectAttributes) throws EdicionInvalidaException {

    System.out.println("\n--- INICIO PROCESO DE EDICIÓN DE USUARIO ---");
    // Se obtiene el usuario que realiza la acción (actor) y el que será modificado (objetivo).
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario actor = ((CustomUserDetails) auth.getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoOriginal);

    System.out.println("LOG: Actor '" + actor.getNombreUsuario() + "' (Rol: " + actor.getRol() + ") intenta editar a '" + objetivo.getNombreUsuario() + "'.");
    System.out.println("LOG: Datos recibidos del formulario:");
    System.out.println("LOG: -> Nuevo Nombre: '" + nuevoNombre + "'");
    System.out.println("LOG: -> Nuevo Correo: '" + nuevoCorreo + "'");
    System.out.println("LOG: -> Nuevo Rol: '" + nuevoRol + "'");

    // Se verifica si el actor tiene permisos para editar al usuario objetivo.
    if (!seguridadService.puedeEditar(actor, objetivo)) {
      System.out.println("WARN: Fallo de seguridad. El actor no tiene permisos para editar. Acción bloqueada.");
      return "redirect:/acceso-denegado";
    }
    // Se verifica si el actor puede asignar el rol especificado.
    if (!seguridadService.puedeAsignarRol(actor, nuevoRol)) {
      System.out.println("WARN: Intento no permitido de asignar el rol '" + nuevoRol + "'. Acción bloqueada.");
      redirectAttributes.addFlashAttribute("error", "No tienes permiso para asignar el rol de " + nuevoRol + ".");
      redirectAttributes.addAttribute("editarUsuarioCorreo", correoOriginal); // Para reabrir el modal.
      return "redirect:/admin";
    }

    System.out.println("LOG: Permisos verificados. Intentando aplicar cambios en la capa de datos...");
    try {
      // Se actualizan los datos básicos del usuario.
      usuarioService.actualizarUsuario(correoOriginal, nuevoNombre, nuevoCorreo, nuevoRol);
      
      // Si se proporcionó una nueva contraseña, se procesa el cambio.
      if (nuevaContraseña != null && !nuevaContraseña.isEmpty()) {
        System.out.println("LOG: Se ha detectado un intento de cambio de contraseña.");
        if (!nuevaContraseña.equals(confirmarContraseña)) {
          System.err.println("ERROR: Las contraseñas no coinciden.");
          throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }
        usuarioService.actualizarContraseñaUsuario(nuevoCorreo, nuevaContraseña);
        System.out.println("SUCCESS: La contraseña fue actualizada exitosamente.");
      }
      redirectAttributes.addFlashAttribute("success", "Usuario '" + nuevoNombre + "' actualizado correctamente.");
      System.out.println("SUCCESS: Usuario actualizado exitosamente en la base de datos.");
    } catch (IllegalArgumentException | RegistroInvalidoException e) {
      // Si algo falla, se lanza una excepción personalizada para que GlobalExceptionHandler la maneje.
      throw new EdicionInvalidaException(e.getMessage(), correoOriginal);
    }
    return "redirect:/admin";
  }

  /**
   * Maneja la solicitud para eliminar un usuario del sistema.
   *
   * @param correoAEliminar El correo del usuario a eliminar.
   * @param redirectAttributes Para enviar mensajes de feedback a la vista.
   * @return Redirección al panel de administración.
   */
  @GetMapping("/admin/eliminar")
  public String eliminarUsuario(@RequestParam("correo") String correoAEliminar, RedirectAttributes redirectAttributes) {
    Usuario actor = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoAEliminar);

    System.out.println("Log: El usuario '" + actor.getNombreUsuario() + "' está intentando eliminar a '" + (objetivo != null ? objetivo.getNombreUsuario() : correoAEliminar) + "'.");
    
    // Verifica si el usuario a eliminar realmente existe.
    if (objetivo == null) {
      System.err.println("ERROR: Se intentó eliminar un usuario que no existe con el correo: " + correoAEliminar);
      redirectAttributes.addFlashAttribute("error", "No se encontró el usuario a eliminar.");
      return "redirect:/admin";
    }
    // Comprueba los permisos de eliminación.
    if (!seguridadService.puedeEliminar(actor, objetivo)) {
      System.err.println("WARN: Fallo de seguridad al eliminar. El actor '" + actor.getNombreUsuario() + "' no tiene permisos sobre '" + objetivo.getNombreUsuario() + "'.");
      return "redirect:/acceso-denegado";
    }

    // Si todo está en orden, procede con la eliminación.
    usuarioService.eliminarPorCorreo(correoAEliminar);
    redirectAttributes.addFlashAttribute("success", "Usuario '" + objetivo.getNombreUsuario() + "' eliminado.");
    System.out.println("SUCCESS: Usuario '" + objetivo.getNombreUsuario() + "' eliminado por '" + actor.getNombreUsuario() + "'.");
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

    Usuario actor = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoUsuario);

    // Verifica si el admin puede gestionar las tareas del usuario objetivo.
    if (!seguridadService.puedeGestionarTareasDe(actor, objetivo)) {
      System.out.println("WARN: Fallo de seguridad al intentar eliminar tarea. El actor no tiene permisos sobre el objetivo.");
      return "redirect:/acceso-denegado";
    }

    try {
      usuarioService.eliminarTarea(correoUsuario, nombreTarea);
      redirectAttributes.addFlashAttribute("success", "Tarea '" + nombreTarea + "' del usuario '" + objetivo.getNombreUsuario() + "' ha sido eliminada.");
      System.out.println("LOG: El admin '" + actor.getNombreUsuario() + "' eliminó la tarea '" + nombreTarea + "' del usuario '" + objetivo.getNombreUsuario() + "'.");
    } catch (RegistroInvalidoException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      System.err.println("ERROR al eliminar tarea: " + e.getMessage());
    }
    // Redirige de vuelta a la vista de tareas, manteniendo seleccionado al mismo usuario.
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

  /**
   * Procesa la edición de una tarea existente desde el panel de administración.
   *
   * @return Redirección a la vista de tareas.
   */
  @PostMapping("/admin/tareas/guardar")
  public String guardarTareaEditada(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreOriginal") String nombreOriginal,
      @RequestParam("nombre") String nuevoNombre,
      @RequestParam("descripcion") String nuevaDescripcion,
      @RequestParam("dificultad") String nuevaDificultad,
      RedirectAttributes redirectAttributes) throws AdminGuardarTareaException {

    Usuario usuario = usuarioService.buscarPorCorreo(correoUsuario);
    try {
      // Crea un objeto Tarea temporal con los nuevos datos para actualizar la original.
      Tarea tareaActualizada = new Tarea(nuevoNombre, nuevaDescripcion, nuevaDificultad);
      usuario.actualizarTarea(nombreOriginal, tareaActualizada);
      usuarioService.guardarEnBD(usuario);
      redirectAttributes.addFlashAttribute("success", "Tarea actualizada correctamente.");
    } catch (TareaInvalidaException | RegistroInvalidoException e) {
      // Si hay un error, se lanza una excepción para que el handler la capture y muestre el formulario de nuevo.
      throw new AdminGuardarTareaException(e.getMessage(), usuario, nuevoNombre, nuevaDescripcion);
    }
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }
  
  /**
   * Procesa el formulario para crear un nuevo usuario desde el panel de admin.
   *
   * @return Redirección al panel de administración.
   */
  @PostMapping("/admin/crear")
  public String crearNuevoUsuario(
      @RequestParam("nombreUsuario") String nombre,
      @RequestParam("correo") String correo,
      @RequestParam("contraseña") String contraseña,
      @RequestParam("rol") Rol rol,
      RedirectAttributes redirectAttributes) throws AdminCrearUsuarioException {
    Usuario actor = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();

    // Comprueba si el admin tiene permiso para asignar el rol seleccionado.
    if (!seguridadService.puedeAsignarRol(actor, rol)) {
      redirectAttributes.addFlashAttribute("errorCreacion", "No tienes permiso para crear un usuario con el rol de " + rol + ".");
      redirectAttributes.addAttribute("crearUsuario", true);
      return "redirect:/admin";
    }

    System.out.println("LOG: Recibida petición POST para crear un nuevo usuario con nombre: " + nombre);
    try {
      Usuario nuevoUsuario = new Usuario(nombre, correo, contraseña);
      nuevoUsuario.setRol(rol);
      registrarUsuario(nuevoUsuario); // Este método ya encripta la contraseña.
      redirectAttributes.addFlashAttribute("success", "Usuario '" + nombre + "' creado exitosamente.");
      System.out.println("SUCCESS: Usuario '" + nombre + "' creado exitosamente en la base de datos.");
    } catch (RegistroInvalidoException e) {
      System.err.println("ERROR: Falló la creación del usuario '" + nombre + "'. Causa: " + e.getMessage());
      throw new AdminCrearUsuarioException(e.getMessage());
    }
    return "redirect:/admin";
  }

  /**
   * Procesa el formulario para añadir una nueva tarea a un usuario específico.
   *
   * @return Redirección a la vista de tareas.
   */
  @PostMapping("/admin/tareas/crear")
  public String crearNuevaTarea(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombre") String nombre,
      @RequestParam("descripcion") String descripcion,
      @RequestParam("dificultad") String dificultad,
      RedirectAttributes redirectAttributes) throws AdminCrearTareaException, RegistroInvalidoException {
    Usuario usuario = usuarioService.buscarPorCorreo(correoUsuario);
    if (usuario == null) {
      redirectAttributes.addFlashAttribute("errorCreacionTarea", "El usuario seleccionado no es válido.");
      redirectAttributes.addAttribute("crearTarea", true);
      return "redirect:/admin?vista=tareas";
    }

    try {
      Tarea nuevaTarea = new Tarea(nombre, descripcion, dificultad);
      usuario.agregarTarea(nuevaTarea);
      usuarioService.guardarEnBD(usuario);
      redirectAttributes.addFlashAttribute("success", "Tarea '" + nombre + "' añadida exitosamente a " + usuario.getNombreUsuario() + ".");
    } catch (TareaInvalidaException e) {
      throw new AdminCrearTareaException(e.getMessage());
    }
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

  /**
   * Endpoint para forzar un reseteo manual de la temporada (puntos de liga).
   * Solo accesible por administradores.
   *
   * @return Redirección a la vista de Top.
   */
  @GetMapping("/admin/temporada/reset-manual")
  public String forzarReseteoTemporada(RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String actor = auth.getName();
    System.out.println("LOG: El admin '" + actor + "' está forzando un reseteo de temporada manual.");
    try {
      temporadaService.forzarReseteoManual();
      System.out.println("SUCCESS: Reseteo de temporada forzado con éxito por '" + actor + "'.");
      redirectAttributes.addFlashAttribute("success", "¡Reseteo de temporada forzado con éxito!");
    } catch (Exception e) {
      System.err.println("ERROR: Falló el reseteo de temporada manual. Causa: " + e.getMessage());
      redirectAttributes.addFlashAttribute("error", "Error al forzar el reseteo: " + e.getMessage());
    }
    return "redirect:/admin?vista=top";
  }

  /**
   * Endpoint para establecer el número de usuarios a mostrar en la tabla del Top.
   *
   * @param limite El número de usuarios a mostrar.
   * @return Redirección a la vista de Top con el límite actualizado.
   */
  @GetMapping("/admin/top/set-limite")
  public String setLimiteTop(@RequestParam("limite") int limite, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String actor = auth.getName();
    System.out.println("LOG: El admin '" + actor + "' está cambiando el límite del Top a: " + limite);
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
}