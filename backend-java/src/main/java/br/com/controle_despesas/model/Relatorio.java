package br.com.controle_despesas.model;

import java.math.BigDecimal;
import java.sql.Date;

public class Relatorio {

    private int id;
    private BigDecimal valor;
    private Date data;
    private String descricao;
    private int idCategoria;
    private String nomeCategoria;
    private String pagamento;

    public Relatorio() {}

    // Construtor usado na query JPQL
    public Relatorio(int id, BigDecimal valor, Date data, String descricao, int idCategoria, String nomeCategoria, String pagamento) {
        this.id = id;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.idCategoria = idCategoria;
        this.nomeCategoria = nomeCategoria;
        this.pagamento = pagamento;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    public String getPagamento() { return pagamento; }
    public void setPagamento(String pagamento) { this.pagamento = pagamento; }

    @Override
    public String toString() {
        return "Relatorio{" +
                "id=" + id +
                ", valor=" + valor +
                ", data=" + data +
                ", descricao='" + descricao + '\'' +
                ", idCategoria=" + idCategoria +
                ", nomeCategoria='" + nomeCategoria + '\'' +
                ", pagamento='" + pagamento + '\'' +
                '}';
    }
}
