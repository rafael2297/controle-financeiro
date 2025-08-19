package br.com.controle_despesas.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.model.Despesa;
import br.com.controle_despesas.model.Receita;
import br.com.controle_despesas.model.Relatorio;
import br.com.controle_despesas.repository.CategoriaRepository;
import br.com.controle_despesas.repository.DespesaRepository;
import br.com.controle_despesas.repository.ReceitaRepository;
import br.com.controle_despesas.util.RelatorioExcelExporter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ReceitaRepository receitaRepository;
    private final DespesaRepository despesaRepository;
    private final CategoriaRepository categoriaRepository;

    /**
     * Monta a lista de relatórios (Receitas + Despesas)
     */
    public List<Relatorio> gerarRelatorio() {
        List<Relatorio> relatorios = new ArrayList<>();

        // RECEITAS
        List<Receita> receitas = receitaRepository.findAll();
        for (Receita r : receitas) {
            String nomeCategoria = categoriaRepository.findById(r.getIdCategoria())
                    .map(c -> c.getNome())
                    .orElse("");

            relatorios.add(new Relatorio(
                    r.getDescricao(),
                    r.getValor(),
                    r.getDataReceita(),
                    "Receita",
                    nomeCategoria));
        }

        // DESPESAS
        List<Despesa> despesas = despesaRepository.findAll();
        for (Despesa d : despesas) {
            String nomeCategoria = categoriaRepository.findById(d.getIdCategoria())
                    .map(c -> c.getNome())
                    .orElse("");

            relatorios.add(new Relatorio(
                    d.getDescricao(),
                    d.getValor().negate(), // transforma em negativo
                    d.getDataDespesa(),
                    "Despesa",
                    nomeCategoria));
        }

        return relatorios;
    }

    /**
     * Exporta o relatório em Excel
     */
    public ByteArrayInputStream gerarExcel() {
        List<Relatorio> relatorios = gerarRelatorio();
        return RelatorioExcelExporter.exportarParaExcel(relatorios);
    }
}
