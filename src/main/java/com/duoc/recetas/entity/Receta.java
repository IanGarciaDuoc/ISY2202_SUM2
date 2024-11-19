package com.duoc.recetas.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "RECETAS")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @ElementCollection
    @CollectionTable(name = "RECETA_INGREDIENTES", 
                    joinColumns = @JoinColumn(name = "receta_id"))
    @Column(name = "ingrediente")
    private List<String> ingredientes;

    @Column(name = "tiempo_coccion")
    private Integer tiempoCoccion;

    @Column(nullable = false)
    private String dificultad;

    @Column(name = "tipo_cocina")
    private String tipoCocina;

    @Column(name = "pais_origen")
    private String paisOrigen;

    @Column(name = "valoracion_promedio")
    private Double valoracionPromedio;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}