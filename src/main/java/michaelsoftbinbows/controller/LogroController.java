package michaelsoftbinbows.controller;

import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.entities.Logro;
import michaelsoftbinbows.services.LogroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

  @Autowired private LogroService logroService;

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
