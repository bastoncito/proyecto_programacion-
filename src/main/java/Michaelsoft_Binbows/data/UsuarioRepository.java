package Michaelsoft_Binbows.data;

import Michaelsoft_Binbows.entities.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByCorreoElectronico(String correo);

  Optional<Usuario> findByNombreUsuario(String usernameOrEmail);

  Page<Usuario> findByOrderByPuntosLigaDesc(Pageable pageable);
}
