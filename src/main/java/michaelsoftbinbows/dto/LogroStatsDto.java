package michaelsoftbinbows.dto;

/**
 * DTO (Data Transfer Object) para transportar estadísticas de logros, como el conteo de cuántas
 * veces se ha completado.
 */
public class LogroStatsDto {

  private String nombre;
  private long conteo;

  public LogroStatsDto(String nombre, long conteo) {
    this.nombre = nombre;
    this.conteo = conteo;
  }

  // Getters
  public String getNombre() {
    return nombre;
  }

  public long getConteo() {
    return conteo;
  }
}
