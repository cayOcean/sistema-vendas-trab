package br.com.aweb.sistema_vendas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto salvar(Produto produto) {
        return this.produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return this.produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return this.produtoRepository.findById(id);
    }

    @Transactional
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = this.produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setQuantidadeEmEstoque(produtoAtualizado.getQuantidadeEmEstoque());

        return this.produtoRepository.save(produtoExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!this.produtoRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado.");
        }
        this.produtoRepository.deleteById(id);
    }
}