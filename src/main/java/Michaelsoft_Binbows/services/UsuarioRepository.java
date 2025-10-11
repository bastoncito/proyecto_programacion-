package Michaelsoft_Binbows.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
    
    boolean existsByNombreUsuario(String nombreUsuario);
    
    boolean existsByCorreoElectronico(String correoElectronico);
    
    List<Usuario> findByRol(Rol rol);
}
