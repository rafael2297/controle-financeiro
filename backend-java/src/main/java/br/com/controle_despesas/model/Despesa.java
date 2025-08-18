package br.com.controle_despesas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_despesas")
@Getter
@Setter
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_despesa")
    private Long id; // 🔹 Alterado de Integer para Long

    private String descricao;

    private BigDecimal valor;

    @Column(name = "data_despesa")
    private LocalDate dataDespesa;

    @Column(name = "id_categoria")
    private Long idCategoria; // 🔹 também melhor usar Long para bater com Categoria.id

    private String pagamento;
}
