import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.regex.Pattern;
public class Usuario{
    String nombre_usuario,correo_electronico,contraseña;
    private List<Tarea> tareas;

    public Usuario(String nombre_usuario, String correo_electronico, String contraseña){
        setNombre_usuario(nombre_usuario);
        setContraseña(contraseña);
        setCorreo_electronico(correo_electronico);
        this.tareas=new ArrayList<>();
    }
    /**
     * Getters/Setters
     * @return
     */
    public String getNombre_usuario() {
        return nombre_usuario;
    }
    public String getCorreo_electronico() {
        return correo_electronico;
    }
    public String getContraseña() {
        return contraseña;
    }
    public void setNombre_usuario(String nombre_usuario) {
        if(esNombreValido(nombre_usuario)){
            this.nombre_usuario = nombre_usuario;
        }else{
            throw new IllegalArgumentException("Nombre de usuario no válido: " + nombre_usuario);
        }
    }
    public void setCorreo_electronico(String correo_electronico) {
        if(esCorreoValido(correo_electronico)){
            this.correo_electronico=correo_electronico;
        }else{
            throw new IllegalArgumentException("Correo electronico no válido: " + correo_electronico);
        }
    }
    public void setContraseña(String nuevaContraseña) {
    String resultado = validarContrasena(nuevaContraseña);
    if (resultado == null) {
        this.contraseña = nuevaContraseña;
    } else {
        throw new IllegalArgumentException(resultado);
    }
}
    /**
     * Recibe una Tarea como parametro
     * la agrega a la base de datos si su nombre, descricpcion y exp son validos
     */
    public void agregarTarea(Tarea tarea){//TRABAJAR AQUI---->>>Validar si la tarea se puede agregar
        // Si pasa las validaciones, la agregamos
        if(validarTarea(tarea)){
            tareas.add(tarea);
            System.out.println("Tarea '" + tarea.getNombre() + "' agregada exitosamente.");
        }
    }

    private boolean validarTarea(Tarea tarea){
        if (tarea == null) {
            System.out.println("Error: La tarea no puede ser nula.");
            return false;
        }
        // Validar que el nombre no esté vacío
        if (tarea.getNombre() == null || tarea.getNombre().isEmpty()) {
            System.out.println("Error: El nombre de la tarea no puede estar vacía.");
            return false;
        }
        // Validar que la descripcion no esté vacía
        if (tarea.getDescripcion() == null || tarea.getDescripcion().isEmpty()) {
            System.out.println("Error: La descripción de la tarea no puede estar vacía.");
            return false;
        }
        // Validar que exp no esté vacía y sea positiva
        if (tarea.getExp() == 0) {
            System.out.println("Error: La exp de la tarea no puede estar vacía.");
            return false;
        }
        if (tarea.getExp() < 0) {
            System.out.println("Error: La exp de la tarea no puede ser negativa.");
            return false;
        }
        // Validar que la fecha no este vacia y no sea menor a la fecha actual
        if (tarea.getFecha_expiracion() == null) {
            System.out.println("Error: La fecha no puede estar vacía.");
            return false;
        }
        Date fecha_actual = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//Simplificar el formato
        String fechaTarea = sdf.format(tarea.getFecha_expiracion());
        String fechaHoy = sdf.format(fecha_actual);

        if (fechaTarea.compareTo(fechaHoy) < 0) {
            System.out.println("Error: La fecha debe ser igual o posterior a hoy.");
            return false;
        }
        // Validar por nombre repetido en el ArrayList
        if (tareaExistePorNombre(tarea.getNombre())) {
            System.out.println("Ya existe una tarea con el nombre: " + tarea.getNombre());
            return false;
        }
        // Validar por descripción repetida en el ArrayList
        if (tareaExistePorDescripcion(tarea.getDescripcion())) {
            System.out.println("Ya existe una tarea con la descripción: " + tarea.getDescripcion());
            return false;
        }

        return true;
    }

    private boolean tareaExistePorNombre(String nombre){
        for (Tarea tareaExistente : tareas) {
            if (tareaExistente.getNombre().equalsIgnoreCase(nombre)) {
                System.out.println("Ya existe una tarea con el nombre: " + nombre);
                return true;
            }
        }
        return false;
    }
    private boolean tareaExistePorDescripcion(String descripcion){
        for (Tarea tareaExistente : tareas) {
            if (tareaExistente.getDescripcion().equalsIgnoreCase(descripcion)) {
                System.out.println("Ya existe una tarea con la descripcion: " + descripcion);
                return true;
            }
        }
        return false;
    }
    private boolean esNombreValido(String nombre){
        if(nombre==null){
            return false;
        }
        if(nombre.trim().isEmpty()){
            return false;
        }
        if(nombre.length()<3 || nombre.length()>30 ){
            return false;
        }
        return true;
    }
    private static boolean esCorreoValido(String correo) {
    if (correo == null || correo.trim().isEmpty()) {
        return false;
    }
    String regex = "[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*@[a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*[.][a-zA-Z]{2,5}";
    // [a-zA-Z0-9_]+ UNO O MAS, caracter (letras o numeros o '_')
    // ([.][a-zA-Z0-9_]+)* CERO O MAS, punto '.' seguido de almenos un caracter 
    // @ UN simbolo arroba
    // [a-zA-Z0-9_]+ UNO O MAS, caracter (letras o numeros o '_')
    // ([.][a-zA-Z0-9_]+)* CERO O MAS, punto '.' seguido de almenos un caracter
    // [.][a-zA-Z]{2,5} UN punto '.', seguido de DOS A CINCO letras
    return (correo.matches(regex));
    }

    public static String validarContrasena(String contraseña) {
    if (contraseña == null || contraseña.trim().isEmpty()) {
        return "La contraseña no puede estar vacía";
    }

    if (contraseña.length() < 8) {
        return "La contraseña debe tener al menos 8 caracteres";
    }

    if (contraseña.contains(" ")) {
        return "La contraseña no puede contener espacios";
    }

    boolean tieneMayuscula = Pattern.compile("[A-Z]").matcher(contraseña).find();
    boolean tieneMinuscula = Pattern.compile("[a-z]").matcher(contraseña).find();
    boolean tieneDigito = Pattern.compile("\\d").matcher(contraseña).find();
    boolean tieneCaracterEspecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{}|;:'\",.<>/?]").matcher(contraseña).find();

    
    if (!tieneMayuscula || !tieneMinuscula || !tieneDigito || !tieneCaracterEspecial) {
         StringBuilder errores = new StringBuilder();
         if (!tieneMayuscula) errores.append("- Debe contener al menos una mayúscula\n");
        if (!tieneMinuscula) errores.append("- Debe contener al menos una minúscula\n");
        if (!tieneDigito) errores.append("- Debe contener al menos un dígito\n");
        if (!tieneCaracterEspecial) errores.append("- Debe contener al menos un carácter especial (!@#$%^&* etc.)\n");
        return "La contraseña es demasiado débil. Requisitos:\n" + errores.toString();
    }

    return null; // Null indica que la contraseña es válida
}

    public List<Tarea> getTareas() {
        return tareas;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario: ").append(nombre_usuario).append("\n")
          .append("Correo: ").append(correo_electronico).append("\n")
          .append("Número de tareas: ").append(tareas.size()).append("\n");
          return sb.toString();
    }
}
