package com.InmoVision3D.service;

import com.InmoVision3D.model.Solicitud;
import com.InmoVision3D.model.enums.EstadoSolicitud;

import java.time.LocalDateTime;
import java.util.List;

public interface SolicitudService {

    Solicitud crear(Long usuarioId, Long inmuebleId, String mensaje, LocalDateTime fechaCita);

    Solicitud obtenerPorId(Long id);

    List<Solicitud> listarPorUsuario(Long usuarioId);

    List<Solicitud> listarPorInmueble(Long inmuebleId);

    List<Solicitud> listarPorPropietario(Long propietarioId);

    List<Solicitud> listarTodas();

    Solicitud cambiarEstado(Long id, EstadoSolicitud nuevoEstado);

    void eliminar(Long id);
}
