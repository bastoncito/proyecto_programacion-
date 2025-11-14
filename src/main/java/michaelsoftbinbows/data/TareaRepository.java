package michaelsoftbinbows.data;

import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.entities.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio para tareas. */
public interface TareaRepository extends JpaRepository<Tarea, Long> {
  /**
   * Busca la primera tarea semanal activa de un usuario.
   *
   * @param usuarioId El ID del usuario.
   * @return Un Optional con la Tarea semanal, si existe.
   */
  Optional<Tarea> findByUsuarioIdAndEsSemanalTrue(Long usuarioId);

  /**
   * Verifica si un usuario tiene una tarea semanal activa.
   *
   * @param usuarioId El ID del usuario.
   * @return true si existe una tarea semanal activa, false en caso contrario.
   */
  boolean existsByUsuarioIdAndEsSemanalTrue(Long usuarioId);

  /**
   * Busca todas las tareas semanales activas de un usuario.
   *
   * @param usuarioId El ID del usuario.
   * @return Una Lista de Tareas semanales.
   */
  List<Tarea> findAllByUsuarioIdAndEsSemanalTrue(Long usuarioId);

  /**
   * Busca una tarea por su nombre y el ID del usuario.
   *
   * @param nombre Nombre de la tarea
   * @param usuarioId Id del usuario
   * @return Optional con la tarea si se encuentra
   */
  Optional<Tarea> findByNombreAndUsuarioId(String nombre, Long usuarioId);

  /**
   * Verifica si existe una tarea con el mismo nombre para un usuario dado.
   *
   * @param nombre Nombre de la tarea
   * @param usuarioId Id del usuario
   * @return true si existe, false en caso contrario
   */
  boolean existsByNombreAndUsuarioId(String nombre, Long usuarioId);

  /**
   * Busca una tarea por su descripción y el ID del usuario.
   *
   * @param descripcion Descripción de la tarea
   * @param usuarioId Id del usuario
   * @return Optional con la tarea si se encuentra
   */
  Optional<Tarea> findByDescripcionAndUsuarioId(String descripcion, Long usuarioId);

  /**
   * Verifica si existe una tarea con la misma descripción para un usuario dado.
   *
   * @param descripcion Descripción de la tarea
   * @param usuarioId Id del usuario
   * @return true si existe, false en caso contrario
   */
  boolean existsByDescripcionAndUsuarioId(String descripcion, Long usuarioId);

  /**
   * Busca todas las tareas de un usuario.
   *
   * @param usuarioId Id del usuario
   * @return Lista de tareas del usuario
   */
  List<Tarea> findAllByUsuarioId(Long usuarioId);
}
