package com.InmoVision3D.service;

import com.InmoVision3D.model.Plano2D;

import java.util.List;

public interface Plano2DService {

    Plano2D agregar(Long inmuebleId, Plano2D plano);

    List<Plano2D> listarPorInmueble(Long inmuebleId);

    Plano2D actualizar(Long id, Plano2D datos);

    void eliminar(Long id);
}
