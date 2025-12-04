package michaelsoftbinbows.controller;

import java.util.List;
import java.util.Map;
import michaelsoftbinbows.dto.PerfilUsuarioDto;
import michaelsoftbinbows.dto.UsuarioBusquedaDto;
import michaelsoftbinbows.services.UsuarioSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/social")
public class SocialController {

  @Autowired private UsuarioSocialService socialService;

  // API JSON
  @GetMapping("/buscar")
  @ResponseBody
  public List<UsuarioBusquedaDto> buscarUsuarios(@RequestParam String query) {
    return socialService.buscarUsuarios(query);
  }

  @GetMapping("/amigos")
  @ResponseBody
  public List<UsuarioBusquedaDto> misAmigos() {
    return socialService.obtenerMisAmigos();
  }

  @GetMapping("/solicitudes")
  @ResponseBody
  public List<UsuarioBusquedaDto> misSolicitudes() {
    return socialService.obtenerSolicitudesPendientes();
  }

  @PostMapping("/solicitud/enviar")
  @ResponseBody
  public ResponseEntity<?> enviarSolicitud(@RequestParam Long receptorId) {
    try {
      socialService.enviarSolicitud(receptorId);
      return ResponseEntity.ok(Map.of("mensaje", "Solicitud enviada"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/solicitud/responder")
  @ResponseBody
  public ResponseEntity<?> responderSolicitud(
      @RequestParam Long solicitudId, @RequestParam boolean aceptar) {
    try {
      socialService.responderSolicitud(solicitudId, aceptar);
      String accion = aceptar ? "aceptada" : "rechazada";
      return ResponseEntity.ok(Map.of("mensaje", "Solicitud " + accion));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  // --- ENDPOINT: ELIMINAR AMIGO ---
  @PostMapping("/amigo/eliminar")
  @ResponseBody
  public ResponseEntity<?> eliminarAmigo(@RequestParam Long amigoId) {
    try {
      socialService.eliminarAmigo(amigoId);
      return ResponseEntity.ok(Map.of("mensaje", "Amigo eliminado"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  // VISTAS HTML
  @GetMapping("/perfil/{id}")
  public String verPerfilUsuario(@PathVariable Long id, Model model) {
    try {
      PerfilUsuarioDto perfil = socialService.obtenerPerfilCompleto(id);
      model.addAttribute("perfil", perfil);

      // La variable ligaAnterior ahora es redundante si usas puntosMesPasado en la vista,
      // pero la dejamos por si acaso la usas en otro lado del template antiguo.
      model.addAttribute("ligaAnterior", calcularLigaPorPuntos(perfil.getPuntosMesPasado()));

      return "view_user_profile";
    } catch (IllegalArgumentException e) {
      return "redirect:/home?error=UsuarioNoEncontrado";
    }
  }

  private String calcularLigaPorPuntos(int puntos) {
    if (puntos >= 5000) return "Diamante";
    if (puntos >= 3000) return "Platino";
    if (puntos >= 1500) return "Oro";
    if (puntos >= 500) return "Plata";
    return "Bronce";
  }
}
