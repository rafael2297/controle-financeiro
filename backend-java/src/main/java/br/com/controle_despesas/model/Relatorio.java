package br.com.controle_despesas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relatorio {

    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private String tipo;
    private String nomeCategoria;
    private String pagamento; 
}
