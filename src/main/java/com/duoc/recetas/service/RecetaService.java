package com.duoc.recetas.service;

import com.duoc.recetas.entity.*;
import com.duoc.recetas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import java.util.stream.Stream;




@Service
@Transactional
public class RecetaService {
    private final RecetaRepository recetaRepository;
    private final ImagenRepository imagenRepository;
    private final ComentarioRepository comentarioRepository;
    private final UsuarioService usuarioService;

    public RecetaService(RecetaRepository recetaRepository, 
                        ImagenRepository imagenRepository,
                        ComentarioRepository comentarioRepository,
                        UsuarioService usuarioService) {
        this.recetaRepository = recetaRepository;
        this.imagenRepository = imagenRepository;
        this.comentarioRepository = comentarioRepository;
        this.usuarioService = usuarioService;
    }
    public void eliminarReceta(Long id) {
        // Primero eliminamos las imágenes y comentarios relacionados
        imagenRepository.deleteByRecetaId(id);
        comentarioRepository.deleteByRecetaId(id);
        // Finalmente eliminamos la receta
        recetaRepository.deleteById(id);
    }

    public List<Receta> findAll() {
        return recetaRepository.findAll();
    }

    public List<Receta> obtenerRecetasRecientes() {
        return recetaRepository.findTop10ByOrderByFechaCreacionDesc();
    }

    public List<Receta> obtenerRecetasMejorValoradas() {
        return recetaRepository.findTopRecetasByMinValoracion(4.0);
    }

    public Receta obtenerRecetaPorId(Long id) {
        return recetaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
    }

    public List<Receta> buscarRecetas(String nombre, String tipoCocina, String paisOrigen, String dificultad) {
        return recetaRepository.buscarRecetas(
            nombre != null ? nombre.trim() : null,
            tipoCocina != null ? tipoCocina.trim() : null,
            paisOrigen != null ? paisOrigen.trim() : null,
            dificultad != null ? dificultad.trim() : null
        );
    }

    public List<Receta> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerRecetasRecientes();
        }
        return recetaRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    public Receta guardarReceta(Receta receta) {
        return recetaRepository.save(receta);
    }

    public List<Receta> obtenerRecetasPorDificultad(String dificultad) {
        return recetaRepository.findByDificultad(dificultad);
    }

    // Métodos para imágenes
    public List<Imagen> obtenerImagenesReceta(Long recetaId) {
        return imagenRepository.findByRecetaId(recetaId);
    }

    public List<Imagen> obtenerFotosReceta(Long recetaId) {
        return imagenRepository.findByRecetaIdAndTipo(recetaId, "FOTO");
    }

    public List<Imagen> obtenerVideosReceta(Long recetaId) {
        return imagenRepository.findByRecetaIdAndTipo(recetaId, "VIDEO");
    }

    public Imagen guardarImagen(Imagen imagen) {
        return imagenRepository.save(imagen);
    }

    // Métodos para comentarios
    public void agregarComentario(Comentario comentario) {
        if (comentario.getReceta() == null) {
            throw new RuntimeException("La receta es requerida");
        }
        if (comentario.getUsuario() == null) {
            throw new RuntimeException("El usuario es requerido");
        }
        
        comentario.setFecha(LocalDateTime.now());
        Comentario comentarioGuardado = comentarioRepository.save(comentario);
        
        // Actualizar valoración promedio
        actualizarValoracionPromedio(comentario.getReceta());
    }

    public List<Comentario> obtenerComentariosReceta(Long recetaId) {
        return comentarioRepository.findByRecetaIdOrderByFechaDesc(recetaId);
    }

    private void actualizarValoracionPromedio(Receta receta) {
        List<Comentario> comentarios = obtenerComentariosReceta(receta.getId());
        if (!comentarios.isEmpty()) {
            double promedio = comentarios.stream()
                .mapToInt(Comentario::getValoracion)
                .average()
                .orElse(0.0);
            receta.setValoracionPromedio(promedio);
            recetaRepository.save(receta);
        }
    }

    public List<Receta> buscarRecetasAvanzado(String nombre, String dificultad, Double valoracionMinima) {
    List<Receta> recetas = recetaRepository.findAll();
    
    Stream<Receta> stream = recetas.stream();
    
    if (nombre != null && !nombre.trim().isEmpty()) {
        stream = stream.filter(r -> r.getNombre().toLowerCase()
                                  .contains(nombre.toLowerCase()));
    }
    
    if (dificultad != null && !dificultad.isEmpty()) {
        stream = stream.filter(r -> r.getDificultad().equals(dificultad));
    }
    
    if (valoracionMinima != null) {
        stream = stream.filter(r -> r.getValoracionPromedio() != null && 
                                  r.getValoracionPromedio() >= valoracionMinima);
    }
    
    return stream.collect(Collectors.toList());
}
    
    public List<Receta> obtenerTodasRecetas() {
    return recetaRepository.findAll();
}

    public Imagen obtenerPrimeraImagenReceta(Long recetaId) {
        List<Imagen> imagenes = imagenRepository.findByRecetaId(recetaId);
        System.out.println("Imágenes obtenidas: " + imagenes);

        return imagenes.isEmpty() ? null : imagenes.get(0); // Devuelve la primera imagen o null si no hay imágenes
    }
}