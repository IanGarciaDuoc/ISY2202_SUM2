package com.duoc.recetas.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comentarios")
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "receta_id")
    private Receta receta;
    
    @Column(nullable = false)
    private String contenido;
    
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(nullable = false)
    private Integer valoracion;

}
