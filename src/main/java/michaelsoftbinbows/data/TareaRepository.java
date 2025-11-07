package michaelsoftbinbows.data;

import michaelsoftbinbows.entities.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio para tareas. */
public interface TareaRepository extends JpaRepository<Tarea, Long> {}
