package com.duoc.recetas.controller;

import com.duoc.recetas.entity.Imagen;
import com.duoc.recetas.service.ImagenService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/imagenes")
public class ImagenController {
    private static final Logger logger = LoggerFactory.getLogger(ImagenController.class);
    private final ImagenService imagenService;
    private final Path rootLocation;

    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
        this.rootLocation = Paths.get("uploads").toAbsolutePath().normalize();
    }

    @GetMapping("/ver/{recetaId}")
    public ResponseEntity<Resource> servirImagen(@PathVariable Long recetaId) {
        try {
            logger.info("Intentando servir imagen para receta ID: {}", recetaId);
            
            Imagen imagen = imagenService.obtenerImagenesReceta(recetaId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));

            logger.info("Imagen encontrada con URL: {}", imagen.getUrl());
            
            // Construir la ruta completa al archivo
            String relativePath = imagen.getUrl().substring("/uploads/".length());
            Path filePath = rootLocation.resolve(relativePath).normalize();
            
            logger.info("Ruta del archivo: {}", filePath);
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                logger.error("Archivo no encontrado en: {}", filePath);
                throw new RuntimeException("Archivo no encontrado");
            }
            
            logger.info("Archivo encontrado y accesible");
            
            String contentType = "image/jpeg";
            if (imagen.getUrl().toLowerCase().endsWith(".png")) {
                contentType = "image/png";
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
                
        } catch (Exception e) {
            logger.error("Error al servir la imagen", e);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> subirArchivo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("recetaId") Long recetaId,
            @RequestParam("tipo") String tipo) {
        try {
            logger.info("Subiendo archivo para receta ID: {}", recetaId);
            Imagen imagen = imagenService.guardarImagen(file, recetaId, tipo);
            logger.info("Archivo subido exitosamente. URL: {}", imagen.getUrl());
            return ResponseEntity.ok(imagen);
        } catch (IOException e) {
            logger.error("Error al subir archivo", e);
            return ResponseEntity.badRequest().body("Error al subir el archivo: " + e.getMessage());
        }
    }
}