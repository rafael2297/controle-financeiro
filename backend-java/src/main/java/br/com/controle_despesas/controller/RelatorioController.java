package br.com.controle_despesas.controller;

import br.com.controle_despesas.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/relatorio/excel")
    public String gerarRelatorioExcel() {
        relatorioService.gerarExcel(); // Nome do método correto
        return "Relatório gerado com sucesso!";
    }
}
