package br.com.controle_despesas.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.model.Saldo;
import br.com.controle_despesas.repository.SaldoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaldoService {

    private final SaldoRepository saldoRepository;

    public Saldo salvar(Saldo saldo) {
        // sempre que criar um saldo novo, valorAtual = valorInicial
        saldo.setValorAtual(saldo.getValorInicial());
        return saldoRepository.save(saldo);
    }

    public boolean existeSaldo() {
        return saldoRepository.count() > 0;
    }

    public BigDecimal buscarSaldoAtual() {
        return saldoRepository.findTopByOrderByIdDesc()
                .map(Saldo::getValorAtual)
                .orElse(BigDecimal.ZERO);
    }
}
