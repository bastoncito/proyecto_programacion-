package michaelsoftbinbows.dto;

/**
 * DTO ligero para mostrar usuarios en listas (Buscador, Amigos, Solicitudes).
 */
public class UsuarioBusquedaDto {
  private Long id;
  private String nombreUsuario;
  private String avatarUrl;
  private String liga;
  private int puntosLiga;
  
  // "AMIGOS", "PENDIENTE_ENVIADA", "PENDIENTE_RECIBIDA", "NADA"
  private String estadoRelacion; 
  
  // ID de la solicitud (para aceptar/rechazar)
  private Long solicitudId;

  public UsuarioBusquedaDto(Long id, String nombreUsuario, String avatarUrl, String liga, int puntosLiga) {
    this.id = id;
    this.nombreUsuario = nombreUsuario;
    this.avatarUrl = avatarUrl;
    this.liga = liga;
    this.puntosLiga = puntosLiga;
    this.estadoRelacion = "NADA"; 
  }

  // Getters y Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombreUsuario() { return nombreUsuario; }
  public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

  public String getLiga() { return liga; }
  public void setLiga(String liga) { this.liga = liga; }

  public int getPuntosLiga() { return puntosLiga; }
  public void setPuntosLiga(int puntosLiga) { this.puntosLiga = puntosLiga; }

  public String getEstadoRelacion() { return estadoRelacion; }
  public void setEstadoRelacion(String estadoRelacion) { this.estadoRelacion = estadoRelacion; }

  public Long getSolicitudId() { return solicitudId; }
  public void setSolicitudId(Long solicitudId) { this.solicitudId = solicitudId; }
}