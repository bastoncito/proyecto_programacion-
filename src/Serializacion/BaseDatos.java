import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class BaseDatos implements Serializable{
    private List<Usuario> usuarios;
    private static final long serialVersionUID = 1L;
    public BaseDatos(){
        usuarios= cargarUsuarios();
    }
    public List<Usuario> cargarUsuarios(){
        File file = new File("lista");
        if(!file.exists()){
            return new ArrayList<>();
        }
        try{
            ObjectInputStream objectInput = new ObjectInputStream(new FileInputStream("lista"));
            return (List<Usuario>) objectInput.readObject();
        }catch(IOException | ClassNotFoundException e){
            return new ArrayList<>();
        }
    }
    public void guardarBaseDatos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("lista"))) {
            oos.writeObject(usuarios);
            System.out.println("Base de datos guardada correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
    public void imprimirTodosUsuarios(){
        if (usuarios.isEmpty()){
            System.out.println("no hay nada en la base de datos");
            return;
        }
        for(int i=0; i<usuarios.size();i++){
            Usuario u=usuarios.get(i);
            System.out.println("Usuario numero: "+(i+1));
            System.out.println(u.toString());
            if(u.getTareas()!=null){
                System.out.println(u.getTareas().toString());
            }
        }
        return;
    }
    public boolean agregarUsuario(Usuario usuario){
        for(Usuario u: usuarios){
            if(u.getNombre_usuario().equalsIgnoreCase(usuario.getNombre_usuario())){
                System.out.println("ya hay un usuario registrado con ese nombre");
                return false;
            }
        }
        usuarios.add(usuario);
        System.out.println("usuario agregado con exito");
        return true;
    }
}
