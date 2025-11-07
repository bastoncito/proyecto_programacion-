package michaelsoftbinbows.data;

import java.util.Optional;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio para Usuario. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  /**
   * Busca a un Usuario por su email.
   *
   * @param correo email del usuario
   * @return objeto opcional de Usuario (puede estar vacío o no)
   */
  Optional<Usuario> findByCorreoElectronico(String correo);

  /**
   * Busca a un Usuario por su nombre.
   *
   * @param usernameOrEmail nombre del usuario
   * @return objeto opcional de Usuario (puede estar vacío o no)
   */
  Optional<Usuario> findByNombreUsuario(String usernameOrEmail);

  /**
   * Devuelve una lista de cierto largo con usuarios ordenados por sus puntos.
   *
   * @param pageable cantidad de usuarios en lista
   * @return lista de usuarios ordenados por puntos
   */
  Page<Usuario> findByOrderByPuntosLigaDesc(Pageable pageable);
}
