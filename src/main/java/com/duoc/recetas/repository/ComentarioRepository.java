package com.duoc.recetas.repository;


import com.duoc.recetas.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByRecetaId(Long recetaId);
    List<Comentario> findByUsuarioId(Long usuarioId);
    
    // Obtener comentarios ordenados por fecha para una receta
    List<Comentario> findByRecetaIdOrderByFechaDesc(Long recetaId);
    
    // Contar comentarios por receta
    long countByRecetaId(Long recetaId);

     // Método para eliminar comentarios por recetaId
    @Modifying
    @Transactional
    @Query("DELETE FROM Comentario c WHERE c.receta.id = :recetaId")
    void deleteByRecetaId(@Param("recetaId") Long recetaId);
}