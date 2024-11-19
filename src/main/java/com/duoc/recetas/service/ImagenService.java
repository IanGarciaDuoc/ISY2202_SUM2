package com.duoc.recetas.service;

import com.duoc.recetas.entity.Imagen;
import com.duoc.recetas.entity.Receta;
import com.duoc.recetas.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class ImagenService {
    private static final Logger logger = LoggerFactory.getLogger(ImagenService.class);
    
    private final Path rootLocation;
    private final ImagenRepository imagenRepository;
    private final RecetaService recetaService;

    public ImagenService(ImagenRepository imagenRepository, 
                        RecetaService recetaService,
                        @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.imagenRepository = imagenRepository;
        this.recetaService = recetaService;
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        inicializarDirectorios();
    }

    private void inicializarDirectorios() {
        try {
            logger.info("Creando directorios en: {}", rootLocation);
            
            // Crear directorio raíz si no existe
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }
            
            // Crear subdirectorios
            Path fotoDir = rootLocation.resolve("foto");
            Path videosDir = rootLocation.resolve("videos");
            
            if (!Files.exists(fotoDir)) {
                Files.createDirectories(fotoDir);
            }
            if (!Files.exists(videosDir)) {
                Files.createDirectories(videosDir);
            }
            
            logger.info("Directorios creados correctamente");
        } catch (IOException e) {
            logger.error("Error al crear directorios: {}", e.getMessage());
            throw new RuntimeException("No se pudieron crear los directorios necesarios", e);
        }
    }

    public Imagen guardarImagen(MultipartFile file, Long recetaId, String tipo) throws IOException {
        logger.info("Guardando imagen para receta ID: {}, tipo: {}", recetaId, tipo);
        
        Receta receta = recetaService.obtenerRecetaPorId(recetaId);
        if (receta == null) {
            throw new RuntimeException("Receta no encontrada");
        }

        String filename = UUID.randomUUID().toString() + "_" + 
                         StringUtils.cleanPath(file.getOriginalFilename());
        String subDir = tipo.toLowerCase();
        Path targetLocation = rootLocation.resolve(subDir).resolve(filename);
        
        logger.info("Guardando archivo en: {}", targetLocation);
        
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Verificar que el archivo se guardó correctamente
        if (!Files.exists(targetLocation)) {
            throw new RuntimeException("Error al guardar el archivo");
        }

        Imagen imagen = new Imagen();
        imagen.setReceta(receta);
        imagen.setTipo(tipo);
        imagen.setUrl("/uploads/" + subDir + "/" + filename);
        
        logger.info("URL de la imagen guardada: {}", imagen.getUrl());
        
        return imagenRepository.save(imagen);
    }

    public List<Imagen> obtenerImagenesReceta(Long recetaId) {
        logger.info("Buscando imágenes para receta ID: {}", recetaId);
        List<Imagen> imagenes = imagenRepository.findByRecetaId(recetaId);
        logger.info("Encontradas {} imágenes", imagenes.size());
        
        // Verificar existencia física de cada imagen
        for (Imagen imagen : imagenes) {
            Path imagePath = rootLocation.resolve(imagen.getUrl().substring("/uploads/".length()));
            logger.info("Verificando existencia de archivo: {}", imagePath);
            logger.info("¿Archivo existe? {}", Files.exists(imagePath));
        }
        
        return imagenes;
    }

    public void eliminarImagen(Long id) throws IOException {
        Imagen imagen = imagenRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        
        String relativePath = imagen.getUrl().substring("/uploads/".length());
        Path filePath = rootLocation.resolve(relativePath);
        
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        imagenRepository.deleteById(id);
    }
}