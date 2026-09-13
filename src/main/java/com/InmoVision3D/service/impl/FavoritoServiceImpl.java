package com.InmoVision3D.service.impl;

import com.InmoVision3D.exception.BusinessException;
import com.InmoVision3D.exception.ResourceNotFoundException;
import com.InmoVision3D.model.Favorito;
import com.InmoVision3D.model.Inmueble;
import com.InmoVision3D.model.Usuario;
import com.InmoVision3D.repository.FavoritoRepository;
import com.InmoVision3D.repository.InmuebleRepository;
import com.InmoVision3D.repository.UsuarioRepository;
import com.InmoVision3D.service.FavoritoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InmuebleRepository inmuebleRepository;

    public FavoritoServiceImpl(FavoritoRepository favoritoRepository, UsuarioRepository usuarioRepository,
                                InmuebleRepository inmuebleRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inmuebleRepository = inmuebleRepository;
    }

    @Override
    public Favorito agregar(Long usuarioId, Long inmuebleId) {
        if (favoritoRepository.existsByUsuarioIdAndInmuebleId(usuarioId, inmuebleId)) {
            throw new BusinessException("Este inmueble ya esta en los favoritos del usuario");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new ResourceNotFoundException("Inmueble", inmuebleId));

        Favorito favorito = new Favorito();
        favorito.setUsuario(usuario);
        favorito.setInmueble(inmueble);
        return favoritoRepository.save(favorito);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Favorito> listarPorUsuario(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existe(Long usuarioId, Long inmuebleId) {
        return favoritoRepository.existsByUsuarioIdAndInmuebleId(usuarioId, inmuebleId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorInmueble(Long inmuebleId) {
        return favoritoRepository.countByInmuebleId(inmuebleId);
    }

    @Override
    public void eliminar(Long usuarioId, Long inmuebleId) {
        Favorito favorito = favoritoRepository.findByUsuarioIdAndInmuebleId(usuarioId, inmuebleId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito no encontrado para ese usuario e inmueble"));
        favoritoRepository.delete(favorito);
    }
}
