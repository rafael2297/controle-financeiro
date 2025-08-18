package br.com.controle_despesas.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaDTO(
    Integer id,
    String descricao,
    BigDecimal valor,
    LocalDate dataDespesa,
    Integer idCategoria,
    String nomeCategoria,
    String pagamento
) {}
