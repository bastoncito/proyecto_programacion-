package Michaelsoft_Binbows.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Configuracion {

  @Id private String clave;

  private String valor;

  // Constructores, Getters y Setters
  public Configuracion() {}

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
