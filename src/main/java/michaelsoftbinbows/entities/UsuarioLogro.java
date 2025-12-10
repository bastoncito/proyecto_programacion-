package michaelsoftbinbows.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Entidad que representa la relación entre un usuario y un logro completado. */
@Entity
public class UsuarioLogro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "logro_id")
  private Logro logro;

  @Column(name = "fecha_completada")
  private LocalDateTime fechaCompletada;

  /** Constructor vacío para JPA. */
  public UsuarioLogro() {}

  /**
   * Constructor que crea una nueva asociación usuario-logro.
   *
   * @param usuario El usuario que completa el logro.
   * @param logro El logro completado.
   */
  public UsuarioLogro(Usuario usuario, Logro logro) {
    this.usuario = usuario;
    this.logro = logro;
    this.fechaCompletada =
        LocalDateTime.now(ZoneId.systemDefault()); // ¡Se guarda la fecha al crearlo!
  }

  /**
   * Obtiene el ID de la asociación.
   *
   * @return El ID.
   */
  public Long getId() {
    return id;
  }

  /**
   * Establece el ID de la asociación.
   *
   * @param id El ID.
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Obtiene el usuario propietario del logro.
   *
   * @return El usuario.
   */
  public Usuario getUsuario() {
    return usuario;
  }

  /**
   * Establece el usuario propietario del logro.
   *
   * @param usuario El usuario.
   */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public Logro getLogro() {
    return logro;
  }

  public void setLogro(Logro logro) {
    this.logro = logro;
  }

  public LocalDateTime getFechaCompletada() {
    return fechaCompletada;
  }

  public void setFechaCompletada(LocalDateTime fecha) {
    this.fechaCompletada = fecha;
  }
}
