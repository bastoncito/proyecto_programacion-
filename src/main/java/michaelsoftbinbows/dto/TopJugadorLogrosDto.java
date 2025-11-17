package michaelsoftbinbows.dto;

/**
 * DTO (Data Transfer Object) para transportar los datos del Top 5 de jugadores por logros. Es una
 * clase simple solo con getters y un constructor.
 */
public class TopJugadorLogrosDto {

  private String nombreUsuario;
  private long totalLogros;

  public TopJugadorLogrosDto(String nombreUsuario, long totalLogros) {
    this.nombreUsuario = nombreUsuario;
    this.totalLogros = totalLogros;
  }

  // Getters
  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public long getTotalLogros() {
    return totalLogros;
  }
}
