package br.com.aweb.sistema_vendas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.aweb.sistema_vendas.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}