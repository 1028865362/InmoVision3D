package com.InmoVision3D.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Expone la carpeta de subidas reales (imágenes de inmuebles, planos 2D
 * subidos por publicadores) como recursos estáticos públicos bajo /media/**.
 *
 * OJO: NO se llama /uploads/** a propósito. Ese prefijo ya lo usa Spring Boot
 * por defecto para servir todo lo que esté en src/main/resources/static/uploads/
 * (ahí viven las fotos de ejemplo del proyecto). Si esta carpeta externa
 * también se sirviera bajo /uploads/**, taparía esas imágenes empaquetadas
 * y dejarían de verse.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + java.nio.file.Paths.get(uploadDir).toAbsolutePath().normalize() + "/";
        registry.addResourceHandler("/media/**").addResourceLocations(location);
    }
}
