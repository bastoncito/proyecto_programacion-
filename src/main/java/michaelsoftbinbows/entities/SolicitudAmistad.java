package michaelsoftbinbows.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "solicitudes_amistad",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"solicitante_id", "receptor_id"})})
public class SolicitudAmistad {

  // DEFINIMOS EL ENUM AQUÍ DENTRO
  // Al ser estático y público, se usa como: SolicitudAmistad.Estado.PENDIENTE
  public enum Estado {
    PENDIENTE,
    ACEPTADA,
    RECHAZADA
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "solicitante_id", nullable = false)
  private Usuario solicitante;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "receptor_id", nullable = false)
  private Usuario receptor;

  // Usamos el Enum interno
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Estado estado;

  private LocalDateTime fechaSolicitud;

  private LocalDateTime fechaRespuesta;

  public SolicitudAmistad() {}

  public SolicitudAmistad(Usuario solicitante, Usuario receptor) {
    this.solicitante = solicitante;
    this.receptor = receptor;
    // Asignamos el estado inicial usando el Enum interno
    this.estado = Estado.PENDIENTE;
    this.fechaSolicitud = LocalDateTime.now();
  }

  // Getters y Setters

  public Long getId() {
    return id;
  }

  public Usuario getSolicitante() {
    return solicitante;
  }

  public void setSolicitante(Usuario solicitante) {
    this.solicitante = solicitante;
  }

  public Usuario getReceptor() {
    return receptor;
  }

  public void setReceptor(Usuario receptor) {
    this.receptor = receptor;
  }

  public Estado getEstado() {
    return estado;
  }

  public void setEstado(Estado estado) {
    this.estado = estado;
    // Lógica automática: si el estado cambia a final, guardamos la fecha
    if (estado == Estado.ACEPTADA || estado == Estado.RECHAZADA) {
      this.fechaRespuesta = LocalDateTime.now();
    }
  }

  public LocalDateTime getFechaSolicitud() {
    return fechaSolicitud;
  }

  public LocalDateTime getFechaRespuesta() {
    return fechaRespuesta;
  }
}
