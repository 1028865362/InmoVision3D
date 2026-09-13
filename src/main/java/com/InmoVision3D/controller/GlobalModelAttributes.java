package com.InmoVision3D.controller;

import com.InmoVision3D.model.Usuario;
import com.InmoVision3D.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Reemplaza a las funciones de config.php basadas en $_SESSION
 * (isLoggedIn(), $_SESSION['nombre'], etc.). Inyecta el usuario
 * autenticado (o null) en el Model de TODAS las vistas Thymeleaf,
 * para que header/nav puedan mostrar el nombre real sin repetir
 * esta consulta en cada controller.
 *
 * Los chequeos de rol (isPublicador()/isAdmin() en PHP) se hacen
 * en las plantillas con sec:authorize="hasRole('PUBLICADOR')" /
 * sec:authorize="hasRole('ADMIN')" gracias a thymeleaf-extras-springsecurity6.
 */
@ControllerAdvice(annotations = Controller.class)
public class GlobalModelAttributes {

    private final UsuarioRepository usuarioRepository;

    public GlobalModelAttributes(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return usuarioRepository.findByEmail(authentication.getName()).orElse(null);
    }
}
