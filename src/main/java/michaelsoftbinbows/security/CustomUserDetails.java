package michaelsoftbinbows.security;

import java.util.Collection;
import java.util.List;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

  private final Usuario usuario;

  public CustomUserDetails(Usuario usuario) {
    this.usuario = usuario;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
  }

  @Override
  public String getPassword() {
    return usuario.getContrasena();
  }

  public Usuario getUsuario() {
    return usuario;
  }

  @Override
  public String getUsername() {
    return usuario.getCorreoElectronico();
  }

  // For simplicity, assume accounts are always valid:
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
