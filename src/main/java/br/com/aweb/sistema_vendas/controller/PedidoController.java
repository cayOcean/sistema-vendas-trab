package br.com.aweb.sistema_vendas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.model.Pedido;
import br.com.aweb.sistema_vendas.model.StatusPedido;
import br.com.aweb.sistema_vendas.service.ClienteService;
import br.com.aweb.sistema_vendas.service.PedidoService;
import br.com.aweb.sistema_vendas.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoController(PedidoService pedidoService, ClienteService clienteService, ProdutoService produtoService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", this.pedidoService.listarTodos());
        return "pedido/list";
    }

    @GetMapping("/novo")
    public String novoPedidoForm(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", this.clienteService.listarTodos());
        model.addAttribute("produtos", this.produtoService.listarTodos());
        return "pedido/form";
    }

    @PostMapping("/novo")
    public String criarPedido(@RequestParam Long clienteId) {
        Cliente clienteEncontrado = this.clienteService.buscarPorId(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        Pedido novoPedido = this.pedidoService.criarPedido(clienteEncontrado);
        return "redirect:/pedidos/edit/" + novoPedido.getId();
    }

    @GetMapping("/edit/{id}")
    public String editarPedidoForm(@PathVariable Long id, Model model) {
        Pedido pedido = this.pedidoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (StatusPedido.CANCELADO.equals(pedido.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido cancelado não pode ser editado");
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("produtos", this.produtoService.listarTodos());

        // Mantive o nome da view original que você passou ('pedido/edit')
        // Se quiser reutilizar o form de criação, mude para 'pedido/form'
        return "pedido/edit";
    }

    @PostMapping("/{pedidoId}/adicionar-item")
    public String adicionarItem(@PathVariable Long pedidoId,
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade) {
        try {
            this.pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
            return "redirect:/pedidos/edit/" + pedidoId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{pedidoId}/remover-item/{itemId}")
    public String removerItem(@PathVariable Long pedidoId, @PathVariable Long itemId) {
        try {
            this.pedidoService.removerItem(pedidoId, itemId);
            return "redirect:/pedidos/edit/" + pedidoId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarPedido(@PathVariable Long id) {
        this.pedidoService.finalizarPedido(id);
        return "redirect:/pedidos";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelarPedidoForm(@PathVariable Long id, Model model) {
        Pedido pedido = this.pedidoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("pedido", pedido);
        return "pedido/cancelar";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarPedido(@PathVariable Long id) {
        try {
            this.pedidoService.cancelarPedido(id);
            return "redirect:/pedidos";
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/detalhes/{id}")
    public String detalhesPedido(@PathVariable Long id, Model model) {
        Pedido pedido = this.pedidoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("pedido", pedido);
        return "pedido/detalhes";
    }
}