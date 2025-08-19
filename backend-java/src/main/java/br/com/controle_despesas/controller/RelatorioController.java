package br.com.controle_despesas.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.controle_despesas.model.Relatorio;
import br.com.controle_despesas.service.RelatorioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    /**
     * Retorna a lista de relatórios em JSON
     */
    @GetMapping("/api/relatorios")
    public List<Relatorio> listarRelatorios() {
        return relatorioService.gerarRelatorio();
    }

    /**
     * Gera e baixa o relatório em Excel
     */
    @GetMapping("/api/relatorios/excel")
    public ResponseEntity<InputStreamResource> exportarExcel() {
        ByteArrayInputStream in = relatorioService.gerarExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=relatorio.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
}
