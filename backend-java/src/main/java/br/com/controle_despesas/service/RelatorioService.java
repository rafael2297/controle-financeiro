package br.com.controle_despesas.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.model.Categoria;
import br.com.controle_despesas.model.Despesa;
import br.com.controle_despesas.model.Receita;
import br.com.controle_despesas.model.Relatorio;
import br.com.controle_despesas.repository.CategoriaRepository;
import br.com.controle_despesas.repository.DespesaRepository;
import br.com.controle_despesas.repository.ReceitaRepository;
import br.com.controle_despesas.util.RelatorioExcelExporter;

@Service
public class RelatorioService {

    private final ReceitaRepository receitaRepository;
    private final DespesaRepository despesaRepository;
    private final CategoriaRepository categoriaRepository;

    public RelatorioService(ReceitaRepository receitaRepository,
            DespesaRepository despesaRepository,
            CategoriaRepository categoriaRepository) {
        this.receitaRepository = receitaRepository;
        this.despesaRepository = despesaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Relatorio> gerarRelatorio() {
        List<Relatorio> relatorios = new ArrayList<>();

        // ===== RECEITAS =====
        List<Receita> receitas = receitaRepository.findAll();
        for (Receita r : receitas) {
            String nomeCategoria = categoriaRepository.findById(r.getIdCategoria())
                    .map(Categoria::getNome)
                    .orElse("Sem categoria");

            Relatorio rel = new Relatorio(
                    r.getDescricao(),
                    r.getValor(),
                    r.getDataReceita(),
                    "receita",
                    nomeCategoria,
                    r.getRecebimento() // do model Receita
            );

            relatorios.add(rel);
        }

        // ===== DESPESAS =====
        List<Despesa> despesas = despesaRepository.findAll();
        for (Despesa d : despesas) {
            String nomeCategoria = categoriaRepository.findById(d.getIdCategoria())
                    .map(Categoria::getNome)
                    .orElse("Sem categoria");

            Relatorio rel = new Relatorio(
                    d.getDescricao(),
                    d.getValor(),
                    d.getDataDespesa(),
                    "despesa",
                    nomeCategoria,
                    d.getPagamento() // do model Despesa
            );

            relatorios.add(rel); // não esquecer de adicionar na lista final
        }

        return relatorios;
    }

    public ByteArrayInputStream gerarExcel() {
        try {
            List<Relatorio> relatorios = gerarRelatorio();
            return RelatorioExcelExporter.exportarParaExcel(relatorios);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatório em Excel: " + e.getMessage(), e);
        }
    }

}
