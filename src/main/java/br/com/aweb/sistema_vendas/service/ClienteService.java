package br.com.aweb.sistema_vendas.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.aweb.sistema_vendas.model.Cliente;
import br.com.aweb.sistema_vendas.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (this.clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        if (this.clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        return this.clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return this.clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return this.clienteRepository.findById(id);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = this.clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));

        if (!clienteExistente.getEmail().equals(clienteAtualizado.getEmail()) && 
            this.clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        if (!clienteExistente.getCpf().equals(clienteAtualizado.getCpf()) && 
            this.clienteRepository.existsByCpf(clienteAtualizado.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        atualizarDadosCliente(clienteExistente, clienteAtualizado);

        return this.clienteRepository.save(clienteExistente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!this.clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        this.clienteRepository.deleteById(id);
    }

    private void atualizarDadosCliente(Cliente destino, Cliente origem) {
        destino.setNome(origem.getNome());
        destino.setEmail(origem.getEmail());
        destino.setCpf(origem.getCpf());
        destino.setTelefone(origem.getTelefone());
        destino.setLogradouro(origem.getLogradouro());
        destino.setNumero(origem.getNumero());
        destino.setComplemento(origem.getComplemento());
        destino.setBairro(origem.getBairro());
        destino.setCidade(origem.getCidade());
        destino.setUf(origem.getUf());
        destino.setCep(origem.getCep());
    }
}