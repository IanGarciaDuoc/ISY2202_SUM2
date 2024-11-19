package com.duoc.recetas.repository;

import com.duoc.recetas.entity.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {
    // Búsquedas básicas
    List<Receta> findByNombreContainingIgnoreCase(String nombre);
    List<Receta> findByTipoCocinaIgnoreCase(String tipoCocina);
    List<Receta> findByPaisOrigenIgnoreCase(String paisOrigen);
    List<Receta> findByDificultadIgnoreCase(String dificultad);
    
    // Obtener recetas recientes
    List<Receta> findTop10ByOrderByFechaCreacionDesc();
    
    // Búsqueda por múltiples criterios
    @Query("SELECT r FROM Receta r " +
           "WHERE (:nombre IS NULL OR LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:tipoCocina IS NULL OR LOWER(r.tipoCocina) = LOWER(:tipoCocina)) " +
           "AND (:paisOrigen IS NULL OR LOWER(r.paisOrigen) = LOWER(:paisOrigen)) " +
           "AND (:dificultad IS NULL OR LOWER(r.dificultad) = LOWER(:dificultad)) " +
           "ORDER BY r.fechaCreacion DESC")
    List<Receta> buscarRecetas(
        @Param("nombre") String nombre,
        @Param("tipoCocina") String tipoCocina,
        @Param("paisOrigen") String paisOrigen,
        @Param("dificultad") String dificultad
    );
    
    // Recetas mejor valoradas
    @Query("SELECT r FROM Receta r WHERE r.valoracionPromedio >= :minValoracion " +
           "ORDER BY r.valoracionPromedio DESC, r.fechaCreacion DESC")
    List<Receta> findTopRecetasByMinValoracion(@Param("minValoracion") Double minValoracion);
    
        // Método personalizado para buscar recetas por dificultad
        List<Receta> findByDificultad(String dificultad);
    }

    
