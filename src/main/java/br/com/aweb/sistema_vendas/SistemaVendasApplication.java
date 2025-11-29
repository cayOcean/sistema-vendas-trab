package br.com.aweb.sistema_vendas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaVendasApplication {

    public static void main(String[] args) {
        // MUDANÇA DE SINTAXE:
        // O original usa "SpringApplication.run(...)".
        // Aqui, instanciamos o objeto "app" primeiro.
        // O resultado final é idêntico, mas a escrita do código muda.
        SpringApplication app = new SpringApplication(SistemaVendasApplication.class);
        app.run(args);
    }

}