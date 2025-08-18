package br.com.controle_despesas.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.model.Relatorio;
import br.com.controle_despesas.repository.DespesaRepository;
import br.com.controle_despesas.util.RelatorioExcelExporter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final DespesaRepository despesaRepository;

    public void gerarExcel() {
        List<Object[]> resultados = despesaRepository.gerarRelatorioBruto();
        List<Relatorio> relatorios = new ArrayList<>();

        for (Object[] linha : resultados) {
            Relatorio r = new Relatorio();
            r.setId(((Number) linha[0]).intValue());
            r.setValor((BigDecimal) linha[1]);
            r.setData((Date) linha[2]);
            r.setDescricao((String) linha[3]);
            r.setIdCategoria(((Number) linha[4]).intValue());
            r.setNomeCategoria((String) linha[5]);
            r.setPagamento((String) linha[6]);
            relatorios.add(r);
        }

        String nomeArquivo = "relatorio_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) +
                ".xlsx";

        // Gera o arquivo no diretório atual
        RelatorioExcelExporter.exportarParaExcel(relatorios, nomeArquivo);
    }
}
