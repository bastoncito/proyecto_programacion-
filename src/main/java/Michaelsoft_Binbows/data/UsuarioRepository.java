package Michaelsoft_Binbows.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Michaelsoft_Binbows.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoElectronico(String correo);

    Optional<Usuario> findByNombreUsuario(String usernameOrEmail);
    
    
}