package Michaelsoft_Binbows.controller;

import Michaelsoft_Binbows.services.LogroService;
import Michaelsoft_Binbows.services.Logro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

    @Autowired
    private LogroService logroService;

    @GetMapping
    public List<Logro> obtenerTodos() {
        return logroService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Logro> obtenerPorId(@PathVariable String id) {
        return logroService.obtenerPorId(id);
    }

    @PostMapping
    public Logro crear(@RequestBody Logro logro) {
        return logroService.guardar(logro);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        logroService.eliminar(id);
    }
}