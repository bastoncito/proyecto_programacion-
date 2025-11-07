package michaelsoftbinbows.data;

import michaelsoftbinbows.entities.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio para configuración del top de usuarios. */
public interface ConfiguracionRepository extends JpaRepository<Configuracion, String> {}
