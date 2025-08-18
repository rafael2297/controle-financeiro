package br.com.controle_despesas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.controle_despesas.model.Saldo;

public interface SaldoRepository extends JpaRepository<Saldo, Long> {
    Optional<Saldo> findTopByOrderByIdDesc();
}
