package br.com.controle_despesas.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.model.Saldo;
import br.com.controle_despesas.repository.DespesaRepository;
import br.com.controle_despesas.repository.ReceitaRepository;
import br.com.controle_despesas.repository.SaldoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaldoService {

    private final SaldoRepository saldoRepository;
    private final DespesaRepository despesaRepository;
    private final ReceitaRepository receitaRepository;

    public Saldo salvar(Saldo saldo) {
        // valorAtual no momento da criação é só o inicial
        saldo.setValorAtual(saldo.getValorInicial());
        return saldoRepository.save(saldo);
    }

    public boolean existeSaldo() {
        return saldoRepository.count() > 0;
    }

    /**
     * Busca o saldo atualizado em tempo real
     */
    public BigDecimal buscarSaldoAtual() {
        BigDecimal saldoInicial = saldoRepository.findTopByOrderByIdDesc()
                .map(Saldo::getValorInicial)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalDespesas = despesaRepository.sumAllDespesas();
        BigDecimal totalReceitas = receitaRepository.sumAllReceitas();

        if (totalDespesas == null)
            totalDespesas = BigDecimal.ZERO;
        if (totalReceitas == null)
            totalReceitas = BigDecimal.ZERO;

        return saldoInicial.add(totalReceitas).subtract(totalDespesas);
    }
}
