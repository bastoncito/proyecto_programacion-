package Michaelsoft_Binbows.services;

import Michaelsoft_Binbows.data.TareaRepository;
import Michaelsoft_Binbows.data.UsuarioRepository;
import Michaelsoft_Binbows.dto.TareaDTO;
import Michaelsoft_Binbows.entities.Tarea;
import Michaelsoft_Binbows.exceptions.TareaInvalidaException;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Tarea> obtenerTodas() {
        return tareaRepository.findAll();
    }

    public Optional<Tarea> obtenerPorId(Long id) {
        return tareaRepository.findById(id);
    }

    @Transactional
    public Tarea crear(TareaDTO tareaDTO, long userId) throws TareaInvalidaException {
        Tarea tarea = new Tarea(tareaDTO.nombre, tareaDTO.descripcion, tareaDTO.dificultad);
        tarea.setUsuario(usuarioRepository.findById(userId).get());
        return guardar(tarea);
    }

    @Transactional
    public Tarea actualizar(Long id, TareaDTO tareaDTO) throws TareaInvalidaException {
        Tarea tarea = tareaRepository.findById(id).get();
        tarea.setNombre(tareaDTO.nombre);
        tarea.setDescripcion(tareaDTO.descripcion);
        //Actualizar otros campos según sea necesario
        return guardar(tarea);
    } 

    public Tarea guardar(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public void eliminar(Long id) {
        tareaRepository.deleteById(id);
    }

}