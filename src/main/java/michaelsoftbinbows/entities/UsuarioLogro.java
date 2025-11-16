package michaelsoftbinbows.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import jakarta.persistence.Column;

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

    // Constructores
    public UsuarioLogro() {}

    public UsuarioLogro(Usuario usuario, Logro logro) {
        this.usuario = usuario;
        this.logro = logro;
        this.fechaCompletada = LocalDateTime.now(); // ¡Se guarda la fecha al crearlo!
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Logro getLogro() { return logro; }
    public void setLogro(Logro logro) { this.logro = logro; }
    public LocalDateTime getFechaCompletada() { return fechaCompletada; }
    public void setFechaCompletada(LocalDateTime fecha) { this.fechaCompletada = fecha; }
}