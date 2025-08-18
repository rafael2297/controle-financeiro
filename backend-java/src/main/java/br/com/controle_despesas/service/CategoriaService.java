package br.com.controle_despesas.service;

import br.com.controle_despesas.model.Categoria;
import br.com.controle_despesas.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }

    public Categoria salvar(Categoria categoria) {
        return repository.save(categoria);
    }

    public Categoria atualizar(Long id, Categoria categoria) {
        categoria.setId(id);
        return repository.save(categoria);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
