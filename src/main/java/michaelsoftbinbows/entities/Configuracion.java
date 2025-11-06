package michaelsoftbinbows.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Entidad que representa un par clave-valor de configuración en la base de datos.
 * Se utiliza para almacenar ajustes globales de la aplicación.
 */
@Entity
public class Configuracion {

  @Id private String clave;

  private String valor;

  // Constructores, Getters y Setters
  /**
   * Constructor vacío requerido por JPA.
   */
  public Configuracion() {
    // Constructor vacío
  }

  /**
   * Constructor para crear un nuevo ajuste de configuración.
   *
   * @param clave La clave única para el ajuste (ej. "top_limite").
   * @param valor El valor asociado a la clave (ej. "10").
   */
  public Configuracion(String clave, String valor) {
    this.clave = clave;
    this.valor = valor;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getValor() {
    return valor;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }
}