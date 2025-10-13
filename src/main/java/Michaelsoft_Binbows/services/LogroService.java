package Michaelsoft_Binbows.services;

import Michaelsoft_Binbows.data.LogroRepository;
import Michaelsoft_Binbows.entities.Logro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogroService {

    @Autowired
    private LogroRepository logroRepository;

    public List<Logro> obtenerTodos() {
        return logroRepository.findAll();
    }

    public Optional<Logro> obtenerPorId(String id) {
        return logroRepository.findById(id);
    }

    public Logro guardar(Logro logro) {
        return logroRepository.save(logro);
    }

    public void eliminar(String id) {
        logroRepository.deleteById(id);
    }

    
}