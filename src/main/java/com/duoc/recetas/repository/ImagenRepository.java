package com.duoc.recetas.repository;


import com.duoc.recetas.entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    List<Imagen> findByRecetaId(Long recetaId);
    List<Imagen> findByRecetaIdAndTipo(Long recetaId, String tipo);
    void deleteByRecetaId(Long recetaId);
}


    
