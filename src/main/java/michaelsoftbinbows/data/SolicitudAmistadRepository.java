package michaelsoftbinbows.data;

import java.util.List;
import java.util.Optional;
import michaelsoftbinbows.entities.SolicitudAmistad;
import michaelsoftbinbows.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Long> {

  // 1. Buscar solicitudes recibidas que estén PENDIENTES
  List<SolicitudAmistad> findByReceptorAndEstado(
      Usuario receptor, SolicitudAmistad.Estado estado);

  // 2. Buscar si existe CUALQUIER registro entre dos personas
  @Query(
      "SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM SolicitudAmistad s "
          + "WHERE (s.solicitante = :u1 AND s.receptor = :u2) "
          + "OR (s.solicitante = :u2 AND s.receptor = :u1)")
  boolean existeSolicitudEntre(@Param("u1") Usuario u1, @Param("u2") Usuario u2);

  // 3. Buscar la solicitud específica entre dos usuarios
  @Query(
      "SELECT s FROM SolicitudAmistad s "
          + "WHERE (s.solicitante = :u1 AND s.receptor = :u2) "
          + "OR (s.solicitante = :u2 AND s.receptor = :u1)")
  Optional<SolicitudAmistad> findSolicitudEntre(@Param("u1") Usuario u1, @Param("u2") Usuario u2);

  // 4. Buscar amistades activas (ACEPTADA)
  @Query(
      "SELECT s FROM SolicitudAmistad s "
          + "WHERE (s.solicitante = :usuario OR s.receptor = :usuario) "
          + "AND s.estado = 'ACEPTADA'")
  List<SolicitudAmistad> findAmistadesDeUsuario(@Param("usuario") Usuario usuario);
}