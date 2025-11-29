package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import michaelsoftbinbows.data.SolicitudAmistadRepository;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.dto.PerfilUsuarioDto;
import michaelsoftbinbows.dto.UsuarioBusquedaDto;
import michaelsoftbinbows.entities.SolicitudAmistad;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioSocialService {

  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private UsuarioService usuarioService;
  @Autowired private SolicitudAmistadRepository solicitudRepository;
  @Autowired private AuthService authService;

  
  public List<UsuarioBusquedaDto> buscarUsuarios(String query) {
    Usuario yo = authService.getCurrentUser();
    List<Usuario> resultados = usuarioRepository.findByNombreUsuarioContainingIgnoreCase(query);
    return resultados.stream()
        .filter(u -> !u.getId().equals(yo.getId()))
        .map(u -> convertirADtoConEstado(u, yo))
        .collect(Collectors.toList());
  }

  public List<UsuarioBusquedaDto> obtenerMisAmigos() {
    Usuario yo = authService.getCurrentUser();
    List<SolicitudAmistad> amistades = solicitudRepository.findAmistadesDeUsuario(yo);
    return amistades.stream()
        .map(s -> {
          Usuario amigo = s.getSolicitante().getId().equals(yo.getId()) ? s.getReceptor() : s.getSolicitante();
          return convertirADtoConEstado(amigo, yo);
        })
        .collect(Collectors.toList());
  }

  public List<UsuarioBusquedaDto> obtenerSolicitudesPendientes() {
    Usuario yo = authService.getCurrentUser();
    List<SolicitudAmistad> solicitudes = 
        solicitudRepository.findByReceptorAndEstado(yo, SolicitudAmistad.Estado.PENDIENTE);
    return solicitudes.stream()
        .map(s -> {
          Usuario solicitante = s.getSolicitante();
          UsuarioBusquedaDto dto = new UsuarioBusquedaDto(
              solicitante.getId(),
              solicitante.getNombreUsuario(),
              solicitante.getAvatarUrl(),
              solicitante.getLiga(),
              solicitante.getPuntosLiga()
          );
          dto.setEstadoRelacion("PENDIENTE_RECIBIDA");
          dto.setSolicitudId(s.getId());
          return dto;
        })
        .collect(Collectors.toList());
  }

  // ==========================================
  // ACCIONES (Enviar, Aceptar, Rechazar, ELIMINAR)
  // ==========================================

  @Transactional
  public void enviarSolicitud(Long receptorId) {
    Usuario yo = authService.getCurrentUser();
    Usuario receptor = usuarioService.obtenerPorId(receptorId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    if (solicitudRepository.existeSolicitudEntre(yo, receptor)) {
      throw new IllegalStateException("Ya existe una relación con este usuario.");
    }

    SolicitudAmistad solicitud = new SolicitudAmistad(yo, receptor);
    solicitudRepository.save(solicitud);
  }

  @Transactional
  public void responderSolicitud(Long solicitudId, boolean aceptar) {
    SolicitudAmistad solicitud = solicitudRepository.findById(solicitudId)
        .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));
    
    Usuario yo = authService.getCurrentUser();

    if (!solicitud.getReceptor().getId().equals(yo.getId())) {
      throw new SecurityException("No tienes permiso para responder esta solicitud.");
    }

    if (aceptar) {
      solicitud.setEstado(SolicitudAmistad.Estado.ACEPTADA);
      solicitudRepository.save(solicitud);
    } else {
      solicitudRepository.delete(solicitud);
    }
  }

  // --- MÉTODO PARA ELIMINAR AMIGO ---
  @Transactional
  public void eliminarAmigo(Long amigoId) {
      Usuario yo = authService.getCurrentUser();
      Usuario exAmigo = usuarioService.obtenerPorId(amigoId)
          .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

      // Buscamos la relación entre los dos
      SolicitudAmistad amistad = solicitudRepository.findSolicitudEntre(yo, exAmigo)
          .orElseThrow(() -> new IllegalStateException("No existe relación con este usuario"));

      // Solo eliminamos si efectivamente son amigos (o hay una solicitud colgada)
      // Borramos el registro de la BD, rompiendo el vínculo.
      solicitudRepository.delete(amistad);
  }

  public PerfilUsuarioDto obtenerPerfilCompleto(Long usuarioId) {
    Usuario objetivo = usuarioService.obtenerPorId(usuarioId)
        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    Usuario yo = authService.getCurrentUser();

    PerfilUsuarioDto dto = new PerfilUsuarioDto();
    dto.setId(objetivo.getId());
    dto.setNombreUsuario(objetivo.getNombreUsuario());
    dto.setAvatarUrl(objetivo.getAvatarUrl());
    dto.setNivel(objetivo.getNivelExperiencia());
    dto.setRacha(objetivo.getRacha());
    dto.setLigaActual(objetivo.getLiga());
    dto.setPuntosMesPasado(objetivo.getPuntosMesPasado());
    dto.setTareasCompletadas(objetivo.getNumeroCompletadas());
    dto.setExperienciaTotal(objetivo.getExperienciaTotal());
    dto.setLogrosDesbloqueados(objetivo.getLogros());

    Optional<SolicitudAmistad> relacion = solicitudRepository.findSolicitudEntre(yo, objetivo);
    
    if (relacion.isPresent()) {
        SolicitudAmistad sol = relacion.get();
        if (sol.getEstado() == SolicitudAmistad.Estado.ACEPTADA) {
            dto.setEstadoAmistad("AMIGOS");
        } else if (sol.getEstado() == SolicitudAmistad.Estado.PENDIENTE) {
            if (sol.getSolicitante().getId().equals(yo.getId())) {
                dto.setEstadoAmistad("PENDIENTE_ENVIADA");
            } else {
                dto.setEstadoAmistad("PENDIENTE_RECIBIDA");
            }
        }
    } else {
        dto.setEstadoAmistad("NADA");
    }
    return dto;
  }

  private UsuarioBusquedaDto convertirADtoConEstado(Usuario objetivo, Usuario yo) {
    UsuarioBusquedaDto dto = new UsuarioBusquedaDto(
        objetivo.getId(),
        objetivo.getNombreUsuario(),
        objetivo.getAvatarUrl(),
        objetivo.getLiga(),
        objetivo.getPuntosLiga()
    );
    Optional<SolicitudAmistad> relacion = solicitudRepository.findSolicitudEntre(yo, objetivo);

    if (relacion.isEmpty()) {
      dto.setEstadoRelacion("NADA");
    } else {
      SolicitudAmistad sol = relacion.get();
      if (sol.getEstado() == SolicitudAmistad.Estado.ACEPTADA) {
        dto.setEstadoRelacion("AMIGOS");
      } else if (sol.getEstado() == SolicitudAmistad.Estado.PENDIENTE) {
         if (sol.getSolicitante().getId().equals(yo.getId())) {
           dto.setEstadoRelacion("PENDIENTE_ENVIADA");
         } else {
           dto.setEstadoRelacion("PENDIENTE_RECIBIDA");
           dto.setSolicitudId(sol.getId());
         }
      }
    }
    return dto;
  }
}