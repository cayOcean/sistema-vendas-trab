package br.com.aweb.sistema_vendas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import br.com.aweb.sistema_vendas.model.Produto;
import br.com.aweb.sistema_vendas.service.ProdutoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    // Mudança 1: Injeção via Construtor (elimina o @Autowired no atributo)
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // Listar produtos
    // Mudança 2: Uso de 'Model' e retorno 'String' em vez de 'ModelAndView'
    @GetMapping
    public String list(Model model) {
        model.addAttribute("produtos", this.produtoService.listarTodos());
        return "produto/list";
    }

    // Formulário de cadastro
    @GetMapping("/novo")
    public String create(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/form";
    }

    // Salvar produto
    @PostMapping("/novo")
    public String create(@Valid Produto produto, BindingResult result) {
        if (result.hasErrors()) {
            return "produto/form";
        }
        
        this.produtoService.salvar(produto);
        return "redirect:/produtos";
    }

    // Formulário de edição
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Produto produtoEncontrado = this.produtoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));

        model.addAttribute("produto", produtoEncontrado);
        return "produto/form";
    }

    // Atualizar produto
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid Produto produto, BindingResult result) {
        if (result.hasErrors()) {
            return "produto/form";
        }

        this.produtoService.atualizar(id, produto);
        return "redirect:/produtos";
    }

    // Excluir produto (Tela de confirmação)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Produto produtoParaExcluir = this.produtoService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("produto", produtoParaExcluir);
        return "produto/delete";
    }

    // Ação de excluir
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        this.produtoService.excluir(id);
        return "redirect:/produtos";
    }
}