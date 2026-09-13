package com.InmoVision3D.service;

import com.InmoVision3D.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Guarda archivos subidos (imágenes de inmuebles, planos 2D) en disco y
 * devuelve la URL pública bajo la que quedan disponibles (servida por
 * WebConfig en /uploads/**).
 *
 * Reemplaza la lógica de move_uploaded_file() hacia assets/uploads/ que
 * tenía la app PHP original — esa parte se había perdido por completo en
 * la migración inicial (el backend Spring solo aceptaba una URL de texto).
 */
@Service
public class FileStorageService {

    private static final List<String> EXTENSIONES_IMAGEN =
            List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");
    private static final List<String> EXTENSIONES_PLANO =
            List.of(".jpg", ".jpeg", ".png", ".webp", ".pdf", ".dwg");

    private final Path raiz;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.raiz = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(raiz.resolve("inmuebles"));
            Files.createDirectories(raiz.resolve("planos"));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de subidas: " + raiz, e);
        }
    }

    public String guardarImagen(MultipartFile file) {
        return guardar(file, "inmuebles", EXTENSIONES_IMAGEN);
    }

    public String guardarPlano(MultipartFile file) {
        return guardar(file, "planos", EXTENSIONES_PLANO);
    }

    private String guardar(MultipartFile file, String subcarpeta, List<String> extensionesPermitidas) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("El archivo está vacío");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String extension = obtenerExtension(original);

        if (!extensionesPermitidas.contains(extension.toLowerCase())) {
            throw new BusinessException("Tipo de archivo no permitido: " + extension);
        }

        String nombreUnico = UUID.randomUUID() + extension.toLowerCase();
        Path destino = raiz.resolve(subcarpeta).resolve(nombreUnico).normalize();

        if (!destino.getParent().equals(raiz.resolve(subcarpeta))) {
            throw new BusinessException("Ruta de archivo inválida");
        }

        try (var in = file.getInputStream()) {
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("No se pudo guardar el archivo: " + e.getMessage());
        }

        return "/media/" + subcarpeta + "/" + nombreUnico;
    }

    private String obtenerExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i >= 0 ? filename.substring(i) : "";
    }
}
