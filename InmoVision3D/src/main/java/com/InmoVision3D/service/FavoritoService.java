package com.InmoVision3D.service;

import com.InmoVision3D.model.Favorito;

import java.util.List;

public interface FavoritoService {

    Favorito agregar(Long usuarioId, Long inmuebleId);

    List<Favorito> listarPorUsuario(Long usuarioId);

    boolean existe(Long usuarioId, Long inmuebleId);

    long contarPorInmueble(Long inmuebleId);

    void eliminar(Long usuarioId, Long inmuebleId);
}
