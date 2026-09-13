package com.InmoVision3D.service.impl;

import com.InmoVision3D.exception.ResourceNotFoundException;
import com.InmoVision3D.model.Inmueble;
import com.InmoVision3D.model.Usuario;
import com.InmoVision3D.model.enums.EstadoInmueble;
import com.InmoVision3D.model.enums.TipoInmueble;
import com.InmoVision3D.repository.InmuebleRepository;
import com.InmoVision3D.repository.UsuarioRepository;
import com.InmoVision3D.service.InmuebleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class InmuebleServiceImpl implements InmuebleService {

    private final InmuebleRepository inmuebleRepository;
    private final UsuarioRepository usuarioRepository;

    public InmuebleServiceImpl(InmuebleRepository inmuebleRepository, UsuarioRepository usuarioRepository) {
        this.inmuebleRepository = inmuebleRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Inmueble crear(Inmueble inmueble, Long propietarioId) {
        Usuario propietario = usuarioRepository.findById(propietarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", propietarioId));
        inmueble.setId(null);
        inmueble.setPropietario(propietario);
        if (inmueble.getEstado() == null) {
            inmueble.setEstado(EstadoInmueble.DISPONIBLE);
        }
        return inmuebleRepository.save(inmueble);
    }

    @Override
    @Transactional(readOnly = true)
    public Inmueble obtenerPorId(Long id) {
        return inmuebleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmueble> listarTodos() {
        return inmuebleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmueble> listarPorEstado(EstadoInmueble estado) {
        return inmuebleRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmueble> listarPorTipo(TipoInmueble tipo) {
        return inmuebleRepository.findByTipo(tipo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmueble> listarPorCiudad(String ciudad) {
        return inmuebleRepository.findByCiudadIgnoreCaseContaining(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmueble> listarPorPropietario(Long propietarioId) {
        return inmuebleRepository.findByPropietarioId(propietarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inmueble> listarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        return inmuebleRepository.findByPrecioBetween(min, max);
    }

    @Override
    public Inmueble actualizar(Long id, Inmueble datos) {
        Inmueble existente = obtenerPorId(id);

        existente.setTitulo(datos.getTitulo());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecio(datos.getPrecio());
        existente.setDireccion(datos.getDireccion());
        existente.setCiudad(datos.getCiudad());
        existente.setTipo(datos.getTipo());
        existente.setOperacion(datos.getOperacion());
        existente.setEstado(datos.getEstado());
        existente.setArea(datos.getArea());
        existente.setHabitaciones(datos.getHabitaciones());
        existente.setBanos(datos.getBanos());

        return inmuebleRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Inmueble existente = obtenerPorId(id);
        inmuebleRepository.delete(existente);
    }
}
