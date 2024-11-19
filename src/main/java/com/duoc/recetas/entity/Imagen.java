package com.duoc.recetas.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "imagenes")
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Receta receta;
    
    private String url;
    private String tipo; // FOTO o VIDEO
}