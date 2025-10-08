package Michaelsoft_Binbows;

import Michaelsoft_Binbows.services.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, Usuario> users = new HashMap<>();
    private final BaseDatos baseDatos;

    public CustomUserDetailsService(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Usuario usuario = baseDatos.buscarUsuarioPorNombre(usernameOrEmail);
        if (usuario == null){
            usuario = baseDatos.buscarUsuarioPorCorreo(usernameOrEmail);
        }
        if (usuario == null) throw new UsernameNotFoundException("User not found: " + usernameOrEmail);
        return new CustomUserDetails(usuario);
    }

}
