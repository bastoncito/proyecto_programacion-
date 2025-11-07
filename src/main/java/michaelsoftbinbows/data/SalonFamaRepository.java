package Michaelsoft_Binbows.data;

import Michaelsoft_Binbows.entities.SalonFama;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SalonFamaRepository extends JpaRepository<SalonFama, Long> {

    /**
     * Busca a los ganadores guardados y los ordena por puesto (1°, 2°, 3°).
     * (Asumimos que solo guardaremos el Top 3 del último mes).
     */
    List<SalonFama> findAllByOrderByPuestoAsc();
}