package br.com.controle_despesas.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.controle_despesas.model.Receita;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    @Query("SELECT SUM(r.valor) FROM Receita r")
    BigDecimal sumAllReceitas();

    @Query(value = "SELECT DATE_FORMAT(data_receita, '%Y-%m') AS mes, SUM(valor) FROM tb_receitas GROUP BY mes", nativeQuery = true)
    List<Object[]> sumReceitasPorMes();
}
