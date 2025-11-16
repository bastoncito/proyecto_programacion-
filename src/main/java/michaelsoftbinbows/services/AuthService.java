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
    Usuario usuario =
        ((CustomUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsuario();
    // Fuerza la carga de las colecciones LAZY mientras estamos en contexto transaccional
    usuario.getTareas().size(); // Inicializa la colección de tareas
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
