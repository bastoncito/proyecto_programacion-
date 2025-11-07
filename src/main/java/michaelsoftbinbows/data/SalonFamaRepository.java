package michaelsoftbinbows.data;

import java.util.List;
import michaelsoftbinbows.entities.SalonFama;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalonFamaRepository extends JpaRepository<SalonFama, Long> {

  /**
   * Busca a los ganadores guardados y los ordena por puesto (1°, 2°, 3°). (Asumimos que solo
   * guardaremos el Top 3 del último mes).
   */
  List<SalonFama> findAllByOrderByPuestoAsc();
}
