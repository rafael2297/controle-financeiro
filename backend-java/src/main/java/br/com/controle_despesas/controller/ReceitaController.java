package br.com.controle_despesas.controller;

import br.com.controle_despesas.model.Receita;
import br.com.controle_despesas.service.ReceitaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receitas")
@CrossOrigin(origins = "*")
public class ReceitaController {

    private final ReceitaService service;

    public ReceitaController(ReceitaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Receita> listar() {
        return service.listarTodas();
    }

    @PostMapping
    public Receita adicionar(@RequestBody Receita receita) {
        return service.salvar(receita);
    }

    @PutMapping("/{id}")
    public Receita alterar(@PathVariable Long id, @RequestBody Receita receita) {
        return service.atualizar(id, receita);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
