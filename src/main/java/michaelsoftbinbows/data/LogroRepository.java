package michaelsoftbinbows.data;

import michaelsoftbinbows.dto.LogroStatsDto;
import michaelsoftbinbows.entities.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Pageable;


/** Repositorio para logros. */
@Repository
public interface LogroRepository extends JpaRepository<Logro, String> {

    /**
     * Cuenta todos los logros que coinciden con el estado 'activo' dado.
     */
    long countByActivo(boolean activo);

    /**
     * Busca todos los logros que coinciden con el estado 'activo' dado.
     * Spring Data JPA implementa este método automáticamente basado en el nombre.
     * @param activo true para buscar activos, false para buscar inactivos.
     * @return Una lista de logros que coinciden.
     */
    List<Logro> findAllByActivo(boolean activo);

/**
     * Consulta personalizada para obtener los logros más completados.
     * 1. Crea un nuevo LogroStatsDto por cada resultado.
     * 2. Une UsuarioLogro (ul) con su Logro (l).
     * 3. FILTRA solo los logros que están activos (l.activo = true). <-- ¡NUEVO!
     * 4. Agrupa por el ID y nombre del logro.
     * 5. Ordena por el conteo (COUNT(ul)) de forma descendente.
     * 6. Pageable nos permitirá limitar a solo 5 resultados.
     */
    @Query("SELECT new michaelsoftbinbows.dto.LogroStatsDto(l.nombre, COUNT(ul)) " +
           "FROM UsuarioLogro ul JOIN ul.logro l " +
           "WHERE l.activo = true " +  // <-- ¡AÑADE ESTA LÍNEA!
           "GROUP BY l.id, l.nombre " +
           "ORDER BY COUNT(ul) DESC")
    List<LogroStatsDto> findTopLogrosCompletados(Pageable pageable);

}



