package michaelsoftbinbows.services;

import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.data.LogroRepository;
import michaelsoftbinbows.dto.LogroStatsDto;
import michaelsoftbinbows.entities.Logro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar la lógica de negocio de los Logros. Proporciona métodos CRUD para
 * interactuar con el repositorio de logros.
 */
@Service
public class LogroService {

  @Autowired private LogroRepository logroRepository;

  /**
   * Obtiene la lista de todos los logros disponibles.
   *
   * @return Una lista de objetos Logro.
   */
  public List<Logro> obtenerTodos() {
    return logroRepository.findAll();
  }

  /**
   * Obtiene la lista de todos los logros que están marcados como 'activos'. Esto es útil para las
   * vistas de usuario (como el perfil) donde no queremos mostrar logros que el admin ha
   * desactivado.
   *
   * @return Una lista de objetos Logro activos.
   */
  public List<Logro> obtenerTodosActivos() {
    // Asumimos que tu LogroRepository tiene este método
    return logroRepository.findAllByActivo(true);
  }

  /**
   * Obtiene un logro específico por su ID.
   *
   * @param id El ID del logro a buscar.
   * @return Un Optional que contiene el Logro si se encuentra, o vacío si no.
   */
  public Optional<Logro> obtenerPorId(String id) {
    return logroRepository.findById(id);
  }

  /**
   * Guarda un nuevo logro o actualiza uno existente en la base de datos.
   *
   * @param logro El objeto Logro a guardar.
   * @return El Logro guardado (puede incluir un ID actualizado).
   */
  public Logro guardar(Logro logro) {
    return logroRepository.save(logro);
  }

  /**
   * Elimina un logro de la base de datos usando su ID.
   *
   * @param id El ID del logro a eliminar.
   */
  public void eliminar(String id) {
    logroRepository.deleteById(id);
  }

  /**
   * Obtiene el conteo total de logros definidos en la BD.
   *
   * @return long el número total de logros.
   */
  public long getConteoTotalLogros() {
    return logroRepository.count();
  }

  /**
   * Obtiene el conteo de logros que están marcados como 'activos'.
   *
   * @return long el número de logros activos.
   */
  public long getConteoLogrosActivos() {
    return logroRepository.countByActivo(true);
  }

  /**
   * Obtiene una lista del Top 5 de logros más completados por los usuarios.
   *
   * @return Lista de LogroStatsDto (nombre y conteo).
   */
  public List<LogroStatsDto> getTop5LogrosMasCompletados() {
    // Creamos un "Pageable" que pide la página 0 y un tamaño de 5.
    Pageable top5 = PageRequest.of(0, 5);
    return logroRepository.findTopLogrosCompletados(top5);
  }
}
