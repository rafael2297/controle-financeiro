package br.com.controle_despesas.controller;

import br.com.controle_despesas.model.Categoria;
import br.com.controle_despesas.service.CategoriaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Categoria> listar() {
        return service.listarTodas();
    }

    @PostMapping
    public Categoria adicionar(@RequestBody Categoria categoria) {
        return service.salvar(categoria);
    }

    @PutMapping("/{id}")
    public Categoria alterar(@PathVariable Long id, @RequestBody Categoria categoria) {
        return service.atualizar(id, categoria);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
