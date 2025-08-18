package br.com.controle_despesas.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.controle_despesas.model.Despesa;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> { // 🔹 Agora usa Long

    @Query(value = """
            SELECT
                d.id_despesa AS id,
                d.valor AS valor,
                d.data_despesa AS data,
                d.descricao AS descricao,
                c.id AS idCategoria,
                c.nome AS nomeCategoria,
                d.pagamento AS pagamento
            FROM tb_despesas d
            INNER JOIN categorias c ON d.id_categoria = c.id
            """, nativeQuery = true)
    List<Object[]> gerarRelatorioBruto();

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM Despesa d")
    BigDecimal sumAllDespesas();

    @Query(value = "SELECT DATE_FORMAT(data_despesa, '%Y-%m') AS mes, SUM(valor) FROM tb_despesas GROUP BY mes", nativeQuery = true)
    List<Object[]> sumDespesasPorMes();
}
