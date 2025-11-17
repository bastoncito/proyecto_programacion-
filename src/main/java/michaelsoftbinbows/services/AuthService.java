package michaelsoftbinbows.services;

import jakarta.transaction.Transactional;
import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** Servicio que maneja la autenticación y sesión actual del usuario. */
@Service
public class AuthService {
  @Autowired UsuarioRepository usuarioRepository;

  /**
   * Obtiene el usuario actualmente autenticado en el contexto de seguridad con todas sus
   * colecciones cargadas (Transactional).
   *
   * @return Objeto Usuario del usuario autenticado con tareas inicializadas.
   */
  @Transactional
  public Usuario getCurrentUser() {
    // Obtenemos los detalles desde el contexto de seguridad
    CustomUserDetails userDetails =
        (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    // Recuperamos la entidad Usuario gestionada por JPA desde el repositorio. Esto asegura
    // que las colecciones LAZY puedan inicializarse dentro del contexto transaccional.
    Long usuarioId = userDetails.getUsuario().getId();
    Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
    if (usuario != null) {
      // Inicializar colecciones necesarias para las vistas
      var unused = usuario.getTareas().size();
      // Si la aplicación usa tareas completadas separadas, inicializarlas también
      try {
        unused = usuario.getTareasCompletadas().size();
      } catch (Exception e) {
        // Ignorar si no existe ese método o la lista está vacía; solo buscamos inicializar
      }
    }
    return usuario;
  }

  /**
   * Obtiene la autenticación actual del contexto de seguridad.
   *
   * @return Objeto Authentication de la sesión actual.
   */
  public Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

  /**
   * Obtiene los detalles del usuario actualmente autenticado.
   *
   * @return Objeto CustomUserDetails del usuario autenticado.
   */
  public CustomUserDetails getUserDetails() {
    return (CustomUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  /**
   * Actualiza la sesión de seguridad con un nuevo usuario.
   *
   * <p>Utilizado para cuando se editan los datos del usuario activo.
   *
   * @param nuevoUsuario Nuevo objeto Usuario con los datos actualizados.
   */
  public void actualizarSesion(Long idUsuario) {
    Usuario actualizado = usuarioRepository.findById(idUsuario).orElseThrow();
    CustomUserDetails userDetails = new CustomUserDetails(actualizado);
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
            userDetails, getAuthentication().getCredentials(), userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  public void vaciarSesion() {
    SecurityContextHolder.clearContext();
  }
}
