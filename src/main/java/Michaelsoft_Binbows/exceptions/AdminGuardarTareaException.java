package Michaelsoft_Binbows.exceptions;

import Michaelsoft_Binbows.entities.Usuario;

public class AdminGuardarTareaException extends Exception{
    private Usuario usuario;
    private String nombreTarea, descripcionTarea;
    public AdminGuardarTareaException(String message, Usuario usuario, String nombreTarea, String descripcionTarea){
        super(message);
        this.usuario = usuario;
        this.nombreTarea = nombreTarea;
        this.descripcionTarea = descripcionTarea;
    }
    public Usuario getUsuario(){
        return usuario;
    }
    public String getNombreTarea(){
        return nombreTarea;
    }
    public String getDescripcionTarea(){
        return descripcionTarea;
    }
}
