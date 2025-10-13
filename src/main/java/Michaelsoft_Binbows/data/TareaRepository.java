package Michaelsoft_Binbows.data;

import org.springframework.data.jpa.repository.JpaRepository;

import Michaelsoft_Binbows.entities.Tarea;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    
}