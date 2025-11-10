package michaelsoftbinbows.services;

import michaelsoftbinbows.data.UsuarioRepository;
import michaelsoftbinbows.entities.Usuario;
import michaelsoftbinbows.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio personalizado para cargar los detalles del usuario para Spring Security. Implementa
 * UserDetailsService para buscar usuarios por nombre de usuario o correo.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    Usuario usuario = usuarioRepository.findByNombreUsuario(usernameOrEmail).orElse(null);
    if (usuario == null) {
      usuario = usuarioRepository.findByCorreoElectronico(usernameOrEmail).orElse(null);
    }
    if (usuario == null) {
      throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
    }
    return new CustomUserDetails(usuario);
  }
}
