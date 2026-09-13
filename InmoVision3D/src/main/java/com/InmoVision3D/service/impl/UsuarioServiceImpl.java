package com.InmoVision3D.service.impl;

import com.InmoVision3D.exception.BusinessException;
import com.InmoVision3D.exception.ResourceNotFoundException;
import com.InmoVision3D.model.Usuario;
import com.InmoVision3D.repository.UsuarioRepository;
import com.InmoVision3D.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("Ya existe un usuario registrado con ese email");
        }
        usuario.setId(null);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email " + email + " no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = obtenerPorId(id);

        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setTelefono(datos.getTelefono());

        if (datos.getEmail() != null && !datos.getEmail().equalsIgnoreCase(existente.getEmail())) {
            if (usuarioRepository.existsByEmail(datos.getEmail())) {
                throw new BusinessException("Ya existe un usuario registrado con ese email");
            }
            existente.setEmail(datos.getEmail());
        }

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        if (datos.getRol() != null) {
            existente.setRol(datos.getRol());
        }

        return usuarioRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Usuario existente = obtenerPorId(id);
        usuarioRepository.delete(existente);
    }

    @Override
    public boolean cambiarPassword(Long id, String actual, String nueva) {
        Usuario existente = obtenerPorId(id);
        if (!passwordEncoder.matches(actual, existente.getPassword())) {
            return false;
        }
        existente.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(existente);
        return true;
    }

    @Override
    public void desactivar(Long id) {
        Usuario existente = obtenerPorId(id);
        existente.setActivo(false);
        usuarioRepository.save(existente);
    }
}
