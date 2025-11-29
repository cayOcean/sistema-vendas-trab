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

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.service.ClienteService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("clientes", this.clienteService.listarTodos());
        return "cliente/list";
    }

    @GetMapping("/novo")
    public String create(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/form";
    }

    @PostMapping("/novo")
    public String create(@Valid Cliente cliente, BindingResult result) {
        if (result.hasErrors()) {
            return "cliente/form";
        }

        try {
            this.clienteService.salvar(cliente);
        } catch (IllegalArgumentException e) {
            tratarErroValidacao(e, result);
            return "cliente/form";
        }

        return "redirect:/clientes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Cliente clienteEncontrado = this.clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        model.addAttribute("cliente", clienteEncontrado);
        return "cliente/form";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid Cliente cliente, BindingResult result) {
        if (result.hasErrors()) {
            return "cliente/form";
        }

        try {
            this.clienteService.atualizar(id, cliente);
        } catch (IllegalArgumentException e) {
            tratarErroValidacao(e, result);
            return "cliente/form";
        }

        return "redirect:/clientes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        Cliente clienteParaExcluir = this.clienteService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("cliente", clienteParaExcluir);
        return "cliente/delete";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        this.clienteService.excluir(id);
        return "redirect:/clientes";
    }

    private void tratarErroValidacao(IllegalArgumentException e, BindingResult result) {
        String mensagem = e.getMessage();
        if (mensagem.contains("CPF")) {
            result.rejectValue("cpf", "error.cliente", mensagem);
        } else if (mensagem.contains("E-mail")) {
            result.rejectValue("email", "error.cliente", mensagem);
        }
    }
}