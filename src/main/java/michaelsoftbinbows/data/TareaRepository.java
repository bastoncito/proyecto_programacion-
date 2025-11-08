package michaelsoftbinbows.data;

import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.entities.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio para tareas. */
public interface TareaRepository extends JpaRepository<Tarea, Long> {
  Optional<Tarea> findByUsuarioIdAndEsSemanalTrue(Long usuarioId);

  List<Tarea> findAllByUsuarioIdAndEsSemanalTrue(Long usuarioId);
}
