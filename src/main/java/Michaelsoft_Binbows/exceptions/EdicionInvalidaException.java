package Michaelsoft_Binbows.exceptions;

public class EdicionInvalidaException extends Exception {
  private String correo;

  public EdicionInvalidaException(String message, String correo) {
    super(message);
    this.correo = correo;
  }

  public String getCorreo() {
    return correo;
  }
}
