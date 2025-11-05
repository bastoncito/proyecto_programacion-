package michaelsoftbinbows.data;

import java.util.Optional;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByCorreoElectronico(String correo);

  Optional<Usuario> findByNombreUsuario(String usernameOrEmail);

  Page<Usuario> findByOrderByPuntosLigaDesc(Pageable pageable);
}
