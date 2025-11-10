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

/** Controller de API REST para logros. */
@RestController
@RequestMapping("/api/logros")
public class LogroController {

  @Autowired private LogroService logroService;

  /**
   * Permite ver todos los logros disponibles.
   *
   * @return lista de logros
   */
  @GetMapping
  public List<Logro> obtenerTodos() {
    return logroService.obtenerTodos();
  }

  /**
   * Muestra un logro en específico.
   *
   * @param id id del logro
   * @return información del logro (si se encuentra)
   */
  @GetMapping("/{id}")
  public Optional<Logro> obtenerPorId(@PathVariable String id) {
    return logroService.obtenerPorId(id);
  }

  /**
   * Crea un logro nuevo.
   *
   * @param logro logro a crear
   * @return logro registrado en base de datos
   */
  @PostMapping
  public Logro crear(@RequestBody Logro logro) {
    return logroService.guardar(logro);
  }

  /**
   * Elimina un logro de la base de datos.
   *
   * @param id id del logro a eliminar
   */
  @DeleteMapping("/{id}")
  public void eliminar(@PathVariable String id) {
    logroService.eliminar(id);
  }
}
