package br.com.controle_despesas.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.model.Despesa;
import br.com.controle_despesas.repository.DespesaRepository;

@Service
public class DespesaService {

    private final DespesaRepository repository;

    public DespesaService(DespesaRepository repository) {
        this.repository = repository;
    }

    public List<Despesa> listarTodas() {
        return repository.findAll();
    }

    public Despesa salvar(Despesa despesa) {
        return repository.save(despesa);
    }

    public Despesa atualizar(Long id, Despesa despesa) { // 🔹 Long
        despesa.setId(id);
        return repository.save(despesa);
    }

    public void deletar(Long id) { // 🔹 Long
        repository.deleteById(id);
    }
}

