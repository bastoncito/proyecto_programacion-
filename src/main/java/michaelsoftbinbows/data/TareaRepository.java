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
   * Busca todas las tareas semanales activas de un usuario.
   *
   * @param usuarioId El ID del usuario.
   * @return Una Lista de Tareas semanales.
   */
  List<Tarea> findAllByUsuarioIdAndEsSemanalTrue(Long usuarioId);
}
