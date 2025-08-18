package br.com.controle_despesas.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.controle_despesas.DTO.ReceitaDTO;
import br.com.controle_despesas.model.Categoria;
import br.com.controle_despesas.model.Receita;
import br.com.controle_despesas.repository.CategoriaRepository;
import br.com.controle_despesas.repository.ReceitaRepository;

@Service
public class ReceitaService {

    private final ReceitaRepository repository;
    private final CategoriaRepository categoriaRepository;

    public ReceitaService(ReceitaRepository repository, CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ReceitaDTO> listarTodas() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ReceitaDTO salvar(ReceitaDTO dto) {
        Receita receita = toEntity(dto);
        Receita saved = repository.save(receita);
        return toDTO(saved);
    }

    public ReceitaDTO atualizar(Long id, ReceitaDTO dto) {
        Receita receita = toEntity(dto);
        receita.setId(id);
        Receita updated = repository.save(receita);
        return toDTO(updated);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // 🔹 Conversão para DTO
    private ReceitaDTO toDTO(Receita receita) {
        return new ReceitaDTO(
                receita.getId().intValue(),
                receita.getDescricao(),
                receita.getValor(),
                receita.getData(),
                receita.getCategoria() != null ? receita.getCategoria().getId().intValue() : null,
                receita.getCategoria() != null ? receita.getCategoria().getNome() : null,
                receita.getPagamento());
    }

    // 🔹 Conversão para Entidade
    private Receita toEntity(ReceitaDTO dto) {
        Receita receita = new Receita();
        receita.setId(dto.id() != null ? dto.id().longValue() : null);
        receita.setDescricao(dto.descricao());
        receita.setValor(dto.valor());
        receita.setData(dto.data());
        receita.setPagamento(dto.pagamento());

        if (dto.idCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(dto.idCategoria().longValue())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            receita.setCategoria(categoria);
        }

        return receita;
    }
}
