package br.com.controle_despesas.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReceitaDTO(
        Integer id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        Integer idCategoria,
        String nomeCategoria,
        String pagamento) {
}
