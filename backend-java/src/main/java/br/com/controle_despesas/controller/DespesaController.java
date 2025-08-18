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

import br.com.controle_despesas.model.Despesa;
import br.com.controle_despesas.service.DespesaService;

@RestController
@RequestMapping("/api/despesas")
@CrossOrigin(origins = "*")
public class DespesaController {

    private final DespesaService service;

    public DespesaController(DespesaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Despesa> listar() {
        return service.listarTodas();
    }

    @PostMapping
    public Despesa adicionar(@RequestBody Despesa despesa) {
        return service.salvar(despesa);
    }

    @PutMapping("/{id}")
    public Despesa alterar(@PathVariable Long id, @RequestBody Despesa despesa) { // 🔹 Long
        return service.atualizar(id, despesa);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) { // 🔹 Long
        service.deletar(id);
    }
}
