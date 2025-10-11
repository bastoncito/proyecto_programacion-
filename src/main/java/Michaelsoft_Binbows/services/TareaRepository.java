package Michaelsoft_Binbows.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {
    
    List<Tarea> findByUsuarioId(Long usuarioId);
    
    List<Tarea> findByUsuarioCompletadoId(Long usuarioId);
    
    List<Tarea> findByUsuarioIdAndFechaCompletadaIsNull(Long usuarioId);
    
    List<Tarea> findByUsuarioIdAndFechaCompletadaIsNotNull(Long usuarioId);
}