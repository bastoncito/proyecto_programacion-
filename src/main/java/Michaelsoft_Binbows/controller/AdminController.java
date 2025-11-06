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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UsuarioService usuarioService;
  @Autowired private SeguridadService seguridadService;
  @Autowired private TemporadaService temporadaService;
  @Autowired private ConfiguracionService configuracionService;

  public void registrarUsuario(Usuario usuario) throws RegistroInvalidoException {
    String encodedPassword = passwordEncoder.encode(usuario.getContraseña());
    usuario.setContraseña(encodedPassword);
    usuarioService.guardarEnBD(usuario);
  }

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
      @RequestParam(name = "preselectUser", required = false) String preselectedUserEmail, // Tu cambio
      @RequestParam(name = "errorConfig", required = false) String errorConfig, // Cambio entrante
      Model model) {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario usuarioActual = ((CustomUserDetails) auth.getPrincipal()).getUsuario();

    if (usuarioActual.getRol() != Rol.ADMIN && usuarioActual.getRol() != Rol.MODERADOR) {
      System.err.println("WARN: Intento de acceso no autorizado a /admin por usuario: " + usuarioActual.getNombreUsuario());
      return "redirect:/acceso-denegado";
    }

    model.addAttribute("vistaActual", vistaActual);
    model.addAttribute("usuarioActual", usuarioActual);
    model.addAttribute("seguridadService", seguridadService);
    model.addAttribute("listaDeUsuarios", usuarioService.obtenerTodos());
    model.addAttribute("rolesDisponibles", (usuarioActual.getRol() == Rol.ADMIN) ? List.of(Rol.MODERADOR, Rol.USUARIO) : List.of(Rol.USUARIO));
    if (error != null && !error.isEmpty()) model.addAttribute("error", error);
    if (errorCreacionTarea != null && !errorCreacionTarea.isEmpty()) model.addAttribute("errorCreacionTarea", errorCreacionTarea);

    if (crearUsuario) {
        System.out.println("Log: Se ha solicitado abrir el modal para crear un nuevo usuario.");
        model.addAttribute("mostrarModalCrear", true);
    }
    if (crearTarea) {
        System.out.println("Log: Se ha solicitado abrir el modal para crear una nueva tarea.");
        model.addAttribute("mostrarModalCrearTarea", true);
        if (preselectedUserEmail != null) { // Tu lógica
          model.addAttribute("preselectedUserEmail", preselectedUserEmail);
        }
    }
    if (mostrarConfig) {
        model.addAttribute("mostrarModalTopConfig", true);
        
        // Cargar los límites actuales para rellenar el formulario (lógica entrante)
        Map<String, Integer> limitesLiga = new HashMap<>();
        limitesLiga.put("LIGA_PLATA", configuracionService.getLimiteLiga("LIGA_PLATA", 500));
        limitesLiga.put("LIGA_ORO", configuracionService.getLimiteLiga("LIGA_ORO", 1500));
        limitesLiga.put("LIGA_PLATINO", configuracionService.getLimiteLiga("LIGA_PLATINO", 3000));
        limitesLiga.put("LIGA_DIAMANTE", configuracionService.getLimiteLiga("LIGA_DIAMANTE", 5000));
        model.addAttribute("limitesLiga", limitesLiga);
        
        if (errorConfig != null) { // Lógica entrante
          model.addAttribute("errorConfig", errorConfig);
        }
    }

    if (correoAEditar != null) {
      System.out.println("DEBUG: Se ha solicitado abrir el modal para editar al usuario: " + correoAEditar);
      Usuario usuarioAEditar = usuarioService.buscarPorCorreo(correoAEditar);
      if (usuarioAEditar != null && seguridadService.puedeEditar(usuarioActual, usuarioAEditar)) {
        model.addAttribute("usuarioParaEditar", usuarioAEditar);
      }
    }
    
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

    switch (vistaActual) {
      case "tareas":
        System.out.println("DEBUG: Cargando datos para la vista 'tareas'.");
        if (correoUsuarioSeleccionado != null) {
          model.addAttribute("usuarioSeleccionado", usuarioService.buscarPorCorreoConTareas(correoUsuarioSeleccionado));
        }
        break;
      case "top":
        int limiteActual = (limite == null) ? configuracionService.getLimiteTop() : limite;
        System.out.println("DEBUG: Cargando datos para la vista 'top' con límite: " + limiteActual);
        model.addAttribute("listaTop10", usuarioService.getTopUsuarios(limiteActual));
        model.addAttribute("limiteActual", limiteActual);
        break;
    }
    
    model.addAttribute("activePage", "admin");
    return "admin";
  }
  
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
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario actor = ((CustomUserDetails) auth.getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoOriginal);

    System.out.println("LOG: Actor '" + actor.getNombreUsuario() + "' (Rol: " + actor.getRol() + ") intenta editar a '" + objetivo.getNombreUsuario() + "'.");
    System.out.println("LOG: Datos recibidos del formulario:");
    System.out.println("LOG: -> Nuevo Nombre: '" + nuevoNombre + "'");
    System.out.println("LOG: -> Nuevo Correo: '" + nuevoCorreo + "'");
    System.out.println("LOG: -> Nuevo Rol: '" + nuevoRol + "'");

    if (!seguridadService.puedeEditar(actor, objetivo)) {
      System.out.println("WARN: Fallo de seguridad. El actor no tiene permisos para editar. Acción bloqueada.");
      return "redirect:/acceso-denegado";
    }
    if (!seguridadService.puedeAsignarRol(actor, nuevoRol)) {
      System.out.println("WARN: Intento no permitido de asignar el rol '" + nuevoRol + "'. Acción bloqueada.");
      redirectAttributes.addFlashAttribute("error", "No tienes permiso para asignar el rol de " + nuevoRol + ".");
      redirectAttributes.addAttribute("editarUsuarioCorreo", correoOriginal);
      return "redirect:/admin";
    }

    System.out.println("LOG: Permisos verificados. Intentando aplicar cambios en la capa de datos...");
    try {
      usuarioService.actualizarUsuario(correoOriginal, nuevoNombre, nuevoCorreo, nuevoRol);
      
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
      throw new EdicionInvalidaException(e.getMessage(), correoOriginal);
    }
    return "redirect:/admin";
  }

  @GetMapping("/admin/eliminar")
  public String eliminarUsuario(@RequestParam("correo") String correoAEliminar, RedirectAttributes redirectAttributes) {
    Usuario actor = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoAEliminar);

    System.out.println("Log: El usuario '" + actor.getNombreUsuario() + "' está intentando eliminar a '" + (objetivo != null ? objetivo.getNombreUsuario() : correoAEliminar) + "'.");
    
    if (objetivo == null) {
      System.err.println("ERROR: Se intentó eliminar un usuario que no existe con el correo: " + correoAEliminar);
      redirectAttributes.addFlashAttribute("error", "No se encontró el usuario a eliminar.");
      return "redirect:/admin";
    }
    if (!seguridadService.puedeEliminar(actor, objetivo)) {
      System.err.println("WARN: Fallo de seguridad al eliminar. El actor '" + actor.getNombreUsuario() + "' no tiene permisos sobre '" + objetivo.getNombreUsuario() + "'.");
      return "redirect:/acceso-denegado";
    }

    usuarioService.eliminarPorCorreo(correoAEliminar);
    redirectAttributes.addFlashAttribute("success", "Usuario '" + objetivo.getNombreUsuario() + "' eliminado.");
    System.out.println("SUCCESS: Usuario '" + objetivo.getNombreUsuario() + "' eliminado por '" + actor.getNombreUsuario() + "'.");
    return "redirect:/admin";
  }

  @GetMapping("/admin/tareas/eliminar")
  public String eliminarTareaDeUsuario(
      @RequestParam("correoUsuario") String correoUsuario,
      @RequestParam("nombreTarea") String nombreTarea,
      RedirectAttributes redirectAttributes) {

    Usuario actor = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();
    Usuario objetivo = usuarioService.buscarPorCorreo(correoUsuario);

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
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }

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
      Tarea tareaActualizada = new Tarea(nuevoNombre, nuevaDescripcion, nuevaDificultad);
      usuario.actualizarTarea(nombreOriginal, tareaActualizada);
      usuarioService.guardarEnBD(usuario);
      redirectAttributes.addFlashAttribute("success", "Tarea actualizada correctamente.");
    } catch (TareaInvalidaException | RegistroInvalidoException e) {
      throw new AdminGuardarTareaException(e.getMessage(), usuario, nuevoNombre, nuevaDescripcion);
    }
    redirectAttributes.addAttribute("vista", "tareas");
    redirectAttributes.addAttribute("correo", correoUsuario);
    return "redirect:/admin";
  }
  
  @PostMapping("/admin/crear")
  public String crearNuevoUsuario(
      @RequestParam("nombreUsuario") String nombre,
      @RequestParam("correo") String correo,
      @RequestParam("contraseña") String contraseña,
      @RequestParam("rol") Rol rol,
      RedirectAttributes redirectAttributes) throws AdminCrearUsuarioException {
    Usuario actor = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsuario();

    if (!seguridadService.puedeAsignarRol(actor, rol)) {
      redirectAttributes.addFlashAttribute("errorCreacion", "No tienes permiso para crear un usuario con el rol de " + rol + ".");
      redirectAttributes.addAttribute("crearUsuario", true);
      return "redirect:/admin";
    }

    System.out.println("LOG: Recibida petición POST para crear un nuevo usuario con nombre: " + nombre);
    try {
      Usuario nuevoUsuario = new Usuario(nombre, correo, contraseña);
      nuevoUsuario.setRol(rol);
      registrarUsuario(nuevoUsuario);
      redirectAttributes.addFlashAttribute("success", "Usuario '" + nombre + "' creado exitosamente.");
      System.out.println("SUCCESS: Usuario '" + nombre + "' creado exitosamente en la base de datos.");
    } catch (RegistroInvalidoException e) {
      System.err.println("ERROR: Falló la creación del usuario '" + nombre + "'. Causa: " + e.getMessage());
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
  
  @GetMapping("/acceso-denegado")
  public String mostrarAccesoDenegado() {
    return "acceso-denegado";
  }

  @PostMapping("/admin/ligas/guardar")
  public String guardarLimitesLigas(
          @RequestParam("limitePlata") int plata,
          @RequestParam("limiteOro") int oro,
          @RequestParam("limitePlatino") int platino,
          @RequestParam("limiteDiamante") int diamante,
          @RequestParam("limiteActual") int limiteActual,
          RedirectAttributes redirectAttributes) {

      if (plata <= 0 || oro <= 0 || platino <= 0 || diamante <= 0) {
          redirectAttributes.addFlashAttribute("errorConfig", "Error: Los puntos deben ser mayores a 0.");
          return "redirect:/admin?vista=top&mostrarConfig=true&limite=" + limiteActual;
      }
      
      if (!(plata < oro && oro < platino && platino < diamante)) {
          redirectAttributes.addFlashAttribute("errorConfig", "Error: El orden de ligas es incorrecto (Plata < Oro < Platino < Diamante).");
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

      redirectAttributes.addFlashAttribute("success", "Límites de liga actualizados. Todas las ligas han sido recalculadas.");
      return "redirect:/admin?vista=top&limite=" + limiteActual;
  }
}