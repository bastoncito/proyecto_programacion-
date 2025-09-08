import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;
public class EjecutoraMenu {
    public static void main(){
        BaseDatos base = new BaseDatos();
        Scanner sc =  new Scanner(System.in);
        while(true){
            try{
                System.out.println("=====MENÚ=====\n1. Agendar Tarea\n2. Registrar Nuevo Usuario\n3. Lista de Usuarios\n0. Fin");
                switch(sc.nextInt()){
                    case 1:
                        System.out.println("¿Para cuál usuario (ingrese nombre o correo)?");
                        String id = sc.nextLine();
                        Usuario actual = id.contains("@") ? base.usuarioExistePorCorreo(id) : base.usuarioExistePorNombre(id);
                        if(actual == null){
                            System.err.println("! -- Usuario no encontrado.");
                            break;
                        }else{
                            System.out.println("Ingrese nombre de la tarea:");
                            System.out.println("Ingrese descripción:");
                            System.out.println("Ingrese cantidad de Exp.:");
                            System.out.println("Ingrese fecha ()");
                            //Date fecha = DateFormat.parse("");
                        }
                        break;
                    case 2:
                        sc.nextLine();
                        System.out.println("Ingrese nombre de usuario (entre 3 y 30 carácteres): ");
                        String nombre = sc.nextLine();
                        System.out.println("Ingrese correo electrónico: ");
                        String correo = sc.nextLine();
                        System.out.println("Ingrese contraseña (debe incluir una mayúscula, un número, y un carácter especial): ");
                        String contraseña = sc.nextLine();
                        Usuario nuevo = new Usuario(nombre, correo, contraseña);
                        base.agregarUsuario(nuevo);
                        break;
                    case 3:
                        System.out.println("=== Base de datos ===");
                        base.imprimirTodosUsuarios();
                        break;
                    case 0:
                        System.out.println("Fin del programa.");
                        base.guardarBaseDatos();
                        sc.close();
                        return;
                    default:

                        break;
                }
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
    }
    
}
