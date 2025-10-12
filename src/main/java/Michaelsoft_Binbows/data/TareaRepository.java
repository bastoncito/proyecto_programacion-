package Michaelsoft_Binbows.data;

import Michaelsoft_Binbows.services.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    
}