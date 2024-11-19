package com.duoc.recetas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtener ruta absoluta del directorio uploads
        String projectDir = System.getProperty("user.dir");
        String uploadPath = Paths.get(projectDir, "uploads").toString();
        
        // Asegurarse de que termine en separador
        if (!uploadPath.endsWith(System.getProperty("file.separator"))) {
            uploadPath += System.getProperty("file.separator");
        }
        
        // Imprimir la ruta para debug
        System.out.println("Ruta de uploads: " + uploadPath);
        
        registry.addResourceHandler("/uploads/**")
               .addResourceLocations("file:" + uploadPath)
               .setCachePeriod(0);
    }
}