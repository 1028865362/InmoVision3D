package com.InmoVision3D.controller;

import com.lowagie.text.DocumentException;
import com.InmoVision3D.dto.EstadisticaTipoDTO;
import com.InmoVision3D.dto.FiltroReporteDTO;
import com.InmoVision3D.dto.ResumenPublicadorDTO;
import com.InmoVision3D.model.Inmueble;
import com.InmoVision3D.service.ReporteService;
import com.InmoVision3D.service.Reportes.ReporteExcelService;
import com.InmoVision3D.service.Reportes.ReportePdfService;
import com.InmoVision3D.service.Reportes.ReporteWordService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Sirve el Centro de reportes del panel admin. Soporta 4 reportes
 * ("tipoReporte"): inventario (listado plano), portipo (agrupado por
 * tipo de inmueble), precios (analisis de precios por tipo) y
 * porpublicador (resumen por publicador). Cada uno exportable en
 * pdf, excel o word.
 */
@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private ReportePdfService pdfService;

    @Autowired
    private ReporteExcelService excelService;

    @Autowired
    private ReporteWordService wordService;

    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("filtro", new FiltroReporteDTO());
        return "admin/reportes";
    }

    @GetMapping("/generar")
    public void generar(@ModelAttribute FiltroReporteDTO filtro,
                         @RequestParam String formato,
                         @RequestParam(defaultValue = "inventario") String tipoReporte,
                         HttpServletResponse response) throws IOException, DocumentException {

        if (!configurarCabecera(response, formato, nombreArchivo(tipoReporte))) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato no válido");
            return;
        }

        OutputStream out = response.getOutputStream();

        switch (tipoReporte) {
            case "portipo" -> {
                List<EstadisticaTipoDTO> stats = reporteService.obtenerEstadisticasPorTipo(filtro);
                switch (formato) {
                    case "pdf" -> pdfService.generarPorTipo(stats, out);
                    case "excel" -> excelService.generarPorTipo(stats, out);
                    case "word" -> wordService.generarPorTipo(stats, out);
                    default -> { /* validado arriba */ }
                }
            }
            case "precios" -> {
                List<EstadisticaTipoDTO> stats = reporteService.obtenerEstadisticasPorTipo(filtro);
                switch (formato) {
                    case "pdf" -> pdfService.generarAnalisisPrecios(stats, out);
                    case "excel" -> excelService.generarAnalisisPrecios(stats, out);
                    case "word" -> wordService.generarAnalisisPrecios(stats, out);
                    default -> { /* validado arriba */ }
                }
            }
            case "porpublicador" -> {
                List<ResumenPublicadorDTO> resumen = reporteService.obtenerResumenPorPublicador(filtro);
                switch (formato) {
                    case "pdf" -> pdfService.generarPorPublicador(resumen, out);
                    case "excel" -> excelService.generarPorPublicador(resumen, out);
                    case "word" -> wordService.generarPorPublicador(resumen, out);
                    default -> { /* validado arriba */ }
                }
            }
            default -> {
                List<Inmueble> inmuebles = reporteService.obtenerFiltrados(filtro);
                switch (formato) {
                    case "pdf" -> pdfService.generar(inmuebles, out);
                    case "excel" -> excelService.generar(inmuebles, out);
                    case "word" -> wordService.generar(inmuebles, out);
                    default -> { /* validado arriba */ }
                }
            }
        }
    }

    private String nombreArchivo(String tipoReporte) {
        String base = switch (tipoReporte) {
            case "portipo" -> "reporte_por_tipo_";
            case "precios" -> "reporte_analisis_precios_";
            case "porpublicador" -> "reporte_por_publicador_";
            default -> "reporte_inventario_";
        };
        return base + System.currentTimeMillis();
    }

    /**
     * Configura Content-Type y Content-Disposition según el formato.
     * Devuelve false si el formato no es válido (pdf|excel|word).
     */
    private boolean configurarCabecera(HttpServletResponse response, String formato, String filename) {
        switch (formato) {
            case "pdf" -> {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".pdf");
            }
            case "excel" -> {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xlsx");
            }
            case "word" -> {
                response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                response.setHeader("Content-Disposition", "attachment; filename=" + filename + ".docx");
            }
            default -> {
                return false;
            }
        }
        return true;
    }
}
