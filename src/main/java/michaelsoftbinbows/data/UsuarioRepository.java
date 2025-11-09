package michaelsoftbinbows.data;

import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la entidad Usuario. Proporciona métodos CRUD y consultas
 * personalizadas.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  /**
   * Busca un usuario por su dirección de correo electrónico.
   *
   * @param correo El correo a buscar.
   * @return Un Optional que contiene al Usuario si se encuentra.
   */
  Optional<Usuario> findByCorreoElectronico(String correo);

  /**
   * Busca un usuario por su nombre de usuario.
   *
   * @param usernameOrEmail El nombre de usuario a buscar.
   * @return Un Optional que contiene al Usuario si se encuentra.
   */
  Optional<Usuario> findByNombreUsuario(String usernameOrEmail);

  /**
   * Busca una página de usuarios ordenados por puntos de liga en orden descendente.
   *
   * @param pageable El objeto Pageable que define el tamaño de página y el número.
   * @return Una Página (Page) de Usuarios.
   */
  Page<Usuario> findByOrderByPuntosLigaDesc(Pageable pageable);

  /**
   * Busca los 3 mejores usuarios ordenados por puntos de liga en orden descendente.
   *
   * @return Una Lista con los 3 mejores Usuarios.
   */
  List<Usuario> findTop3ByOrderByPuntosLigaDesc();
}
