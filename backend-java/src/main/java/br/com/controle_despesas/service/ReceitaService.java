package br.com.controle_despesas.service;

import br.com.controle_despesas.model.Receita;
import br.com.controle_despesas.repository.ReceitaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceitaService {

    private final ReceitaRepository repository;

    public ReceitaService(ReceitaRepository repository) {
        this.repository = repository;
    }

    public List<Receita> listarTodas() {
        return repository.findAll();
    }

    public Receita salvar(Receita receita) {
        return repository.save(receita);
    }

    public Receita atualizar(Long id, Receita receita) {
        receita.setId(id);
        return repository.save(receita);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
