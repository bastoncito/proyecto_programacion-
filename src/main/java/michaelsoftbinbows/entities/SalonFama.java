package michaelsoftbinbows.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad que representa a un ganador en el Salón de la Fama al final de una temporada. Almacena
 * una instantánea del top 3 de jugadores.
 */
@Entity
public class SalonFama {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int puesto; // 1, 2, o 3
  private String temporadaNombre; // ej: "Octubre 2025"
  private String nombreUsuario;
  private int puntos;
  private String liga;

  /** Constructor vacío requerido por JPA. */
  public SalonFama() {
    // Constructor vacío
  }

  /**
   * Constructor para guardar a los ganadores de una temporada.
   *
   * @param puesto El puesto en el podio (1, 2, o 3).
   * @param temporadaNombre El nombre de la temporada (ej. "Octubre 2025").
   * @param nombreUsuario El nombre del usuario ganador.
   * @param puntos Los puntos con los que terminó.
   * @param liga La liga en la que terminó.
   */
  public SalonFama(
      int puesto, String temporadaNombre, String nombreUsuario, int puntos, String liga) {
    this.puesto = puesto;
    this.temporadaNombre = temporadaNombre;
    this.nombreUsuario = nombreUsuario;
    this.puntos = puntos;
    this.liga = liga;
  }

  // --- Getters y Setters ---

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getPuesto() {
    return puesto;
  }

  public void setPuesto(int puesto) {
    this.puesto = puesto;
  }

  public String getTemporadaNombre() {
    return temporadaNombre;
  }

  public void setTemporadaNombre(String temporadaNombre) {
    this.temporadaNombre = temporadaNombre;
  }

  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public int getPuntos() {
    return puntos;
  }

  public void setPuntos(int puntos) {
    this.puntos = puntos;
  }

  public String getLiga() {
    return liga;
  }

  public void setLiga(String liga) {
    this.liga = liga;
  }
}
