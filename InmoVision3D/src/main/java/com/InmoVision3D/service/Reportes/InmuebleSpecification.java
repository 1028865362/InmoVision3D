package com.InmoVision3D.service.Reportes;

import com.InmoVision3D.dto.FiltroReporteDTO;
import com.InmoVision3D.model.Inmueble;
import com.InmoVision3D.model.enums.EstadoInmueble;
import com.InmoVision3D.model.enums.TipoInmueble;
import com.InmoVision3D.model.enums.TipoOperacion;
import org.springframework.data.jpa.domain.Specification;

public class InmuebleSpecification {

    public static Specification<Inmueble> conFiltros(FiltroReporteDTO filtro) {

      Specification<Inmueble> spec = (root, query, cb) -> cb.conjunction();

        if (filtro == null) {
            return spec;
        }

        if (filtro.getEstado() != null && !filtro.getEstado().isBlank()) {
            EstadoInmueble estado = EstadoInmueble.valueOf(filtro.getEstado().toUpperCase());
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("estado"), estado));
        }

        if (filtro.getOperacion() != null && !filtro.getOperacion().isBlank()) {
            TipoOperacion operacion = TipoOperacion.valueOf(filtro.getOperacion().toUpperCase());
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("operacion"), operacion));
        }

        if (filtro.getTipo() != null && !filtro.getTipo().isBlank()) {
            TipoInmueble tipo = TipoInmueble.valueOf(filtro.getTipo().toUpperCase());
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("tipo"), tipo));
        }

        if (filtro.getIdPublicador() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("propietario").get("id"), filtro.getIdPublicador()));
        }

        if (filtro.getBusqueda() != null && !filtro.getBusqueda().isBlank()) {

            String busqueda = "%" + filtro.getBusqueda().toLowerCase() + "%";

            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("titulo")), busqueda),
                            cb.like(cb.lower(root.get("direccion")), busqueda)
                    )
            );
        }

        return spec;
    }
}
