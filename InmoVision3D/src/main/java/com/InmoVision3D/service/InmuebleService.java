package com.InmoVision3D.service;

import com.InmoVision3D.model.Inmueble;
import com.InmoVision3D.model.enums.EstadoInmueble;
import com.InmoVision3D.model.enums.TipoInmueble;

import java.math.BigDecimal;
import java.util.List;

public interface InmuebleService {

    Inmueble crear(Inmueble inmueble, Long propietarioId);

    Inmueble obtenerPorId(Long id);

    List<Inmueble> listarTodos();

    List<Inmueble> listarPorEstado(EstadoInmueble estado);

    List<Inmueble> listarPorTipo(TipoInmueble tipo);

    List<Inmueble> listarPorCiudad(String ciudad);

    List<Inmueble> listarPorPropietario(Long propietarioId);

    List<Inmueble> listarPorRangoPrecio(BigDecimal min, BigDecimal max);

    Inmueble actualizar(Long id, Inmueble datos);

    void eliminar(Long id);
}
