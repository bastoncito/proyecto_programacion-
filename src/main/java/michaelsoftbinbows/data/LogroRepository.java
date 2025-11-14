package michaelsoftbinbows.data;

import michaelsoftbinbows.entities.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Importar @Repository

/** Repositorio para logros. */
@Repository // Añadimos la anotación @Repository
public interface LogroRepository extends JpaRepository<Logro, String> {

    /**
     * Cuenta todos los logros que coinciden con el estado 'activo' dado.
     * Spring Data JPA implementa este método automáticamente basado en el nombre.
     * @param activo true para contar activos, false para contar inactivos.
     * @return El número de logros que coinciden.
     */
    long countByActivo(boolean activo);

}