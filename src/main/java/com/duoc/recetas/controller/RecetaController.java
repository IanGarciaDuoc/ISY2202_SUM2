package com.duoc.recetas.controller;

import com.duoc.recetas.entity.*;
import com.duoc.recetas.service.RecetaService;
import com.duoc.recetas.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/recetas")
public class RecetaController {

    private final RecetaService recetaService;
    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(RecetaController.class);


    @Autowired
    public RecetaController(RecetaService recetaService, UsuarioService usuarioService) {
        this.recetaService = recetaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("")
    public String listarRecetas(Model model) {
        model.addAttribute("recetasRecientes", recetaService.obtenerRecetasRecientes());
        model.addAttribute("recetasDestacadas", recetaService.obtenerRecetasMejorValoradas());
        return "recetas/lista";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalleReceta(@PathVariable Long id, Model model) {
        logger.info("Accediendo a detalle de receta con ID: {}", id);
        
        Receta receta = recetaService.obtenerRecetaPorId(id);
        if (receta == null) {
            logger.error("No se encontró la receta con ID: {}", id);
            return "redirect:/?error=receta-no-encontrada";
        }

        // Obtener imágenes y comentarios
        List<Imagen> imagenes = recetaService.obtenerImagenesReceta(id);
        List<Comentario> comentarios = recetaService.obtenerComentariosReceta(id);

        logger.info("Número de imágenes encontradas: {}", imagenes.size());
        if (!imagenes.isEmpty()) {
            Imagen primeraImagen = imagenes.get(0);
            logger.info("URL de la primera imagen: {}", primeraImagen.getUrl());
            logger.info("Tipo de la primera imagen: {}", primeraImagen.getTipo());
        }

        model.addAttribute("receta", receta);
        model.addAttribute("imagen", !imagenes.isEmpty() ? imagenes.get(0) : null);
        model.addAttribute("comentarios", comentarios);
        model.addAttribute("nuevoComentario", new Comentario());

        return "detalle";
    }
    

    @GetMapping("/buscar")
    public String buscarRecetas(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String tipoCocina,
            @RequestParam(required = false) String dificultad,
            Model model) {

        model.addAttribute("tiposCocina", new String[]{"Italiana", "Japonesa", "Chilena", "Mexicana", "Española"});
        model.addAttribute("dificultades", new String[]{"Fácil", "Media", "Difícil"});

        List<Receta> resultados = recetaService.buscarRecetas(query, tipoCocina, null, dificultad);
        model.addAttribute("recetas", resultados);
        model.addAttribute("terminoBusqueda", query);
        return "recetas/buscar";
    }
    @PostMapping("/detalle/{id}/comentar")
    @PreAuthorize("isAuthenticated()")
    public String agregarComentario(
            @PathVariable Long id,
            @RequestParam("valoracion") Integer valoracion,
            @RequestParam("contenido") String contenido,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Usuario usuario = usuarioService.obtenerUsuarioPorUsername(userDetails.getUsername());
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Receta receta = recetaService.obtenerRecetaPorId(id);
        
        Comentario comentario = new Comentario();
        comentario.setContenido(contenido);
        comentario.setValoracion(valoracion);
        comentario.setUsuario(usuario);
        comentario.setReceta(receta);
        comentario.setFecha(LocalDateTime.now());
        
        recetaService.agregarComentario(comentario);

        return "redirect:/recetas/detalle/" + id;
    }

    @GetMapping("/api/buscar")
    @ResponseBody
    public ResponseEntity<List<Receta>> buscarRecetasApi(
            @RequestParam(required = false) String nombre) {
        try {
            List<Receta> recetas;
            if (nombre == null || nombre.trim().isEmpty()) {
                recetas = recetaService.obtenerRecetasRecientes();
            } else {
                recetas = recetaService.buscarPorNombre(nombre);
            }
            return ResponseEntity.ok(recetas);
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/recetas/api/filtrar")
@ResponseBody
public List<Receta> filtrarRecetas(@RequestParam String categoria) {
    if ("todos".equalsIgnoreCase(categoria)) {
        return recetaService.obtenerTodasRecetas();
    }
    return recetaService.obtenerRecetasPorDificultad(categoria);
}
}
