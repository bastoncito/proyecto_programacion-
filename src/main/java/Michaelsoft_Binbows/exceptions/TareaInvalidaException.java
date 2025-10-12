package Michaelsoft_Binbows.exceptions;

public class TareaInvalidaException extends Exception{
    private String nombre, descripcion;
    public TareaInvalidaException(String message, String nombre, String descripcion){
        super(message);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    public String getNombre(){
        return nombre;
    }
    public String getDescripcion(){
        return descripcion;
    }
}

