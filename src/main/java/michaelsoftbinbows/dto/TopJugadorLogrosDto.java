package michaelsoftbinbows.dto;

/** DTO actualizado para incluir el avatar del usuario. */
public class TopJugadorLogrosDto {

  private String nombreUsuario;
  private long totalLogros;
  private String avatarUrl;

  // Constructor actualizado
  public TopJugadorLogrosDto(String nombreUsuario, long totalLogros, String avatarUrl) {
    this.nombreUsuario = nombreUsuario;
    this.totalLogros = totalLogros;
    this.avatarUrl = avatarUrl;
  }

  // Getters
  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public long getTotalLogros() {
    return totalLogros;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }
}
