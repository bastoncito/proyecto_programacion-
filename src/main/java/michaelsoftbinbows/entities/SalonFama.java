package michaelsoftbinbows.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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

  // JPA necesita un constructor vacío
  public SalonFama() {}

  // Constructor para guardar a los ganadores
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
