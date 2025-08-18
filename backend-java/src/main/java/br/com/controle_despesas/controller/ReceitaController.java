package br.com.controle_despesas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.controle_despesas.DTO.ReceitaDTO;
import br.com.controle_despesas.service.ReceitaService;

@RestController
@RequestMapping("/api/receitas")
@CrossOrigin(origins = "*")
public class ReceitaController {

    private final ReceitaService service;

    public ReceitaController(ReceitaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReceitaDTO> listar() {
        return service.listarTodas();
    }

    @PostMapping
    public ReceitaDTO adicionar(@RequestBody ReceitaDTO receita) {
        return service.salvar(receita);
    }

    @PutMapping("/{id}")
    public ReceitaDTO alterar(@PathVariable Long id, @RequestBody ReceitaDTO receita) {
        return service.atualizar(id, receita);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
