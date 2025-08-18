package br.com.controle_despesas.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.controle_despesas.model.Saldo;
import br.com.controle_despesas.service.SaldoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/saldos")
@RequiredArgsConstructor
public class SaldoController {

    private final SaldoService saldoService;

    @PostMapping
    public ResponseEntity<Saldo> adicionar(@RequestBody Saldo saldo) {
        Saldo novoSaldo = saldoService.salvar(saldo);
        return ResponseEntity.ok(novoSaldo);
    }

    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeSaldo() {
        return ResponseEntity.ok(saldoService.existeSaldo());
    }

    @GetMapping("/atual")
    public ResponseEntity<BigDecimal> getSaldoAtual() {
        return ResponseEntity.ok(saldoService.buscarSaldoAtual());
    }
}
