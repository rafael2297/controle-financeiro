package br.com.controle_despesas.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saldos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Saldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // saldo inicial do sistema
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorInicial;

    // saldo atual do sistema
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAtual;
}
