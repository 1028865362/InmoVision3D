package com.InmoVision3D.service;

import com.InmoVision3D.model.ImagenInmueble;

import java.util.List;

public interface ImagenInmuebleService {

    ImagenInmueble agregar(Long inmuebleId, ImagenInmueble imagen);

    List<ImagenInmueble> listarPorInmueble(Long inmuebleId);

    ImagenInmueble actualizar(Long id, ImagenInmueble datos);

    void eliminar(Long id);
}
