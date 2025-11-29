package michaelsoftbinbows.dto;

import java.util.List;
import michaelsoftbinbows.entities.Logro;

/**
 * DTO completo para la vista de perfil "Modo Foco".
 */
public class PerfilUsuarioDto {
  private Long id;
  private String nombreUsuario;
  private String avatarUrl;
  private int nivel;
  private int racha;
  
  private String ligaActual;
  private int puntosMesPasado; 
  
  private int tareasCompletadas;
  private int experienciaTotal;
  
  private List<Logro> logrosDesbloqueados;

  private String estadoAmistad; 

  public PerfilUsuarioDto() {}

  // Getters y Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombreUsuario() { return nombreUsuario; }
  public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

  public String getAvatarUrl() { return avatarUrl; }
  public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

  public int getNivel() { return nivel; }
  public void setNivel(int nivel) { this.nivel = nivel; }

  public int getRacha() { return racha; }
  public void setRacha(int racha) { this.racha = racha; }

  public String getLigaActual() { return ligaActual; }
  public void setLigaActual(String ligaActual) { this.ligaActual = ligaActual; }

  public int getPuntosMesPasado() { return puntosMesPasado; }
  public void setPuntosMesPasado(int puntosMesPasado) { this.puntosMesPasado = puntosMesPasado; }

  public int getTareasCompletadas() { return tareasCompletadas; }
  public void setTareasCompletadas(int tareasCompletadas) { this.tareasCompletadas = tareasCompletadas; }

  public int getExperienciaTotal() { return experienciaTotal; }
  public void setExperienciaTotal(int experienciaTotal) { this.experienciaTotal = experienciaTotal; }

  public List<Logro> getLogrosDesbloqueados() { return logrosDesbloqueados; }
  public void setLogrosDesbloqueados(List<Logro> logrosDesbloqueados) { this.logrosDesbloqueados = logrosDesbloqueados; }

  public String getEstadoAmistad() { return estadoAmistad; }
  public void setEstadoAmistad(String estadoAmistad) { this.estadoAmistad = estadoAmistad; }
}