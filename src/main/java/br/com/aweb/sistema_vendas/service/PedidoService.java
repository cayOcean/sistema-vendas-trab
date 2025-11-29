package br.com.aweb.sistema_vendas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.model.ItemPedido;
import br.com.aweb.sistema_vendas.model.Pedido;
import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.model.StatusPedido;
import br.com.aweb.sistema_vendas.repository.PedidoRepository;
import br.com.aweb.sistema_vendas.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteService clienteService; // Ajuste na convenção de nome (camelCase)

    @Transactional
    public Pedido criarPedido(Cliente cliente) {
        return pedidoRepository.save(new Pedido(cliente));
    }

    @Transactional
    public void adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        validarStatusPedido(pedido);

        if (produto.getQuantidadeEmEstoque() < quantidade) {
            throw new IllegalStateException("Quantidade insuficiente para o produto: " + produto.getNome());
        }

        ItemPedido novoItem = new ItemPedido(produto, quantidade);
        novoItem.setPedido(pedido);

        pedido.getItens().add(novoItem);

        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() - quantidade);

        atualizarValorTotal(pedido);

        pedidoRepository.save(pedido);
        produtoRepository.save(produto);
    }

    @Transactional
    public void removerItem(Long pedidoId, Long itemId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        validarStatusPedido(pedido);

        ItemPedido itemParaRemover = pedido.getItens().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado no pedido"));

        Produto produto = itemParaRemover.getProduto();
        produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + itemParaRemover.getQuantidade());

        pedido.getItens().remove(itemParaRemover);

        atualizarValorTotal(pedido);

        pedidoRepository.save(pedido);
        produtoRepository.save(produto);
    }

    @Transactional
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        pedido.getItens().forEach(item -> {
            Produto produto = item.getProduto();
            produto.setQuantidadeEmEstoque(produto.getQuantidadeEmEstoque() + item.getQuantidade());
            produtoRepository.save(produto);
        });

        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void finalizarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (StatusPedido.CANCELADO.equals(pedido.getStatus())) {
            throw new IllegalStateException("Não é possível finalizar um pedido cancelado");
        }

        if (pedido.getItens().isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar um pedido sem itens");
        }

        atualizarValorTotal(pedido);
        pedido.setStatus(StatusPedido.FINALIZADO);
        pedidoRepository.save(pedido);
    }

    private void atualizarValorTotal(Pedido pedido) {
        BigDecimal totalCalculado = pedido.getItens().stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(totalCalculado);
    }

    private void validarStatusPedido(Pedido pedido) {
        if (!StatusPedido.ATIVO.equals(pedido.getStatus())) {
            throw new IllegalStateException("Não é possível alterar pedido cancelado");
        }
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }
}